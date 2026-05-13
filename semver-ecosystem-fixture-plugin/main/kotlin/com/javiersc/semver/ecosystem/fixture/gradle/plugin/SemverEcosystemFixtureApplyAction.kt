@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.ecosystem.fixture.gradle.plugin

import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.ProjectFeatureApplicationContext
import org.gradle.features.binding.ProjectTypeApplyAction

public abstract class SemverEcosystemFixtureApplyAction :
    ProjectTypeApplyAction<SemverEcosystemFixtureDefinition, BuildModel.None> {

    override fun apply(
        context: ProjectFeatureApplicationContext,
        definition: SemverEcosystemFixtureDefinition,
        buildModel: BuildModel.None,
    ): Unit = Unit

    public companion object {
        public const val NAME: String = "semverEcosystemFixture"
    }
}
