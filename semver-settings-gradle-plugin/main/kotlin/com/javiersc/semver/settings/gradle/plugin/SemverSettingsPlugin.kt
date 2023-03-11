package com.javiersc.semver.settings.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.SemverProjectPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply

public class SemverSettingsPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        target.gradle.beforeProject { project ->
            project.pluginManager.apply(SemverProjectPlugin::class)
        }
    }
}
