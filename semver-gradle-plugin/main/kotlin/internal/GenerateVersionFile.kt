package com.javiersc.semver.gradle.plugin.internal

import java.io.File
import org.gradle.api.Project

internal fun Project.generateVersionFile() =
    File("$buildDir/semver/version.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(
            """
               |$version
               |$tagPrefixProperty$version
               |
            """.trimMargin()
        )
    }
