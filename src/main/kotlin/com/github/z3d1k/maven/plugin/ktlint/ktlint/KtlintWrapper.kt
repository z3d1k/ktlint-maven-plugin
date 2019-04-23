package com.github.z3d1k.maven.plugin.ktlint.ktlint

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.Reporter
import com.pinterest.ktlint.core.RuleSet
import com.github.z3d1k.maven.plugin.ktlint.rules.resolveRuleSets
import org.jetbrains.kotlin.backend.common.push
import java.io.File

typealias FormatFunction = (String, Iterable<RuleSet>, Map<String, String>, (LintError, Boolean) -> Unit) -> String

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
    userProperties: Map<String, String> = emptyMap()
): LintSummary {
    val filePath = file.toRelativeString(baseDir)
    reporter.before(filePath)
    val eventList = mutableListOf<LintError>()
    KtLint.lint(file.readText(), resolveRuleSets(enableExperimentalRules), userProperties) { error ->
        eventList.push(error)
        reporter.onLintError(filePath, error, false)
    }
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
    val filePath = file.toRelativeString(base)
    val sourceText = file.readText()
    val formatFunc: FormatFunction = when (file.extension) {
        "kt" -> KtLint::format
        "kts" -> KtLint::formatScript
        else -> {
            return FormatSummary()
        }
    }
    val formattedSource =
        formatFunc(sourceText, resolveRuleSets(enableExperimentalRules), userProperties) { lintError, corrected ->
            reporter.onLintError(filePath, lintError, corrected)
        }
    val isFormatted = formattedSource !== sourceText
    if (isFormatted) {
        file.writeText(formattedSource)
    }
    return FormatSummary(1, if (isFormatted) 1 else 0)
}
