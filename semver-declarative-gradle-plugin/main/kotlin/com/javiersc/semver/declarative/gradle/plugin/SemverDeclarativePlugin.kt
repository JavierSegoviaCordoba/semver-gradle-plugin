@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.features.annotations.RegistersProjectFeatures

@RegistersProjectFeatures(SemverProjectType::class)
public open class SemverDeclarativePlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        // NO-OP
    }
}
