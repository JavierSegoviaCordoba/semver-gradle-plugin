@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.fixture.integration.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.features.annotations.BindsProjectFeature
import org.gradle.features.binding.ProjectFeatureBinding
import org.gradle.features.binding.ProjectFeatureBindingBuilder
import org.gradle.features.dsl.bindProjectFeature

@BindsProjectFeature(SemverEcosystemFixtureIntegrationFeaturePlugin.Binding::class)
public open class SemverEcosystemFixtureIntegrationFeaturePlugin : Plugin<Project> {

    public class Binding : ProjectFeatureBinding {

        override fun bind(builder: ProjectFeatureBindingBuilder) {
            builder
                .bindProjectFeature(
                    "semver",
                    SemverEcosystemFixtureIntegrationApplyAction::class,
                )
                .withUnsafeDefinition()
                .withUnsafeApplyAction()
        }
    }

    override fun apply(target: Project): Unit = Unit
}
