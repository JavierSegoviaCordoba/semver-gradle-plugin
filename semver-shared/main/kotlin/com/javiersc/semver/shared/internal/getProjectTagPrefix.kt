package com.javiersc.semver.shared.internal

import org.gradle.api.Project
import org.gradle.api.provider.Provider

public fun Project.projectTagPrefix(tagPrefix: Provider<String>): Provider<String> = provider {
    projectTagPrefixProperty.orNull ?: tagPrefix.get()
}
