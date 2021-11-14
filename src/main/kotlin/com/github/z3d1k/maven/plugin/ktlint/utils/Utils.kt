package com.github.z3d1k.maven.plugin.ktlint.utils

import com.pinterest.ktlint.core.Reporter
import java.io.File

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

fun String.invariantSeparatorPathString(separator: Char = File.separatorChar): String {
    return if (separator != '/') {
        replace(separator, '/')
    } else {
        this
    }
}
