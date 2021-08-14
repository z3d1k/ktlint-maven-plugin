package com.github.z3d1k.maven.plugin.ktlint.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.associateBy
import com.github.z3d1k.maven.plugin.ktlint.utils.map
import com.pinterest.ktlint.core.LintError
import org.apache.maven.plugin.logging.Log
import org.w3c.dom.Element
import java.io.File
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
    return runCatching { parseBaselineFile(baselineFile) }
        .onSuccess { log.info("Using baseline ${baselineFile.canonicalPath}") }
        .onFailure { log.warn("Unable to load baseline: ${it.message}") }
        .getOrDefault(Baseline())
}

/**
 * Based on baseline file parsing implementation from ktlint cli.
 * [https://github.com/pinterest/ktlint/blob/0.42.1/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/internal/BaselineSupport.kt#L53]
 */
internal fun parseBaselineFile(baselineFile: File): Baseline {
    val builderFactory = DocumentBuilderFactory.newInstance()
    val docBuilder = builderFactory.newDocumentBuilder()
    val doc = docBuilder.parse(baselineFile)
    val filesList = doc.getElementsByTagName("file")
    val rules = filesList.associateBy(
        { fileElement -> fileElement.getAttribute("name") },
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
