package com.javiersc.semver.gradle.plugin

import org.gradle.api.provider.Property

public interface SemVerExtension {

    public val tagPrefix: Property<String>
    public val applyToAllProjects: Property<Boolean>

    public companion object {
        internal const val defaultApplyToAllProjects = true
        internal const val defaultTagPrefix = ""
        internal const val name = "semVer"
    }
}
