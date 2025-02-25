package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.internal.checkScopeCorrectness
import com.javiersc.semver.project.gradle.plugin.services.GitBuildService
import com.javiersc.semver.project.gradle.plugin.tasks.CreateSemverTagTask
import com.javiersc.semver.project.gradle.plugin.tasks.PrintSemverTask
import com.javiersc.semver.project.gradle.plugin.tasks.PushSemverTagTask
import com.javiersc.semver.project.gradle.plugin.tasks.WriteSemverTask
import com.javiersc.semver.project.gradle.plugin.valuesources.VersionValueSource
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
            val gitTagBuildService: Provider<GitBuildService> = GitBuildService.register(this)
            checkScopeCorrectness()
            configureVersion()
            configureBuildServicesAndTasks(gitTagBuildService)
        }
    }

    private fun Project.configureBuildServicesAndTasks(
        gitTagBuildService: Provider<GitBuildService>
    ) {
        PrintSemverTask.register(this)
        WriteSemverTask.register(this)
        CreateSemverTagTask.register(this, gitTagBuildService)
        PushSemverTagTask.register(this, gitTagBuildService)
    }

    private fun Project.configureVersion() {
        val gradleVersionProvider: Provider<String> = VersionValueSource.register(this)
        version = VersionProperty(gradleVersionProvider)

        // It is possible third party plugin breaks lazy configuration by calling `project.version`
        // too early, applying the calculated version in `afterEvaluate` fix it sometimes.
        afterEvaluate { proj ->
            val gradleVersionProviderProj: Provider<String> = VersionValueSource.register(proj)
            proj.version = VersionProperty(gradleVersionProviderProj)
        }
    }
}
