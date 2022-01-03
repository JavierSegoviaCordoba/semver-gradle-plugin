package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.kotlin.stdlib.AnsiColor
import com.javiersc.kotlin.stdlib.ansiColor
import com.javiersc.semver.Version
import org.gradle.api.Project

internal fun Project.semverMessage(message: Any, color: AnsiColor = AnsiColor.Foreground.Purple) =
    logger.lifecycle("$message".ansiColor(color))

internal fun Project.warningLastVersionIsNotHigherVersion(last: Version?, higher: Version?) {
    val message =
        """|There is an old tag with a higher version than the last tag version:
           |  - Old tag version -> $last
           |  - Last tag version -> $higher
        """.trimMargin()

    if (last != null && higher != null && last < higher) {
        logger.lifecycle(message)
    }
}
