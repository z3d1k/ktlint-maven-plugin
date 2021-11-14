package com.github.z3d1k.maven.plugin.ktlint

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.apache.maven.plugin.MojoFailureException
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(JUnit4::class)
class LinterTaskMojoTest : AbstractTaskMojoTest("lint") {
    @Test
    fun `lint fail on error`() = createScenarioRunner("fail-on-error") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 1 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Example.kt")
        verify(log).error(Mockito.contains("Unexpected blank line(s) before \"}\""))
        verify(log).error("Ktlint lint task finished: 1 files were checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun `lint continue on error`() = createScenarioRunner("continue-on-error") { _, log, throwable ->
        if (throwable != null) {
            fail("Exceptions were not expected")
        }

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Example.kt")
        verify(log).error(Mockito.contains("Unexpected blank line(s) before \"}\""))
        verify(log).error("Ktlint lint task finished: 1 files were checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun `lint no errors`() = createScenarioRunner("no-errors") { _, log, throwable ->
        if (throwable != null) {
            fail("Exceptions were not expected")
        }

        verify(log).info("Ktlint lint task started")
        verify(log).info("Ktlint lint task finished: 1 files were checked")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun `editor config properties`() = createScenarioRunner("editorconfig-properties") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 1 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Example.kt")
        verify(log).error(Mockito.contains("Exceeded max line length"))
        verify(log).error("Ktlint lint task finished: 1 files were checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun `suppress lint errors`() = createScenarioRunner("suppress-error") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 2 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/NotSuppressed.kt")
        verify(log).error(Mockito.contains("Wildcard import"))
        verify(log).error(Mockito.contains("Unexpected blank line(s) before \"}\""))
        verify(log).error("Ktlint lint task finished: 2 files were checked, found 2 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun `with baseline`() = createScenarioRunner("with-baseline") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 1 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info(ArgumentMatchers.startsWith("Using baseline"))
        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Example.kt")
        verify(log).error(Mockito.contains("Needless blank line(s)"))
        verify(log).error("Ktlint lint task finished: 1 files were checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun `compilation failed on file`() = createScenarioRunner("compilation-failure") { _, log, throwable ->
        throwable?.let {
            assertTrue(it is MojoFailureException)
            assertEquals("Failed during ktlint execution: found 1 errors in 1 files", it.message)
        } ?: fail("${MojoFailureException::class.java.canonicalName} was expected")

        verify(log).info("Ktlint lint task started")
        verify(log).error("src/main/kotlin/com/example/Invalid.kt")
        verify(log).error(Mockito.contains("File processing error: ParseException"))
        verify(log).error("Ktlint lint task finished: 2 files were checked, found 1 errors in 1 files")
        verifyNoMoreInteractions(log)
    }
}
