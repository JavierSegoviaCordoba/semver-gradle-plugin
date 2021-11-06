package com.javiersc.semver.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType

public interface SemVerExtension {

    public val tagPrefix: Property<String>
    public val applyToAllProjects: Property<Boolean>

    public companion object {
        internal const val defaultApplyToAllProjects = true
        internal const val defaultTagPrefix = ""
        internal const val name = "semver"
    }
}

internal val Project.semVerExtension: SemVerExtension
    get() = extensions.getByType()

internal val Project.tagPrefix: String
    get() = semVerExtension.tagPrefix.get()

internal val Project.applyToAllProjects: Boolean
    get() = semVerExtension.applyToAllProjects.get()
