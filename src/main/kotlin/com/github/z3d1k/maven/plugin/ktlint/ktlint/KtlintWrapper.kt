package com.github.z3d1k.maven.plugin.ktlint.ktlint

import com.github.z3d1k.maven.plugin.ktlint.rules.resolveRuleSets
import com.github.z3d1k.maven.plugin.ktlint.utils.forFile
import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.Reporter
import java.io.File

data class LintSummary(val files: Int = 0, val filesWithErrors: Int = 0, val errors: Int = 0) {
    val hasErrors: Boolean by lazy { errors > 0 }

    operator fun plus(lintSummary: LintSummary): LintSummary {
        return LintSummary(
            files + lintSummary.files,
            filesWithErrors + lintSummary.filesWithErrors,
            errors + lintSummary.errors
        )
    }
}

data class FormatSummary(val files: Int = 0, val correctedFiles: Int = 0) {
    operator fun plus(formatSummary: FormatSummary): FormatSummary {
        return FormatSummary(
            files + formatSummary.files,
            correctedFiles + formatSummary.correctedFiles
        )
    }
}

private fun createParsingError(throwable: Throwable) = LintError(
    line = -1,
    col = -1,
    ruleId = "File processing error: ${throwable.javaClass.simpleName}",
    detail = throwable.message ?: "Unable to process file",
    canBeAutoCorrected = false
)

fun lintFile(
    reporter: Reporter,
    baseDir: File,
    file: File,
    enableExperimentalRules: Boolean,
    baseline: Baseline,
    userProperties: Map<String, String> = emptyMap()
): LintSummary {
    return reporter.forFile(file.toRelativeString(baseDir)) { _, filePath ->
        val eventList = mutableListOf<LintError>()
        val params = KtLint.Params(
            fileName = file.canonicalPath,
            text = file.readText(),
            ruleSets = resolveRuleSets(enableExperimentalRules),
            userData = userProperties,
            cb = { error, corrected ->
                if (!baseline.containsError(filePath, error)) {
                    eventList.add(error)
                    reporter.onLintError(filePath, error, corrected)
                }
            }
        )
        runCatching { KtLint.lint(params) }
            .onFailure { e ->
                val error = createParsingError(e)
                eventList.add(error)
                reporter.onLintError(filePath, error, false)
            }

        LintSummary(1, if (eventList.isEmpty()) 0 else 1, eventList.size)
    }
}

fun formatFile(
    reporter: Reporter,
    baseDir: File,
    file: File,
    enableExperimentalRules: Boolean,
    userProperties: Map<String, String> = emptyMap()
): FormatSummary {
    if (file.extension.lowercase() !in listOf("kt", "kts")) {
        return FormatSummary()
    }
    return reporter.forFile(file.toRelativeString(baseDir)) { _, filePath ->
        val sourceText = file.readText()
        val params = KtLint.Params(
            fileName = file.canonicalPath,
            text = sourceText,
            ruleSets = resolveRuleSets(enableExperimentalRules),
            userData = userProperties,
            script = file.extension.equals("kts", ignoreCase = true),
            cb = { lintError, corrected -> reporter.onLintError(filePath, lintError, corrected) }
        )
        val formattedSource = runCatching { KtLint.format(params) }
            .onFailure { e -> reporter.onLintError(filePath, createParsingError(e), false) }
            .getOrDefault(sourceText)
        val isFormatted = formattedSource != sourceText
        if (isFormatted) {
            file.writeText(formattedSource)
        }
        FormatSummary(1, if (isFormatted) 1 else 0)
    }
}
