package com.github.z3d1k.maven.plugin.ktlint.utils

import java.nio.file.Paths

fun String.normalizeLineEndings(lineSeparator: String = System.lineSeparator()): String {
    return this.replace("\n|\r\n".toRegex(), lineSeparator)
}

fun String.normalizePath(): String = Paths.get(this).toString()
