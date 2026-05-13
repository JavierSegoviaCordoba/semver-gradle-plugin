@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.ecosystem.plugin.api

import org.gradle.api.provider.Property
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.Definition

public interface SemverDefinition : Definition<BuildModel.None> {

    public val tagPrefix: Property<String>
}
