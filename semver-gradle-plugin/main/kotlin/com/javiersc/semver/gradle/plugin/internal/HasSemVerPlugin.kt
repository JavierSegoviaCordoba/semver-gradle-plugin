package com.javiersc.semver.gradle.plugin.internal

import org.gradle.api.Project

internal val Project.hasSemVerPlugin: Boolean
    get() = pluginManager.hasPlugin("com.javiersc.semver.gradle.plugin")
