package com.github.z3d1k.ktlintmavenplugin.support

enum class ReporterType(val reporterName: String) {
    CHECKSTYLE("checkstyle"),
    JSON("json"),
    PLAIN("plain");

    companion object {
        private val valuesMap = ReporterType.values().associateBy { it.reporterName }

        fun fromName(reporterName: String): ReporterType = valuesMap[reporterName]
                ?: throw IllegalArgumentException("Unknown reporter name")
    }
}
