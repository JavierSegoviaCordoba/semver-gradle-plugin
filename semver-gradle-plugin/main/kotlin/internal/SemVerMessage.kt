package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.kotlin.stdlib.AnsiColor
import com.javiersc.kotlin.stdlib.ansiColor
import org.gradle.api.Project

internal fun Project.semverMessage(message: Any, color: AnsiColor = AnsiColor.Foreground.Green) =
    logger.lifecycle("$message".ansiColor(color))
