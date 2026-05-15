package com.javiersc.semver.shared

import com.javiersc.semver.shared.internal.checkScopeCorrectness
import com.javiersc.semver.shared.services.GitBuildService
import com.javiersc.semver.shared.tasks.CreateSemverTagTask
import com.javiersc.semver.shared.tasks.PrintSemverTask
import com.javiersc.semver.shared.tasks.PushSemverTagTask
import com.javiersc.semver.shared.tasks.WriteSemverTask
import com.javiersc.semver.shared.valuesources.VersionValueSource
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.apply

public fun Project.configureSemver(config: SemverConfig) {
    configureSemver(config) { project: Project, semverConfig: SemverConfig ->
        VersionValueSource.register(
            project = project,
            versionMapper = semverConfig.versionMapper,
            gitDir = semverConfig.gitDir,
            commitsMaxCount = semverConfig.commitsMaxCount,
            tagPrefix = semverConfig.tagPrefix,
        )
    }
}

public fun Project.configureSemver(
    config: SemverConfig,
    versionProviderFactory: (Project, SemverConfig) -> Provider<String>,
) {
    configureSemver(
        enabled = config.enabled,
        gitDir = config.gitDir,
        commitsMaxCount = config.commitsMaxCount,
        tagPrefix = config.tagPrefix,
    ) { project: Project ->
        versionProviderFactory(project, config)
    }
}

public fun Project.configureSemver(
    enabled: Provider<Boolean>,
    gitDir: Provider<out Directory>,
    commitsMaxCount: Provider<Int>,
    tagPrefix: Provider<String>,
    versionProviderFactory: (Project) -> Provider<String>,
) {
    pluginManager.apply(BasePlugin::class)

    if (enabled.get()) {
        val gitTagBuildService: Provider<GitBuildService> =
            GitBuildService.register(this, gitDir, commitsMaxCount)
        checkScopeCorrectness()
        configureVersion(versionProviderFactory)
        configureBuildServicesAndTasks(tagPrefix, gitTagBuildService)
    }
}

private fun Project.configureBuildServicesAndTasks(
    tagPrefix: Provider<String>,
    gitTagBuildService: Provider<GitBuildService>,
) {
    PrintSemverTask.register(this, tagPrefix)
    WriteSemverTask.register(this, tagPrefix)
    CreateSemverTagTask.register(this, tagPrefix, gitTagBuildService)
    PushSemverTagTask.register(this, tagPrefix, gitTagBuildService)
}

private fun Project.configureVersion(versionProviderFactory: (Project) -> Provider<String>) {
    val gradleVersionProvider: Provider<String> = versionProviderFactory(this)
    version = VersionProperty(gradleVersionProvider)

    // It is possible third party plugin breaks lazy configuration by calling `project.version`
    // too early, applying the calculated version in `afterEvaluate` fix it sometimes.
    afterEvaluate { proj ->
        val gradleVersionProviderProj: Provider<String> = versionProviderFactory(proj)
        proj.version = VersionProperty(gradleVersionProviderProj)
    }
}
