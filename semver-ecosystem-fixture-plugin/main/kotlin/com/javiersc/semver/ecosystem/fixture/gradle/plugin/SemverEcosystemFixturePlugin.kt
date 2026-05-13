package com.javiersc.semver.ecosystem.fixture.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.features.annotations.RegistersProjectFeatures

@RegistersProjectFeatures(SemverEcosystemFixtureProjectType::class)
public open class SemverEcosystemFixturePlugin : Plugin<Settings> {

    override fun apply(target: Settings): Unit = Unit
}
