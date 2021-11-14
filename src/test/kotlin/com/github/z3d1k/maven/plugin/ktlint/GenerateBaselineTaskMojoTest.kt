package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.normalizeLineEndings
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class GenerateBaselineTaskMojoTest(private val name: String) : AbstractTaskMojoTest("baseline", "generate-baseline") {
    @Test
    fun `create baseline`() = createScenarioRunner(name) { project, _, _ ->
        val baselineFileName = project.getGoalConfiguration("com.github.z3d1k", "ktlint-maven-plugin", null, goal)
            ?.getChild("baseline")
            ?.value
            ?: "ktlint-baseline.xml"

        val baselineFile = File(project.basedir, baselineFileName)
        assert(baselineFile.exists())
        assertEquals(
            File(project.basedir, "expected-baseline.xml").readText().normalizeLineEndings(),
            baselineFile.readText()
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "generate baseline: {0}")
        fun parameters() = arrayOf(
            "create",
            "create-unconfigured"
        )
    }
}
