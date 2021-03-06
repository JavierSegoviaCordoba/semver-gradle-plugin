package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.checkScopeCorrectness
import com.javiersc.semver.gradle.plugin.services.GitBuildService
import com.javiersc.semver.gradle.plugin.tasks.CreateSemverTagTask
import com.javiersc.semver.gradle.plugin.tasks.PrintSemverTask
import com.javiersc.semver.gradle.plugin.tasks.PushSemverTagTask
import com.javiersc.semver.gradle.plugin.tasks.WriteSemverTask
import com.javiersc.semver.gradle.plugin.valuesources.VersionValueSource
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.apply

public class SemverPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.pluginManager.apply(BasePlugin::class)

        SemverExtension.register(target)

        val gitTagBuildService: Provider<GitBuildService> = GitBuildService.register(target)
        target.checkScopeCorrectness()
        target.configureLazyVersion(gitTagBuildService)
        target.configureBuildServicesAndTasks(gitTagBuildService)
    }

    private fun Project.configureBuildServicesAndTasks(
        gitTagBuildService: Provider<GitBuildService>
    ) {
        CreateSemverTagTask.register(this, gitTagBuildService)
        PushSemverTagTask.register(this, gitTagBuildService)
        PrintSemverTask.register(this)
        WriteSemverTask.register(this)
    }

    private fun Project.configureLazyVersion(gitTagBuildService: Provider<GitBuildService>) {
        version = LazyVersion(VersionValueSource.register(this, gitTagBuildService))

        // Some third party plugin breaks lazy configuration by calling `project.version`
        // too early based on plugins order, applying the calculated version in
        // `afterEvaluate` fix it
        afterEvaluate {
            version = LazyVersion(VersionValueSource.register(this, gitTagBuildService))
        }
    }
}
