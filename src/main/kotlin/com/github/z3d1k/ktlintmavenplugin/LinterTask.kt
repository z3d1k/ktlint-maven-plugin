package com.github.z3d1k.ktlintmavenplugin

import com.github.shyiko.ktlint.core.KtLint
import com.github.shyiko.ktlint.core.LintError
import com.github.shyiko.ktlint.core.Reporter
import com.github.shyiko.ktlint.reporter.checkstyle.CheckStyleReporter
import com.github.shyiko.ktlint.reporter.json.JsonReporter
import com.github.shyiko.ktlint.reporter.plain.PlainReporter
import com.github.z3d1k.ktlintmavenplugin.support.resolveRuleSets
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.utils.io.FileUtils
import org.jetbrains.kotlin.backend.common.push
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream

@Mojo(name = "lint", defaultPhase = LifecyclePhase.VALIDATE)
class LinterTask : AbstractMojo() {
    @Parameter(defaultValue = "\${project}", readonly = true)
    private lateinit var mavenProject: MavenProject

    @Parameter
    private var includes: String = "src\\/**\\/*.kt"

    @Parameter
    private var excludes: String? = null

    @Parameter
    private var outputToConsole: Boolean = true

    @Parameter
    private var color: Boolean = false

    @Parameter
    private var groupByFile: Boolean = true

    @Parameter
    private var pad: Boolean = false

    @Parameter
    private var verbose: Boolean = true

    @Parameter
    private val checkstyleReportPath: String? = null

    @Parameter
    private val jsonReportPath: String? = null

    @Parameter
    private var failOnError: Boolean = true

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        log.info("Ktlint lint task started")
        val lintResults = lint(mavenProject.basedir, includes, excludes)
        generateReports(
                outputToConsole,
                color,
                groupByFile,
                pad,
                verbose,
                checkstyleReportPath,
                jsonReportPath,
                lintResults
        )
        val errorCount: Int = lintResults.entries.fold(0, { count, entry -> entry.value.count() + count })
        log.info("Ktlint lint task finished: $errorCount errors found in project")
        if (errorCount != 0 && failOnError) {
            throw MojoExecutionException("Failed during ktlint execution: $errorCount errors found in project")
        }
    }

    private fun lint(
        baseDirectory: File,
        includes: String,
        excludes: String?,
        userProperties: Map<String, String> = emptyMap()
    ): Map<String, List<LintError>> {
        return baseDirectory
                .let { FileUtils.getFiles(it, includes, excludes, true) }
                .map { file ->
                    val eventList = mutableListOf<LintError>()
                    KtLint.lint(file.readText(), resolveRuleSets(), userProperties, { eventList.push(it) })
                    file.path to eventList
                }.toMap()
    }

    private fun generateReports(
        outputToConsole: Boolean,
        color: Boolean,
        groupByFile: Boolean,
        pad: Boolean,
        verbose: Boolean,
        checkstyleReportPath: String?,
        jsonReportPath: String?,
        lintResults: Map<String, List<LintError>>
    ) {
        val plainReporter = PlainReporter(System.out, verbose, groupByFile, color, pad).takeIf { outputToConsole }
        val checkStyleReporter = checkstyleReportPath?.let { CheckStyleReporter(getPrintStreamForReporter(it)) }
        val jsonReporter = jsonReportPath?.let { JsonReporter(getPrintStreamForReporter(it)) }
        val reporters = listOfNotNull(plainReporter, checkStyleReporter, jsonReporter)
                .toTypedArray()
                .let { Reporter.from(*it) }
        reporters.beforeAll()
        lintResults.forEach { filePath, errorList ->
            reporters.before(filePath)
            errorList.forEach {
                reporters.onLintError(filePath, it, false)
            }
            reporters.after(filePath)
        }
        reporters.afterAll()
    }

    private fun getPrintStreamForReporter(fileName: String): PrintStream {
        // create intermediate directories, if not exists
        File(fileName).parentFile.mkdirs()
        return PrintStream(FileOutputStream(fileName))
    }
}
