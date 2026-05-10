package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.shared.VersionProperty
import com.javiersc.semver.shared.internal.checkScopeCorrectness
import com.javiersc.semver.shared.services.GitBuildService
import com.javiersc.semver.shared.tasks.CreateSemverTagTask
import com.javiersc.semver.shared.tasks.PrintSemverTask
import com.javiersc.semver.shared.tasks.PushSemverTagTask
import com.javiersc.semver.shared.tasks.WriteSemverTask
import com.javiersc.semver.shared.valuesources.VersionValueSource
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.apply

public class SemverProjectPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.config()
    }

    private fun Project.config() {
        pluginManager.apply(BasePlugin::class)

        SemverExtension.register(this)

        if (semverExtension.isEnabled.get()) {
            val extension: SemverExtension = semverExtension
            val gitTagBuildService: Provider<GitBuildService> =
                GitBuildService.register(this, extension.gitDir, extension.commitsMaxCount)
            checkScopeCorrectness()
            configureVersion(extension)
            configureBuildServicesAndTasks(extension, gitTagBuildService)
        }
    }

    private fun Project.configureBuildServicesAndTasks(
        extension: SemverExtension,
        gitTagBuildService: Provider<GitBuildService>,
    ) {
        PrintSemverTask.register(this, extension.tagPrefix)
        WriteSemverTask.register(this, extension.tagPrefix)
        CreateSemverTagTask.register(this, extension.tagPrefix, gitTagBuildService)
        PushSemverTagTask.register(this, extension.tagPrefix, gitTagBuildService)
    }

    private fun Project.configureVersion(extension: SemverExtension) {
        val gradleVersionProvider: Provider<String> =
            VersionValueSource.register(
                project = this,
                versionMapper = extension.versionMapper,
                gitDir = extension.gitDir,
                commitsMaxCount = extension.commitsMaxCount,
                tagPrefix = extension.tagPrefix,
            )
        version = VersionProperty(gradleVersionProvider)

        // It is possible third party plugin breaks lazy configuration by calling `project.version`
        // too early, applying the calculated version in `afterEvaluate` fix it sometimes.
        afterEvaluate { proj ->
            val gradleVersionProviderProj: Provider<String> =
                VersionValueSource.register(
                    project = proj,
                    versionMapper = extension.versionMapper,
                    gitDir = extension.gitDir,
                    commitsMaxCount = extension.commitsMaxCount,
                    tagPrefix = extension.tagPrefix,
                )
            proj.version = VersionProperty(gradleVersionProviderProj)
        }
    }
}
