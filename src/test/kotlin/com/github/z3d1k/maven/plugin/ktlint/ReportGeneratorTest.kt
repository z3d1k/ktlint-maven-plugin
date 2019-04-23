package com.github.z3d1k.maven.plugin.ktlint

import com.pinterest.ktlint.core.LintError
import com.github.z3d1k.maven.plugin.ktlint.reports.ReporterParameters
import com.github.z3d1k.maven.plugin.ktlint.reports.ReportsGenerator
import com.nhaarman.mockitokotlin2.mock
import org.apache.maven.plugin.logging.Log
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class ReportGeneratorTest {

    val errorsMap = mapOf(
        "test.kt" to listOf(LintError(1, 1, "test-rule", "test"), LintError(1, 2, "test-rule", "test_2"))
    )

    val expectedPlainOutput =
        """
test.kt:1:1: test
test.kt:1:2: test_2
""".trim()

    val expectedPlainOutput_group_verbose =
        """
test.kt
  1:1 test (test-rule)
  1:2 test_2 (test-rule)
""".trim()

    val expectedJsonOutput =
        """
[
	{
		"file": "test.kt",
		"errors": [
			{
				"line": 1,
				"column": 1,
				"message": "test",
				"rule": "test-rule"
			},
			{
				"line": 1,
				"column": 2,
				"message": "test_2",
				"rule": "test-rule"
			}
		]
	}
]
""".trim()

    val expectedCheckstyleOutput =
        """
<?xml version="1.0" encoding="utf-8"?>
<checkstyle version="8.0">
	<file name="test.kt">
		<error line="1" column="1" severity="error" message="test" source="test-rule" />
		<error line="1" column="2" severity="error" message="test_2" source="test-rule" />
	</file>
</checkstyle>
""".trim()

    @Test
    fun testReportersFromProperties() {
        val plainOutput = ByteArrayOutputStream()
        val jsonOutput = ByteArrayOutputStream()
        val checkstyleOutput = ByteArrayOutputStream()
        val reporterParams = listOf(
            ReporterParameters("json", PrintStream(jsonOutput), emptyMap()),
            ReporterParameters("checkstyle", PrintStream(checkstyleOutput), emptyMap()),
            ReporterParameters("plain", PrintStream(plainOutput), mapOf("verbose" to "true", "group_by_file" to "true"))
        )
        val log = mock<Log>()
        ReportsGenerator(log, reporterParams)
            .generateReports(errorsMap)
        val plainOutputString = plainOutput.toString().trim()
        val jsonOutputString = jsonOutput.toString().trim()
        val checkstyleOutputString = checkstyleOutput.toString().trim()
        assertEquals(expectedPlainOutput_group_verbose, plainOutputString, "Plain reporter output must match")
        assertEquals(expectedJsonOutput, jsonOutputString, "Json reporter output must match")
        assertEquals(expectedCheckstyleOutput, checkstyleOutputString, "Checkstyle reporter output must match")

        listOf(plainOutput, checkstyleOutput, jsonOutput).forEach { it.close() }
    }
}
