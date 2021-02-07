package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.lintFiles
import com.github.z3d1k.maven.plugin.ktlint.utils.withReporter
import com.pinterest.ktlint.reporter.baseline.BaselineReporter
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File
import java.io.PrintStream

@Mojo(name = "generate-baseline", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
class GenerateBaselineTask : AbstractMojo() {
    @Parameter(defaultValue = "\${project}", readonly = true)
    private lateinit var mavenProject: MavenProject

    @Parameter
    private var includes: String = "src\\/**\\/*.kt"

    @Parameter
    private var excludes: String? = null

    @Parameter
    private var enableExperimentalRules: Boolean = false

    @Parameter(property = "baseline")
    private var baseline: File? = null

    override fun execute() {
        val file = baseline ?: File(mavenProject.basedir, "ktlint-baseline.xml")
        val printStream = PrintStream(file, Charsets.UTF_8.name())

        log.info("Start baseline generation using ${file.canonicalPath}...")
        withReporter(BaselineReporter(printStream)) { baselineReporter ->
            mavenProject.lintFiles(includes, excludes, baselineReporter, enableExperimentalRules)
        }
    }
}
