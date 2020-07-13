package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.ktlint.FormatSummary
import com.github.z3d1k.maven.plugin.ktlint.ktlint.formatFile
import com.github.z3d1k.maven.plugin.ktlint.reports.ReportsGenerator
import com.github.z3d1k.maven.plugin.ktlint.utils.getSourceFiles
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
        val reporter = ReportsGenerator(log)

        log.info("Ktlint format task started")
        reporter.beforeAll()
        val formatSummary =
            mavenProject
                .getSourceFiles(includes, excludes)
                .fold(FormatSummary()) { summary, file ->
                    summary + formatFile(
                        reporter,
                        mavenProject.basedir,
                        file,
                        enableExperimentalRules
                    )
                }
        reporter.afterAll()

        log.info(
            "Ktlint format task finished: ${formatSummary.correctedFiles} of ${formatSummary.files} files was corrected"
        )
    }
}
