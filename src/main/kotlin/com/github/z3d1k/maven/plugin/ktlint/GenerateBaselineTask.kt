package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.lintFiles
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

        log.info("Start baseline generation using ${file.canonicalPath}...")

        val baselineReporter = BaselineReporter(PrintStream(file, Charsets.UTF_8.name()))

        baselineReporter.beforeAll()
        mavenProject.lintFiles(includes, excludes, baselineReporter, enableExperimentalRules)
        baselineReporter.afterAll()
    }
}
