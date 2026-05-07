@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.Definition

public interface SemverDefinition : Definition<BuildModel.None> {

    public val enabled: Property<Boolean>
    public val gitDir: DirectoryProperty
    public val tagPrefix: Property<String>
    public val commitsMaxCount: Property<Int>
    public val overrideVersion: Property<String>
    @get:Nested public val mapVersion: SemverMapVersionDefinition
}
