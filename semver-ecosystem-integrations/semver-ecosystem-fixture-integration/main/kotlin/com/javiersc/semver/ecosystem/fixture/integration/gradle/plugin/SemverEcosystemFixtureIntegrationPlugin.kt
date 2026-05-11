package com.javiersc.semver.ecosystem.fixture.integration.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.features.annotations.RegistersProjectFeatures

@RegistersProjectFeatures(SemverEcosystemFixtureIntegrationProjectType::class)
public open class SemverEcosystemFixtureIntegrationPlugin : Plugin<Settings> {

    override fun apply(target: Settings): Unit = Unit
}
