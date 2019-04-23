package com.github.z3d1k.maven.plugin.ktlint.reports

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.Reporter
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import org.apache.maven.plugin.logging.Log
import org.apache.maven.shared.utils.logging.MessageUtils

class MavenReporter(private val log: Log) : Reporter {
    private val accumulator by lazy { ConcurrentHashMap<String, MutableList<LintError>>() }

    override fun onLintError(file: String, err: LintError, corrected: Boolean) {
        if (corrected) {
            return
        }

        accumulator.getOrPut(file, ::mutableListOf).add(err)
    }

    override fun after(file: String) {
        accumulator[file]
            ?.takeUnless { it.isEmpty() }
            ?.let { errors ->
                log.error(file.formatFileString())
                errors.forEach { lintError ->
                    reportLintError(lintError, " ".repeat(2))
                }
            }
    }

    private fun reportLintError(error: LintError, prefix: String) {
        val message =
            MessageUtils
                .buffer()
                .a(prefix)
                .strong(error.line)
                .a(":${error.col}:".pad(4))
                .failure(error.detail)
                .a(" (${error.ruleId})")
                .toString()
        log.error(message)
    }

    private fun String.formatFileString(): String {
        val dir = this.substringBeforeLast(File.separator, "") + File.separator
        val name = this.substringAfterLast(File.separator)
        return MessageUtils
            .buffer()
            .a(dir)
            .strong(name)
            .toString()
    }

    private fun String.pad(length: Int): String = this.padEnd(length + 1)
}
