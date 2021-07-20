package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.reports.generateReporter
import com.github.z3d1k.maven.plugin.ktlint.utils.forAll
import com.github.z3d1k.maven.plugin.ktlint.utils.formatFiles
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

@Mojo(name = "format", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
class FormatTask : AbstractMojo() {
    @Parameter(defaultValue = "\${project}", readonly = true)
    private lateinit var mavenProject: MavenProject

    @Parameter
    private var includes: String = "src\\/**\\/*.kt"

    @Parameter
    private var excludes: String? = null

    @Parameter
    private var enableExperimentalRules: Boolean = false

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        log.info("Ktlint format task started")
        val formatSummary = generateReporter(log).forAll { reporter ->
            mavenProject.formatFiles(includes, excludes, reporter, enableExperimentalRules)
        }
        log.info(
            "Ktlint format task finished: ${formatSummary.correctedFiles} of ${formatSummary.files} files were corrected"
        )
    }
}
