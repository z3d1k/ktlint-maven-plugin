package com.github.z3d1k.maven.plugin.ktlint.utils

import com.github.z3d1k.maven.plugin.ktlint.ktlint.Baseline
import com.github.z3d1k.maven.plugin.ktlint.ktlint.FormatSummary
import com.github.z3d1k.maven.plugin.ktlint.ktlint.LintSummary
import com.github.z3d1k.maven.plugin.ktlint.ktlint.formatFile
import com.github.z3d1k.maven.plugin.ktlint.ktlint.initKtLintRuleEngine
import com.github.z3d1k.maven.plugin.ktlint.ktlint.lintFile
import com.pinterest.ktlint.core.Reporter
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.utils.io.FileUtils
import java.io.File
import java.nio.file.Path

fun MavenProject.getSourceFiles(include: String, exclude: String?): List<Path> {
    return FileUtils
        .getFiles(basedir, include, exclude, true)
        .map(File::toPath)
}

fun MavenProject.lintFiles(
    include: String,
    exclude: String?,
    reporter: Reporter,
    enableExperimentalRules: Boolean,
    baseline: Baseline = Baseline()
): LintSummary {
    val ruleEngine = initKtLintRuleEngine(enableExperimentalRules)
    return getSourceFiles(include, exclude)
        .fold(LintSummary()) { summary, file ->
            summary + lintFile(ruleEngine, reporter, basedir.toPath(), file, baseline)
        }
}

fun MavenProject.formatFiles(
    include: String,
    exclude: String?,
    reporter: Reporter,
    enableExperimentalRules: Boolean
): FormatSummary {
    val ruleEngine = initKtLintRuleEngine(enableExperimentalRules)
    return getSourceFiles(include, exclude)
        .fold(FormatSummary()) { summary, file ->
            summary + formatFile(ruleEngine, reporter, basedir.toPath(), file)
        }
}

fun String.invariantSeparatorPathString(separator: Char = File.separatorChar): String {
    return if (separator != '/') {
        replace(separator, '/')
    } else {
        this
    }
}
