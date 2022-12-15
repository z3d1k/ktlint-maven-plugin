package com.github.z3d1k.maven.plugin.ktlint.ktlint

import com.github.z3d1k.maven.plugin.ktlint.reports.forFile
import com.github.z3d1k.maven.plugin.ktlint.rules.resolveRuleProviders
import com.pinterest.ktlint.core.KtLintRuleEngine
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.Reporter
import com.pinterest.ktlint.core.api.EditorConfigDefaults
import com.pinterest.ktlint.core.api.EditorConfigOverride
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.io.path.writeText

data class LintSummary(val files: Int = 0, val filesWithErrors: Int = 0, val errors: Int = 0) {
    val hasErrors: Boolean by lazy { errors > 0 }

    operator fun plus(lintSummary: LintSummary): LintSummary {
        return LintSummary(
            files + lintSummary.files,
            filesWithErrors + lintSummary.filesWithErrors,
            errors + lintSummary.errors,
        )
    }
}

data class FormatSummary(val files: Int = 0, val correctedFiles: Int = 0) {
    operator fun plus(formatSummary: FormatSummary): FormatSummary {
        return FormatSummary(
            files + formatSummary.files,
            correctedFiles + formatSummary.correctedFiles,
        )
    }
}

private fun createParsingError(throwable: Throwable) = LintError(
    line = -1,
    col = -1,
    ruleId = "File processing error: ${throwable.javaClass.simpleName}",
    detail = throwable.message ?: "Unable to process file",
    canBeAutoCorrected = false,
)

fun initKtLintRuleEngine(
    enableExperimentalRules: Boolean = false,
    editorConfigDefaults: EditorConfigDefaults = EditorConfigDefaults.EMPTY_EDITOR_CONFIG_DEFAULTS,
    editorConfigOverrides: EditorConfigOverride = EditorConfigOverride.EMPTY_EDITOR_CONFIG_OVERRIDE,
) = KtLintRuleEngine(
    resolveRuleProviders(enableExperimentalRules),
    editorConfigDefaults,
    editorConfigOverrides,
)

fun lintFile(
    ruleEngine: KtLintRuleEngine,
    reporter: Reporter,
    baseDir: Path,
    file: Path,
    baseline: Baseline,
): LintSummary {
    return reporter.forFile(file.relativeTo(baseDir).pathString) { _, filePath ->
        val eventList = mutableListOf<LintError>()
        runCatching {
            ruleEngine.lint(file.readText(), file) { error ->
                if (!baseline.containsError(filePath, error)) {
                    eventList.add(error)
                    reporter.onLintError(filePath, error, false)
                }
            }
        }.onFailure { e ->
            val error = createParsingError(e)
            eventList.add(error)
            reporter.onLintError(filePath, error, false)
        }

        LintSummary(1, if (eventList.isEmpty()) 0 else 1, eventList.size)
    }
}

fun formatFile(
    ruleEngine: KtLintRuleEngine,
    reporter: Reporter,
    baseDir: Path,
    file: Path,
): FormatSummary {
    if (file.extension.lowercase() !in listOf("kt", "kts")) {
        return FormatSummary()
    }
    return reporter.forFile(file.relativeTo(baseDir).pathString) { _, filePath ->
        val sourceText = file.readText()
        val formattedSource = runCatching {
            ruleEngine.format(sourceText, file) { error, corrected ->
                reporter.onLintError(filePath, error, corrected)
            }
        }
            .onFailure { e -> reporter.onLintError(filePath, createParsingError(e), false) }
            .getOrDefault(sourceText)
        val isFormatted = formattedSource != sourceText
        if (isFormatted) {
            file.writeText(formattedSource)
        }
        FormatSummary(1, if (isFormatted) 1 else 0)
    }
}
