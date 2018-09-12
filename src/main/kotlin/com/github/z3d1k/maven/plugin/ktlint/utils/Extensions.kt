package com.github.z3d1k.maven.plugin.ktlint.utils

import com.github.shyiko.ktlint.internal.EditorConfig
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.utils.io.FileUtils
import java.io.File

fun MavenProject.getSourceFiles(include: String, exclude: String?): List<File> {
    return FileUtils.getFiles(basedir, include, exclude, true)
}

fun MavenProject.getEditorConfig(): Map<String, String> = EditorConfig.of(basedir.toPath()) ?: emptyMap()
