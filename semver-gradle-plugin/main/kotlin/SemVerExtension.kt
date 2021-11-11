package com.javiersc.semver.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType

public interface SemVerExtension {

    public val tagPrefix: Property<String>

    public companion object {
        internal const val defaultTagPrefix = "v"
        internal const val name = "semver"
    }
}

internal val Project.semVerExtension: SemVerExtension
    get() = extensions.getByType()

internal val Project.tagPrefix: String
    get() = semVerExtension.tagPrefix.get()
