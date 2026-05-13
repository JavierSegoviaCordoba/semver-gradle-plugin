@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.semver.ecosystem.fixture.gradle.plugin.SemverEcosystemFixtureDefinition
import com.javiersc.semver.features.plugin.api.SemverDefinition
import org.gradle.api.logging.Logging
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.ProjectFeatureApplicationContext
import org.gradle.features.binding.ProjectFeatureApplyAction

public abstract class SemverEcosystemFixtureIntegrationApplyAction :
    ProjectFeatureApplyAction<SemverDefinition, BuildModel.None, SemverEcosystemFixtureDefinition> {

    override fun apply(
        context: ProjectFeatureApplicationContext,
        definition: SemverDefinition,
        buildModel: BuildModel.None,
        parentDefinition: SemverEcosystemFixtureDefinition,
    ) {
        Logging.getLogger(SemverEcosystemFixtureIntegrationApplyAction::class.java)
            .lifecycle("SemverEcosystemFixtureIntegration applied")
    }
}
