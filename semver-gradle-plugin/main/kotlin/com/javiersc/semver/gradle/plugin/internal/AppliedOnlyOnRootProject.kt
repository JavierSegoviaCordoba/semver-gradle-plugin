package com.javiersc.semver.gradle.plugin.internal

import org.gradle.api.Project

internal val Project.appliedOnlyOnRootProject: Boolean
    get() = rootProject.hasSemVerPlugin && rootProject.subprojects.none(Project::hasSemVerPlugin)
