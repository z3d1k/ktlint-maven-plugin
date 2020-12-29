package com.github.z3d1k.maven.plugin.ktlint.utils

import com.github.z3d1k.maven.plugin.ktlint.ktlint.Baseline
import com.github.z3d1k.maven.plugin.ktlint.ktlint.LintSummary
import com.github.z3d1k.maven.plugin.ktlint.ktlint.lintFile
import com.pinterest.ktlint.core.Reporter
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.utils.io.FileUtils
import org.jetbrains.kotlin.utils.identity
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File

fun MavenProject.getSourceFiles(include: String, exclude: String?): List<File> {
    return FileUtils.getFiles(basedir, include, exclude, true)
}

fun MavenProject.lintFiles(
    include: String,
    exclude: String?,
    reporter: Reporter,
    enableExperimentalRules: Boolean,
    baseline: Baseline = Baseline(),
): LintSummary {
    return getSourceFiles(include, exclude)
        .fold(LintSummary()) { summary, file ->
            summary + lintFile(reporter, basedir, file, enableExperimentalRules, baseline)
        }
}

internal fun <T> NodeList.map(transform: (Element) -> T): List<T> {
    return (0 until length).map { idx -> transform(item(idx) as Element) }
}

internal fun <K, V> NodeList.associateBy(keySelector: (Element) -> K, valueTransform: (Element) -> V): Map<K, V> {
    return map(identity()).associateBy(keySelector, valueTransform)
}
