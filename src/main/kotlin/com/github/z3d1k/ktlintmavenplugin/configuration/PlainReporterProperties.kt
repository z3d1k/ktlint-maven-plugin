package com.github.z3d1k.ktlintmavenplugin.configuration

import com.github.shyiko.ktlint.reporter.plain.PlainReporter
import org.apache.maven.plugins.annotations.Parameter

class PlainReporterProperties {
    @Parameter
    var verbose: Boolean = false

    @Parameter
    var color: Boolean = false

    @Parameter
    var groupByFile: Boolean = false

    @Parameter
    var pad: Boolean = false

    fun buildReporter() = PlainReporter(System.out, verbose, groupByFile, color, pad)
}