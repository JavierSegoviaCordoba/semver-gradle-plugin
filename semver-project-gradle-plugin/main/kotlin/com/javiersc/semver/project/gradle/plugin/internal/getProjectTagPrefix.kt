package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.semver.project.gradle.plugin.semverExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal val Project.projectTagPrefix: Provider<String>
    get() = provider { projectTagPrefixProperty.orNull ?: semverExtension.tagPrefix.get() }
