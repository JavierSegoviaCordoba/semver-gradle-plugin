package com.javiersc.semver.settings.gradle.plugin.utils

import java.io.File

fun File.createGitIgnore() {
    resolve(".gitignore").apply {
        createNewFile()
        writeText(
            """
            |.gradle/
            |.idea/
            |.kotlin/
            |build/
            |local.properties
            |environment/
            |"""
                .trimMargin()
        )
    }
}
