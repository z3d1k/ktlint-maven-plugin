package com.github.z3d1k.maven.plugin.ktlint.utils

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

fun String.normalizeLineEndings(lineSeparator: String = System.lineSeparator()): String {
    return this.replace("\n|\r\n".toRegex(), lineSeparator)
}

fun withMockedSystemOut(block: (out: OutputStream) -> Unit) {
    val systemOut = System.out

    ByteArrayOutputStream().use {
        System.setOut(PrintStream(it))
        block(it)
        System.setOut(systemOut)
    }
}
