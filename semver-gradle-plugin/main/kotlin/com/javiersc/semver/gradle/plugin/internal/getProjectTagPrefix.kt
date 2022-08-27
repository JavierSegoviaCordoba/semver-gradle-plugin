package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.semver.gradle.plugin.semverExtension
import org.gradle.api.Project

internal val Project.projectTagPrefix: String
    get() = projectTagPrefixProperty ?: semverExtension.tagPrefix.get()
