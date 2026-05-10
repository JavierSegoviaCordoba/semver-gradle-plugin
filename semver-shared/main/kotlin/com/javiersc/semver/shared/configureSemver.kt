package com.javiersc.semver.shared

import com.javiersc.semver.shared.internal.checkScopeCorrectness
import com.javiersc.semver.shared.services.GitBuildService
import com.javiersc.semver.shared.tasks.CreateSemverTagTask
import com.javiersc.semver.shared.tasks.PrintSemverTask
import com.javiersc.semver.shared.tasks.PushSemverTagTask
import com.javiersc.semver.shared.tasks.WriteSemverTask
import com.javiersc.semver.shared.valuesources.VersionValueSource
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.apply

public fun Project.configureSemver(config: SemverConfig) {
    pluginManager.apply(BasePlugin::class)

    if (config.enabled.get()) {
        val gitTagBuildService: Provider<GitBuildService> =
            GitBuildService.register(this, config.gitDir, config.commitsMaxCount)
        checkScopeCorrectness()
        configureVersion(config)
        configureBuildServicesAndTasks(config, gitTagBuildService)
    }
}

private fun Project.configureBuildServicesAndTasks(
    config: SemverConfig,
    gitTagBuildService: Provider<GitBuildService>,
) {
    PrintSemverTask.register(this, config.tagPrefix)
    WriteSemverTask.register(this, config.tagPrefix)
    CreateSemverTagTask.register(this, config.tagPrefix, gitTagBuildService)
    PushSemverTagTask.register(this, config.tagPrefix, gitTagBuildService)
}

private fun Project.configureVersion(config: SemverConfig) {
    val gradleVersionProvider: Provider<String> =
        VersionValueSource.register(
            project = this,
            versionMapper = config.versionMapper,
            gitDir = config.gitDir,
            commitsMaxCount = config.commitsMaxCount,
            tagPrefix = config.tagPrefix,
        )
    version = VersionProperty(gradleVersionProvider)

    // It is possible third party plugin breaks lazy configuration by calling `project.version`
    // too early, applying the calculated version in `afterEvaluate` fix it sometimes.
    afterEvaluate { proj ->
        val gradleVersionProviderProj: Provider<String> =
            VersionValueSource.register(
                project = proj,
                versionMapper = config.versionMapper,
                gitDir = config.gitDir,
                commitsMaxCount = config.commitsMaxCount,
                tagPrefix = config.tagPrefix,
            )
        proj.version = VersionProperty(gradleVersionProviderProj)
    }
}
