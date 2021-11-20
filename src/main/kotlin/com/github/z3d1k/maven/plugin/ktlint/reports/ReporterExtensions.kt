package com.github.z3d1k.maven.plugin.ktlint.reports

import com.github.z3d1k.maven.plugin.ktlint.utils.invariantSeparatorPathString
import com.pinterest.ktlint.core.Reporter

fun <T> Reporter.forAll(block: (Reporter) -> T): T {
    beforeAll()
    val result = block(this)
    afterAll()
    return result
}

fun <T> Reporter.forFile(fileName: String, block: (Reporter, String) -> T): T {
    val normalizedFileName = fileName.invariantSeparatorPathString()

    before(normalizedFileName)
    val result = block(this, normalizedFileName)
    after(normalizedFileName)
    return result
}
