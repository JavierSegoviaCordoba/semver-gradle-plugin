package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.gradle.logging.extensions.lifecycleColored
import com.javiersc.gradle.logging.extensions.warnColored
import com.javiersc.kotlin.stdlib.AnsiColor
import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.SemverPlugin
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

internal fun semverWarningMessage(message: Any, color: AnsiColor = AnsiColor.Foreground.Yellow) =
    defaultLogger.warnColored(color) { "$message" }

internal fun semverMessage(message: Any, color: AnsiColor = AnsiColor.Foreground.Purple) =
    defaultLogger.lifecycleColored(color) { "$message" }

internal fun warningLastVersionIsNotHigherVersion(last: Version?, higher: Version?) {
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

private val defaultLogger: Logger = Logging.getLogger(SemverPlugin::class.java)
