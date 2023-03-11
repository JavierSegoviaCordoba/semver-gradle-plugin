package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.SemverProjectPlugin
import com.javiersc.semver.settings.gradle.plugin.SemverSettingsPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply

public class SemverPlugin : Plugin<PluginAware> {
    override fun apply(target: PluginAware) {
        when (target) {
            is Project -> target.pluginManager.apply(SemverProjectPlugin::class)
            is Settings -> target.pluginManager.apply(SemverSettingsPlugin::class)
            else -> error("Semver cannot be applied to ${target::class}")
        }
    }
}
