package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.checkScopeCorrectness
import com.javiersc.semver.gradle.plugin.internal.git.hasCommits
import com.javiersc.semver.gradle.plugin.internal.git.hasGit
import com.javiersc.semver.gradle.plugin.internal.semverWarningMessage
import com.javiersc.semver.gradle.plugin.services.GitTagBuildService
import com.javiersc.semver.gradle.plugin.tasks.SemverCreateTag
import com.javiersc.semver.gradle.plugin.tasks.SemverPrintTask
import com.javiersc.semver.gradle.plugin.tasks.SemverPushTag
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

        when {
            target.hasGit.not() -> {
                semverWarningMessage("semver plugin can't work if there is no git repository")
            }
            target.hasCommits.not() -> {
                semverWarningMessage("semver plugin can't work if there are no commits")
            }
            else -> {
                target.configureLazyVersion()
                target.checkScopeCorrectness()
                target.configureBuildServicesAndTasks()
            }
        }
    }

    private fun Project.configureBuildServicesAndTasks() {
        val gitTagBuildService: Provider<GitTagBuildService> = GitTagBuildService.register(this)

        SemverCreateTag.register(this, gitTagBuildService)
        SemverPushTag.register(this, gitTagBuildService)
        SemverPrintTask.register(this)
    }

    private fun Project.configureLazyVersion() {
        version = LazyVersion(VersionValueSource.register(this))

        // Some third party plugin breaks lazy configuration by calling `project.version`
        // too early based on plugins order, applying the calculated version in
        // `afterEvaluate` fix it
        afterEvaluate { it.version = LazyVersion(VersionValueSource.register(this)) }
    }
}
