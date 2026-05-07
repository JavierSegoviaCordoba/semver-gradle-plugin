@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.declarative.dsl.model.annotations.Adding
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.Definition

public interface SemverDefinition : Definition<BuildModel.None> {

    public val enabled: Property<Boolean>
    public val gitDir: DirectoryProperty
    public val tagPrefix: Property<String>
    public val commitsMaxCount: Property<Int>

    @get:HiddenInDefinition public val overrideVersion: Property<String>

    @get:Nested public val currentVersion: SemverCurrentVersionBuilderDefinition
    @get:Nested public val mapVersion: SemverMapCurrentVersionDefinition

    public val mappedVersion: Property<SemverCurrentVersionBuilderDefinition>

    @Adding
    public fun mapVersion(version: String) {
        overrideVersion.set(version)
    }
}
