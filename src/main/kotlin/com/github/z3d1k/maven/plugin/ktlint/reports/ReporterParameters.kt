package com.github.z3d1k.maven.plugin.ktlint.reports

import com.pinterest.ktlint.reporter.plain.internal.Color
import java.io.File
import java.io.PrintStream
import java.nio.file.Files

data class ReporterParameters(
    val name: String,
    val output: PrintStream,
    val parameters: Map<String, String> = emptyMap()
) {
    companion object {
        fun fromParametersMap(parametersMap: Map<String, String>): List<ReporterParameters> {
            return parametersMap
                .map { (key, value) ->
                    val splittedKey = key.split(".", limit = 2)
                    if (splittedKey.count() != 2) {
                        throw IllegalArgumentException(
                            "Reporter parameters must be formatted like this:\n" +
                                "<{reporter_name}.{parameter_key}>{value}</{reporter_name}.{parameter_key}>"
                        )
                    }
                    val (reporterName, parameterKey) = splittedKey
                    reporterName to (parameterKey to value)
                }
                .groupBy({ it.first }, { it.second })
                .map { it.key to it.value.toMap() }
                .map { (name, params) ->
                    val reporterName = if (name == "console") "plain" else name

                    val reporterParametersMap =
                        if (reporterName == "plain" && !params.containsKey("color_name")) {
                            params + ("color_name" to Color.LIGHT_GRAY.name)
                        } else {
                            params
                        } - "output"

                    ReporterParameters(
                        reporterName,
                        getPrintStreamByFilename(name, params["output"]),
                        reporterParametersMap
                    )
                }
        }

        private fun getPrintStreamByFilename(reporterName: String, filename: String?): PrintStream {
            return when (reporterName) {
                "console" -> System.out
                else -> {
                    requireNotNull(filename) { "Output path not specified for reporter \"$reporterName\"" }
                    File(filename)
                        .also { Files.createDirectories(it.parentFile.toPath()) }
                        .let { PrintStream(it) }
                }
            }
        }
    }
}
