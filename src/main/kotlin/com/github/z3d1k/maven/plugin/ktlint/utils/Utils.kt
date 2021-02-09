package com.github.z3d1k.maven.plugin.ktlint.utils

import com.pinterest.ktlint.core.Reporter

fun <T> Reporter.forAll(block: (Reporter) -> T): T {
    beforeAll()
    val result = block(this)
    afterAll()
    return result
}

fun <T> Reporter.forFile(fileName: String, block: (Reporter, String) -> T): T {
    before(fileName)
    val result = block(this, fileName)
    after(fileName)
    return result
}
