@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.ecosystem.fixture.integration.gradle.plugin

import com.javiersc.semver.declarative.gradle.plugin.SemverDefinition
import org.gradle.api.tasks.Nested
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.Definition

public interface SemverEcosystemFixtureIntegrationDefinition : Definition<BuildModel.None> {

    @get:Nested public val semver: SemverDefinition
}
