package com.github.z3d1k.maven.plugin.ktlint.utils

import org.apache.maven.project.MavenProject
import org.apache.maven.shared.utils.io.FileUtils
import java.io.File

fun MavenProject.getSourceFiles(include: String, exclude: String?): List<File> {
    return FileUtils.getFiles(basedir, include, exclude, true)
}
