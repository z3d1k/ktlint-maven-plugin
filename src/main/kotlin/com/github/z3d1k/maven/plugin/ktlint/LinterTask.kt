package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.ktlint.LintSummary
import com.github.z3d1k.maven.plugin.ktlint.ktlint.lintFile
import com.github.z3d1k.maven.plugin.ktlint.ktlint.loadBaseline
import com.github.z3d1k.maven.plugin.ktlint.reports.ReporterParameters
import com.github.z3d1k.maven.plugin.ktlint.reports.ReportsGenerator
import com.github.z3d1k.maven.plugin.ktlint.utils.getSourceFiles
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

@Mojo(name = "lint", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
class LinterTask : AbstractMojo() {
    @Parameter(defaultValue = "\${project}", readonly = true)
    private lateinit var mavenProject: MavenProject

    @Parameter
    private var includes: String = "src\\/**\\/*.kt"

    @Parameter
    private var excludes: String? = null

    @Parameter
    private var enableExperimentalRules: Boolean = false

    @Parameter
    private var reporters: Map<String, String> = emptyMap()

    @Parameter(property = "baseline")
    private var baseline: File? = null

    @Parameter
    private var failOnError: Boolean = true

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        val reporterParameters = ReporterParameters.fromParametersMap(reporters)
        val reporter = ReportsGenerator(log, reporterParameters)
        val baselineRules = loadBaseline(log, baseline)

        log.info("Ktlint lint task started")
        reporter.beforeAll()
        val lintSummary =
            mavenProject
                .getSourceFiles(includes, excludes)
                .fold(LintSummary()) { summary, file ->
                    summary + lintFile(
                        reporter,
                        mavenProject.basedir,
                        file,
                        enableExperimentalRules,
                        baselineRules
                    )
                }
        reporter.afterAll()

        reporterParameters.forEach { it.output.close() }
        if (lintSummary.hasErrors) {
            log.error(
                "Ktlint lint task finished: ${lintSummary.files} files was checked," +
                    " found ${lintSummary.errors} errors in ${lintSummary.filesWithErrors} files"
            )
            if (failOnError) {
                throw MojoFailureException(
                    "Failed during ktlint execution:" +
                        " found ${lintSummary.errors} errors in ${lintSummary.filesWithErrors} files"
                )
            }
        } else {
            log.info("Ktlint lint task finished: ${lintSummary.files} files was checked")
        }
    }
}
