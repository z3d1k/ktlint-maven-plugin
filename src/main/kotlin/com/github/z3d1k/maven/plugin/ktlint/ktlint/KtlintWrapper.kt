package com.github.z3d1k.maven.plugin.ktlint.ktlint

import com.github.z3d1k.maven.plugin.ktlint.rules.resolveRuleSets
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

fun lintFile(
    reporter: Reporter,
    baseDir: File,
    file: File,
    enableExperimentalRules: Boolean,
    baseline: Baseline = Baseline(),
    userProperties: Map<String, String> = emptyMap()
): LintSummary {
    val filePath = file.toRelativeString(baseDir)
    reporter.before(filePath)
    val eventList = mutableListOf<LintError>()
    KtLint.lint(
        KtLint.Params(
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
    )
    reporter.after(filePath)
    return LintSummary(1, if (eventList.isEmpty()) 0 else 1, eventList.size)
}

fun formatFile(
    reporter: Reporter,
    base: File,
    file: File,
    enableExperimentalRules: Boolean,
    userProperties: Map<String, String> = emptyMap()
): FormatSummary {
    if (!listOf("kt", "kts").contains(file.extension.toLowerCase())) {
        return FormatSummary()
    }
    val filePath = file.toRelativeString(base)
    val sourceText = file.readText()
    val formattedSource = KtLint.format(
        KtLint.Params(
            fileName = file.canonicalPath,
            text = sourceText,
            ruleSets = resolveRuleSets(enableExperimentalRules),
            userData = userProperties,
            script = file.extension.equals("kts", ignoreCase = true),
            cb = { lintError, corrected -> reporter.onLintError(filePath, lintError, corrected) }
        )
    )
    val isFormatted = formattedSource !== sourceText
    if (isFormatted) {
        file.writeText(formattedSource)
    }
    return FormatSummary(1, if (isFormatted) 1 else 0)
}
