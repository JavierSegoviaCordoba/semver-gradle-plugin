package com.javiersc.semver.settings.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

public class SemverSettingsPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        target.gradle.beforeProject { project ->
            project.pluginManager.apply("com.javiersc.semver")
        }
    }
}
