package com.github.z3d1k.maven.plugin.ktlint

import com.github.z3d1k.maven.plugin.ktlint.utils.invariantSeparatorPathString
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class PathInvariantConversionTest(
    private val separator: Char,
    private val path: String,
    private val expectedPath: String
) {
    @Test
    fun `invariantSeparatorPathString should always return path string with slash as separator`() {
        assertEquals(expectedPath, path.invariantSeparatorPathString(separator))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = arrayOf(
            arrayOf('/', "/path/to/file", "/path/to/file"),
            arrayOf('\\', "\\path\\to\\file", "/path/to/file"),
            arrayOf(
                File.separatorChar,
                "${File.separatorChar}path${File.separatorChar}to${File.separatorChar}file",
                "/path/to/file"
            )
        )
    }
}
