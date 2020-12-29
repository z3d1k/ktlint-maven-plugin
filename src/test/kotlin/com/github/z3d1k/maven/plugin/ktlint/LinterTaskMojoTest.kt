package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.normalizePath
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.testing.MojoRule
import org.apache.maven.project.MavenProject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(JUnit4::class)
class LinterTaskMojoTest {
    @Rule
    @JvmField
    var rule = MojoRule()

    @Test
    fun lintFailOnError() = withScenario("fail-on-error") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 1 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Example.kt".normalizePath())
        verify(log).error(Mockito.contains("Unexpected blank line(s) before \"}\""))
        verify(log).error("Ktlint lint task finished: 1 files was checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun lintContinueOnError() = withScenario("continue-on-error") { _, log, throwable ->
        if (throwable != null) {
            fail("Exceptions were not expected")
        }

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Example.kt".normalizePath())
        verify(log).error(Mockito.contains("Unexpected blank line(s) before \"}\""))
        verify(log).error("Ktlint lint task finished: 1 files was checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun lintNoErrors() = withScenario("no-errors") { _, log, throwable ->
        if (throwable != null) {
            fail("Exceptions were not expected")
        }

        verify(log).info("Ktlint lint task started")
        verify(log).info("Ktlint lint task finished: 1 files was checked")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun editorConfigProperties() = withScenario("editorconfig-properties") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 1 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Example.kt".normalizePath())
        verify(log).error(Mockito.contains("Exceeded max line length"))
        verify(log).error("Ktlint lint task finished: 1 files was checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun suppressLintErrors() = withScenario("suppress-error") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 2 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/NotSuppressed.kt".normalizePath())
        verify(log).error(Mockito.contains("Wildcard import"))
        verify(log).error(Mockito.contains("Unexpected blank line(s) before \"}\""))
        verify(log).error("Ktlint lint task finished: 2 files was checked, found 2 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun withBaseline() = withScenario("with-baseline") { _, log, throwable ->
        if (throwable != null) {
            fail("Exceptions were not expected")
        }

        verify(log).info(ArgumentMatchers.startsWith("Using baseline"))
        verify(log).info("Ktlint lint task started")
        verify(log).info("Ktlint lint task finished: 1 files was checked")
        verifyNoMoreInteractions(log)
    }

    private fun withScenario(name: String, block: (MavenProject, Log, Throwable?) -> Unit) {
        val pom = File("target/test-classes/scenarios/lint-$name/pom.xml")
        assertTrue(pom.isFile)
        val mavenProject = rule.readMavenProject(pom.parentFile)
        val task = rule.lookupConfiguredMojo(mavenProject, "lint") as LinterTask
        val log = mock<Log>()
        whenever(log.isDebugEnabled).thenReturn(true)
        task.log = log
        val throwable = try {
            task.execute()
            null
        } catch (e: Exception) {
            e
        }
        block(mavenProject, log, throwable)
    }
}
