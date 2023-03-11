package com.javiersc.semver.project.gradle.plugin.internal.git

import java.io.File
import org.gradle.api.Project

internal val Project.hasGit: Boolean
    get() = gitDir.exists()

internal val Project.gitDir: File
    get() = file("${rootProject.projectDir}/.git")
