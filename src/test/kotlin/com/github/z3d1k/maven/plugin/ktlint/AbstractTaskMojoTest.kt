package com.github.z3d1k.maven.plugin.ktlint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.testing.MojoRule
import org.apache.maven.project.MavenProject
import org.junit.Rule
import java.io.File
import kotlin.test.assertTrue

abstract class AbstractTaskMojoTest(protected val taskName: String, protected val goal: String = taskName) {
    @Rule
    @JvmField
    var rule = MojoRule()

    protected fun createScenarioRunner(
        scenarioName: String,
        block: (MavenProject, Log, Throwable?) -> Unit
    ) {
        val pom = File("target/test-classes/scenarios/$taskName-$scenarioName/pom.xml")
        assertTrue(pom.isFile)
        val mavenProject = rule.readMavenProject(pom.parentFile)
        val task = rule.lookupConfiguredMojo(pom.parentFile, goal)
        val log = mock<Log> {
            on { isDebugEnabled } doReturn true
        }
        task.log = log
        val throwable = runCatching { task.execute() }
            .exceptionOrNull()
        block(mavenProject, log, throwable)
    }
}
