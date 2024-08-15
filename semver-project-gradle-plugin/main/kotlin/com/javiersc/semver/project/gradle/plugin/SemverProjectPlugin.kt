package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.internal.checkScopeCorrectness
import com.javiersc.semver.project.gradle.plugin.internal.git.hasGit
import com.javiersc.semver.project.gradle.plugin.services.GitBuildService
import com.javiersc.semver.project.gradle.plugin.tasks.CreateSemverTagTask
import com.javiersc.semver.project.gradle.plugin.tasks.PrintSemverTask
import com.javiersc.semver.project.gradle.plugin.tasks.PushSemverTagTask
import com.javiersc.semver.project.gradle.plugin.tasks.WriteSemverTask
import com.javiersc.semver.project.gradle.plugin.valuesources.VersionValueSource
import com.javiersc.semver.project.gradle.plugin.valuesources.VersionValueSource.Versions
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

        if (semverExtension.isEnabled.get() && hasGit) {
            val gitTagBuildService = GitBuildService.register(this)
            checkScopeCorrectness()
            val versions: Provider<Versions> = configureVersionAndGetVersions()
            configureBuildServicesAndTasks(gitTagBuildService, versions)
        }
    }

    private fun Project.configureBuildServicesAndTasks(
        gitTagBuildService: Provider<GitBuildService>,
        versions: Provider<Versions>
    ) {
        PrintSemverTask.register(this, versions)
        WriteSemverTask.register(this, versions)
        CreateSemverTagTask.register(this, versions, gitTagBuildService)
        PushSemverTagTask.register(this, versions, gitTagBuildService)
    }

    private fun Project.configureVersionAndGetVersions(): Provider<Versions> {
        val versions: Provider<Versions> = VersionValueSource.register(this)
        semverExtension.calculatedVersion.set(versions.map { GradleVersion(it.version) })
        semverExtension.lastSemver.set(versions.map(Versions::lastSemver))
        version = VersionProperty(semverExtension.version)
        semverExtension.onMapVersion { version = VersionProperty(semverExtension.version) }

        // It is possible third party plugin breaks lazy configuration by calling `project.version`
        // too early, applying the calculated version in `afterEvaluate` fix it sometimes.
        afterEvaluate { proj ->
            val versionsAfter: Provider<Versions> = VersionValueSource.register(proj)
            proj.semverExtension.calculatedVersion.set(
                versionsAfter.map { GradleVersion(it.version) })
            proj.semverExtension.lastSemver.set(versionsAfter.map(Versions::lastSemver))
            proj.version = VersionProperty(proj.semverExtension.version)
            semverExtension.onMapVersion { version = VersionProperty(semverExtension.version) }
        }
        return versions
    }
}
