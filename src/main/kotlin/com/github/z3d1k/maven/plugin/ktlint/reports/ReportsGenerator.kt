package com.github.z3d1k.maven.plugin.ktlint.reports

import com.github.shyiko.ktlint.core.LintError
import com.github.shyiko.ktlint.core.Reporter
import com.github.shyiko.ktlint.core.ReporterProvider
import java.util.ServiceLoader

class ReportsGenerator(
    reporterParameters: List<ReporterParameters>,
    reporterProviders: Iterable<ReporterProvider> = ServiceLoader.load(ReporterProvider::class.java)
) {
    private val reporter: Reporter

    init {
        val reporterProvidersMap = reporterProviders.associate { it.id to it }
        val reportersList = reporterParameters
                .map { (name, printStream, parameters) ->
                    reporterProvidersMap[name]?.get(printStream, parameters)
                            ?: throw IllegalArgumentException(
                                "Unable to initialize reporter with $name: unknown reporter name"
                            )
                }
        reporter = Reporter.from(*reportersList.toTypedArray())
    }

    fun generateReports(lintResults: Map<String, List<LintError>>) {
        reporter.beforeAll()
        lintResults.forEach { fileName, lintErrors ->
            reporter.before(fileName)
            lintErrors.map {
                reporter.onLintError(fileName, it, false)
            }
            reporter.after(fileName)
        }
        reporter.afterAll()
    }
}
