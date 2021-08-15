package com.github.z3d1k.maven.plugin.ktlint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.testing.MojoRule
import org.apache.maven.project.MavenProject
import org.junit.Rule
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class AbstractTaskMojoTest(protected val taskName: String, protected val goal: String = taskName) {
    private val testScenariosPath = Paths.get("target/test-classes/scenarios")

    @Rule
    @JvmField
    var rule = MojoRule()

    protected fun createScenarioRunner(
        scenarioName: String,
        block: (MavenProject, Log, Throwable?) -> Unit
    ) {
        val scenarioDir = Paths.get("src/test/resources/scenarios/$taskName-$scenarioName")
        val scenarioTempDir = Files.createTempDirectory(testScenariosPath, "$taskName-$scenarioName")

        scenarioDir.copyRecursivelyTo(scenarioTempDir)
        assertTrue(Files.list(scenarioTempDir).anyMatch { it.name == "pom.xml" })

        val mavenProject = rule.readMavenProject(scenarioTempDir.toFile())
        val task = rule.lookupConfiguredMojo(scenarioTempDir.toFile(), goal)
        val log = mock<Log> {
            on { isDebugEnabled } doReturn true
        }
        task.log = log
        val throwable = runCatching { task.execute() }
            .exceptionOrNull()
        block(mavenProject, log, throwable)

        scenarioTempDir.deleteRecursively()
        assertFalse(scenarioTempDir.exists())
    }

    private fun Path.copyRecursivelyTo(dest: Path) {
        Files.walk(this).use { stream ->
            stream.forEach { source ->
                Files.copy(source, dest.resolve(this.relativize(source)), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    private fun Path.deleteRecursively() {
        Files.walk(this)
            .sorted(Comparator.reverseOrder())
            .forEach(Files::delete)
    }
}
