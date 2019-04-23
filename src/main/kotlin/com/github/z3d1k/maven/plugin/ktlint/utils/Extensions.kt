package com.github.z3d1k.maven.plugin.ktlint.utils

import com.pinterest.ktlint.internal.EditorConfig
import java.io.File
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.utils.io.FileUtils

fun MavenProject.getSourceFiles(include: String, exclude: String?): List<File> {
    return FileUtils.getFiles(basedir, include, exclude, true)
}

fun MavenProject.getEditorConfig(): Map<String, String> = EditorConfig.of(basedir.toPath()) ?: emptyMap()
