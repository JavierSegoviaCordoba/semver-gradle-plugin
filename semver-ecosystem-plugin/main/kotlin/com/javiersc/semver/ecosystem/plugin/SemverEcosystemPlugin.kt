@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.ecosystem.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.features.annotations.RegistersProjectFeatures

@RegistersProjectFeatures(SemverProjectFeaturePlugin::class)
public class SemverEcosystemPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {}
}
