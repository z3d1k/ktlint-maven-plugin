package com.github.z3d1k.maven.plugin.ktlint

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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
}
