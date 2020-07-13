package com.github.z3d1k.maven.plugin.ktlint

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.testing.MojoRule
import org.apache.maven.project.MavenProject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class FormatTaskMojoTest {
    @Rule
    @JvmField
    var rule = MojoRule()

    @Test
    fun nothingToFormat() = withScenario("nothing-to-format") { _, log ->
        verify(log).info("Ktlint format task started")
        verify(log).info("Ktlint format task finished: 0 of 1 files was corrected")
        verifyNoMoreInteractions(log)
    }

    @Test
    fun fixFormatting() = withScenario("fix-file") { _, log ->
        verify(log).info("Ktlint format task started")
        verify(log).info("Ktlint format task finished: 1 of 1 files was corrected")
        verifyNoMoreInteractions(log)
    }

    private fun withScenario(name: String, block: (MavenProject, Log) -> Unit) {
        val pom = File("target/test-classes/scenarios/format-$name/pom.xml")
        assertTrue(pom.isFile)
        val mavenProject = rule.readMavenProject(pom.parentFile)
        val task = rule.lookupConfiguredMojo(mavenProject, "format") as FormatTask
        val log = mock<Log>()
        whenever(log.isDebugEnabled).thenReturn(true)
        task.log = log
        task.execute()
        block(mavenProject, log)
    }
}
