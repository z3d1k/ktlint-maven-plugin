package com.github.z3d1k.maven.plugin.ktlint.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.invariantSeparatorPathString
import com.pinterest.ktlint.core.LintError
import org.apache.maven.plugin.logging.Log
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

data class Baseline(val rules: Map<String, List<LintError>> = emptyMap())

fun Baseline.containsError(fileName: String, error: LintError): Boolean {
    return rules[fileName]
        ?.any { lintError ->
            lintError.col == error.col &&
                lintError.line == error.line &&
                lintError.ruleId == error.ruleId
        }
        ?: false
}

fun loadBaseline(log: Log, baselineFile: File?): Baseline {
    if (baselineFile == null) return Baseline()
    return runCatching { parseBaselineFile(baselineFile.inputStream()) }
        .onSuccess { log.info("Using baseline ${baselineFile.canonicalPath}") }
        .onFailure { log.warn("Unable to load baseline: ${it.message}") }
        .getOrDefault(Baseline())
}

/**
 * Based on baseline file parsing implementation from ktlint cli.
 * [https://github.com/pinterest/ktlint/blob/0.42.1/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/internal/BaselineSupport.kt#L53]
 */
internal fun parseBaselineFile(baselineFile: InputStream): Baseline {
    val builderFactory = DocumentBuilderFactory.newInstance()
    val docBuilder = builderFactory.newDocumentBuilder()
    val doc = docBuilder.parse(baselineFile)
    val filesList = doc.getElementsByTagName("file")
    val rules = filesList.associateBy(
        { fileElement -> fileElement.getAttribute("name").invariantSeparatorPathString() },
        { fileElement -> fileElement.getBaselineErrors() }
    )
    return Baseline(rules)
}

internal fun Element.getBaselineErrors(): List<LintError> {
    return getElementsByTagName("error").map { errorElement ->
        LintError(
            line = errorElement.getAttribute("line").toInt(),
            col = errorElement.getAttribute("column").toInt(),
            ruleId = errorElement.getAttribute("source"),
            detail = "" // we don't have details in the baseline file
        )
    }
}

internal fun <T> NodeList.map(transform: (Element) -> T): List<T> {
    return (0 until length).map { idx -> transform(item(idx) as Element) }
}

internal fun <K, V> NodeList.associateBy(keySelector: (Element) -> K, valueTransform: (Element) -> V): Map<K, V> {
    return (0 until length).associate { idx ->
        val item = item(idx) as Element
        keySelector(item) to valueTransform(item)
    }
}
