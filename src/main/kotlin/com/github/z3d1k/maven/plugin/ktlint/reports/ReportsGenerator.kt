package com.github.z3d1k.maven.plugin.ktlint.reports

import com.pinterest.ktlint.core.Reporter
import com.pinterest.ktlint.core.ReporterProvider
import org.apache.maven.plugin.logging.Log
import java.util.ServiceLoader

fun generateReporter(
    log: Log,
    reporterParameters: List<ReporterParameters> = emptyList(),
    reporterProviders: Iterable<ReporterProvider> = ServiceLoader.load(ReporterProvider::class.java)
): Reporter {
    val reporterProvidersMap = reporterProviders.associateBy { it.id }
    val reportersList = reporterParameters.map { (name, printStream, parameters) ->
        requireNotNull(reporterProvidersMap[name]?.get(printStream, parameters)) {
            val availableReportersList = (reporterProvidersMap.keys + "console")
                .joinToString(", ", prefix = "{", postfix = "}") { it }
            "Unable to initialize reporter: unknown reporter name $name. " +
                "Available reporters are: $availableReportersList"
        }
    }
    return Reporter.from(MavenReporter(log), *reportersList.toTypedArray())
}
