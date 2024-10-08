@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.javiersc.semver.settings.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.SemverExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

public class SemverSettingsPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        val semver: SemverSettingsExtension = SemverSettingsExtension.register(target)
        target.gradle.beforeProject { project ->
            project.pluginManager.apply("com.javiersc.semver")
            project.pluginManager.withPlugin("com.javiersc.semver") {
                project.configure<SemverExtension> {
                    this.isEnabled.set(semver.isEnabled)
                    this.gitDir.set(semver.gitDir)
                    this.commitsMaxCount.set(semver.commitsMaxCount)
                    this.tagPrefix.set(semver.tagPrefix)
                    this.versionMapper.set(semver.versionMapper)
                }
            }
        }
    }
}

public fun Settings.semver(action: Action<SemverSettingsExtension>) {
    action.execute(extensions.getByType())
}
