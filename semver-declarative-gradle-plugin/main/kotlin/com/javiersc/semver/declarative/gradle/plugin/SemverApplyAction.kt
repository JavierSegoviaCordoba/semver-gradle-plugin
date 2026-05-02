@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.SemverExtension
import com.javiersc.semver.project.gradle.plugin.SemverProjectPlugin
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.ProjectFeatureApplicationContext
import org.gradle.features.binding.ProjectTypeApplyAction
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

public abstract class SemverApplyAction :
    ProjectTypeApplyAction<SemverDefinition, BuildModel.None> {

    @get:Inject public abstract val project: Project
    @get:Inject public abstract val pluginManager: PluginManager

    override fun apply(
        context: ProjectFeatureApplicationContext,
        definition: SemverDefinition,
        buildModel: BuildModel.None,
    ) {
        val enabled: Provider<Boolean> = definition.enabled.orElse(true)

        if (enabled.get()) {
            val defaultGitDir: Directory = project.layout.settingsDirectory.dir(".git")

            val gitDir: Provider<Directory> = definition.gitDir.orElse(defaultGitDir)
            val commitsMaxCount: Provider<Int> =
                definition.commitsMaxCount.orElse(DefaultCommitsMaxCount)
            val tagPrefix: Provider<String> = definition.tagPrefix.orElse(DefaultTagPrefix)

            pluginManager.apply(SemverProjectPlugin::class)

            project.configure<SemverExtension> {
                this.isEnabled.set(enabled)
                this.gitDir.set(gitDir)
                this.commitsMaxCount.set(commitsMaxCount)
                this.tagPrefix.set(tagPrefix)
            }
        }
    }

    public companion object {
        public const val NAME: String = "semver"

        internal const val DefaultCommitsMaxCount = -1
        internal const val DefaultTagPrefix = ""
    }
}
