package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.normalizePath
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class FormatTaskMojoTest : AbstractTaskMojoTest("format") {
    @Test
    fun nothingToFormat() = createScenarioRunner("nothing-to-format") { _, log, _ ->
        verify(log).info("Ktlint format task started")
        verify(log).info("Ktlint format task finished: 0 of 1 files were corrected")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun fixFormatting() = createScenarioRunner("fix-file") { _, log, _ ->
        verify(log).info("Ktlint format task started")
        verify(log).info("Ktlint format task finished: 1 of 1 files were corrected")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun `compilation failed on file`() = createScenarioRunner("compilation-failure") { _, log, _ ->
        verify(log).info("Ktlint format task started")
        verify(log).error("src/main/kotlin/com/example/Invalid.kt".normalizePath())
        verify(log).error(Mockito.contains("File processing error: ParseException"))
        verify(log).info("Ktlint format task finished: 0 of 2 files were corrected")
        verifyNoMoreInteractions(log)
    }
}
