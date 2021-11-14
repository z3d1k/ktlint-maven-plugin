package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.reports.ReporterParameters
import com.github.z3d1k.maven.plugin.ktlint.reports.generateReporter
import com.github.z3d1k.maven.plugin.ktlint.utils.forAll
import com.github.z3d1k.maven.plugin.ktlint.utils.forFile
import com.github.z3d1k.maven.plugin.ktlint.utils.normalizeLineEndings
import com.github.z3d1k.maven.plugin.ktlint.utils.withMockedSystemOut
import com.nhaarman.mockitokotlin2.mock
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.Reporter
import com.pinterest.ktlint.reporter.checkstyle.CheckStyleReporterProvider
import com.pinterest.ktlint.reporter.json.JsonReporterProvider
import com.pinterest.ktlint.reporter.plain.PlainReporterProvider
import org.apache.commons.lang3.RandomStringUtils
import org.apache.maven.plugin.logging.Log
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readText
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class ReportGeneratorTest {
    private val log = mock<Log>()

    @Test
    fun `test reporters from properties map`() = withMockedSystemOut { consoleOutputStream ->
        val tempPlainFile = Files.createTempFile("plain", ".txt")
        val tempJsonFile = Files.createTempFile("json", ".json")
        val tempCheckstyleFile = Files.createTempFile("checkstyle", ".xml")

        val reporterParametersMap = mapOf(
            // console (plain) reporter
            "console.verbose" to "true",
            "console.group_by_file" to "true",
            "console.color" to "false",
            // plain reporter
            "plain.output" to tempPlainFile.absolutePathString(),
            "plain.verbose" to "true",
            "plain.group_by_file" to "true",
            "plain.color" to "true",
            "plain.color_name" to "LIGHT_BLUE",
            // json reporter
            "json.output" to tempJsonFile.absolutePathString(),
            // checkstyle reporter
            "checkstyle.output" to tempCheckstyleFile.absolutePathString()
        )

        val reporterParams = ReporterParameters.fromParametersMap(reporterParametersMap)
        generateReporter(
            log,
            reporterParams,
            listOf(PlainReporterProvider(), JsonReporterProvider(), CheckStyleReporterProvider())
        ).generateReports(errorsMap)
        reporterParams.forEach { it.output.close() }

        val consoleOutput = consoleOutputStream.toString().trim()
        val plainColoredOutputString = tempPlainFile.readText().trim()
        val jsonOutputString = tempJsonFile.readText().trim()
        val checkstyleOutputString = tempCheckstyleFile.readText().trim()

        assertEquals(expectedPlainOutputGroupVerbose, consoleOutput, "Plain reporter output must match")
        assertEquals(
            expectedPlainOutputGroupVerboseColored,
            plainColoredOutputString,
            "Plain reporter colored output must match"
        )
        assertEquals(expectedJsonOutput, jsonOutputString, "Json reporter output must match")
        assertEquals(expectedCheckstyleOutput, checkstyleOutputString, "Checkstyle reporter output must match")

        listOf(tempPlainFile, tempJsonFile, tempCheckstyleFile).forEach { it.deleteIfExists() }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `report parameters parsing should fail without specified output`() {
        val parameters = mapOf("plain.verbose" to "true")
        ReporterParameters.fromParametersMap(parameters)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `report parameters parsing should fail when incorrectly specified`() {
        val parameters = mapOf("plain_verbose" to "true")
        ReporterParameters.fromParametersMap(parameters)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `report generation should fail if reporter name is unknown`() {
        val reporterParameters = ReporterParameters(
            name = RandomStringUtils.random(10),
            output = System.out
        )
        generateReporter(
            log,
            listOf(reporterParameters),
            listOf(PlainReporterProvider())
        )
    }

    private fun Reporter.generateReports(lintResults: Map<String, List<LintError>>) {
        forAll { reporter ->
            lintResults.forEach { (fileName, lintErrors) ->
                reporter.forFile(fileName) { _, file ->
                    lintErrors.map { reporter.onLintError(file, it, false) }
                }
            }
        }
    }

    companion object {
        private val errorsMap = mapOf(
            "test.kt" to listOf(LintError(1, 1, "test-rule", "test"), LintError(1, 2, "test-rule", "test_2"))
        )

        private val expectedPlainOutputGroupVerbose =
            """
            test.kt
              1:1 test (test-rule)
              1:2 test_2 (test-rule)
            """
                .trimIndent()
                .normalizeLineEndings()

        private val expectedPlainOutputGroupVerboseColored =
            """
            ${"\u001B[94m\u001B[0m"}test.kt
              1${"\u001B[94m:1\u001B[0m"} test${"\u001B[94m (test-rule)\u001B[0m"}
              1${"\u001B[94m:2\u001B[0m"} test_2${"\u001B[94m (test-rule)\u001B[0m"}
            """
                .trimIndent()
                .normalizeLineEndings()

        private val expectedJsonOutput =
            """
            |[
            |	{
            |		"file": "test.kt",
            |		"errors": [
            |			{
            |				"line": 1,
            |				"column": 1,
            |				"message": "test",
            |				"rule": "test-rule"
            |			},
            |			{
            |				"line": 1,
            |				"column": 2,
            |				"message": "test_2",
            |				"rule": "test-rule"
            |			}
            |		]
            |	}
            |]
            """
                .trimMargin()
                .normalizeLineEndings()

        private val expectedCheckstyleOutput =
            """
            |<?xml version="1.0" encoding="utf-8"?>
            |<checkstyle version="8.0">
            |	<file name="test.kt">
            |		<error line="1" column="1" severity="error" message="test" source="test-rule" />
            |		<error line="1" column="2" severity="error" message="test_2" source="test-rule" />
            |	</file>
            |</checkstyle>
            """
                .trimMargin()
                .normalizeLineEndings()
    }
}
