package com.github.z3d1k.maven.plugin.ktlint.utils

import com.pinterest.ktlint.core.Reporter

fun <T> withReporter(reporter: Reporter, block: (Reporter) -> T): T {
    reporter.beforeAll()
    val result = block(reporter)
    reporter.afterAll()
    return result
}
