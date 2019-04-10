package com.github.z3d1k.maven.plugin.ktlint

import com.github.shyiko.ktlint.core.KtLint
import com.github.shyiko.ktlint.core.LintError
import com.github.shyiko.ktlint.core.RuleSet
import com.github.z3d1k.maven.plugin.ktlint.rules.resolveRuleSets
import com.github.z3d1k.maven.plugin.ktlint.utils.getEditorConfig
import com.github.z3d1k.maven.plugin.ktlint.utils.getSourceFiles
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

typealias FormatFunction = (String, Iterable<RuleSet>, Map<String, String>, (LintError, Boolean) -> Unit) -> String

@Mojo(name = "format", defaultPhase = LifecyclePhase.NONE)
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
        var filesNumber: Int
        val formattedCount =
            mavenProject.getSourceFiles(includes, excludes)
                .also { filesNumber = it.size }
                .map { formatFile(mavenProject.basedir, it, resolveRuleSets(enableExperimentalRules), emptyMap()) }
                .count { it }
        log.info("Ktlint format task finished: $formattedCount of $filesNumber files was formatted")
    }

    private fun formatFile(
        base: File,
        file: File,
        ruleSet: List<RuleSet>,
        userProperties: Map<String, String>? = null
    ): Boolean {
        val properties = userProperties ?: mavenProject.getEditorConfig()
        val filePath = file.toRelativeString(base)
        val sourceText = file.readText()
        val correctedErrors = mutableListOf<LintError>()
        val notCorrectedErrors = mutableListOf<LintError>()
        val formatFunc: FormatFunction = when (file.extension) {
            "kt" -> KtLint::format
            "kts" -> KtLint::formatScript
            else -> {
                log.info("File $filePath ignored: only files with \"*.kt\" or \"*.kts\" extensions can be formatted")
                return false
            }
        }
        val formattedSource = formatFunc(sourceText, ruleSet, properties) { lintError, corrected ->
            if (corrected) {
                correctedErrors += lintError
            } else {
                notCorrectedErrors += lintError
            }
        }
        var isFormatted = false
        if (formattedSource !== sourceText) {
            log.info("$filePath formatted")
            file.writeText(formattedSource)
            isFormatted = true
        }
        correctedErrors.forEach { (line, col, ruleId, detail) ->
            log.info("Error corrected: $ruleId($line:$col): $detail")
        }
        notCorrectedErrors.forEach { (line, col, ruleId, detail) ->
            log.warn("Unable to correct error: $ruleId($line:$col): $detail")
        }
        return isFormatted
    }
}
