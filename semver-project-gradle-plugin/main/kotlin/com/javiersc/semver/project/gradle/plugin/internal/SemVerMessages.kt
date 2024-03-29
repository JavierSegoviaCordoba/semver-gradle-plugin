package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.gradle.logging.extensions.quietColored
import com.javiersc.gradle.logging.extensions.warnColored
import com.javiersc.gradle.version.GradleVersion
import com.javiersc.kotlin.stdlib.AnsiColor
import com.javiersc.semver.project.gradle.plugin.SemverProjectPlugin
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

internal fun semverWarningMessage(message: Any) = defaultLogger.warnColored { "$message" }

internal fun semverMessage(message: Any, color: AnsiColor = AnsiColor.Foreground.Purple) =
    defaultLogger.quietColored(color) { "$message" }

internal fun warningLastVersionIsNotHigherVersion(last: GradleVersion?, higher: GradleVersion?) {
    val message =
        """|There is an old tag with a higher version than the last tag version:
           |  - Old tag version -> $last
           |  - Last tag version -> $higher
        """
            .trimMargin()

    if (last != null && higher != null && last < higher) {
        semverMessage(message = message, color = AnsiColor.Foreground.Yellow)
    }
}

private val defaultLogger: Logger = Logging.getLogger(SemverProjectPlugin::class.java)
