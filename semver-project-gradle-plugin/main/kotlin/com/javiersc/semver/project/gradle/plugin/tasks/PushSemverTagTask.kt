package com.javiersc.semver.project.gradle.plugin.tasks

import com.javiersc.semver.project.gradle.plugin.internal.projectTagPrefix
import com.javiersc.semver.project.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.project.gradle.plugin.services.GitBuildService
import com.javiersc.semver.project.gradle.plugin.valuesources.VersionValueSource
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

public abstract class PushSemverTagTask : DefaultTask() {

    init {
        group = "semver"
    }

    @get:Internal internal abstract val tagPrefixProperty: Property<String>

    @get:Internal internal abstract val projectTagPrefix: Property<String>

    @get:Internal internal abstract val version: Property<String>

    @get:Input internal abstract val versions: Property<VersionValueSource.Versions>

    @get:Internal internal abstract val gitTagBuildService: Property<GitBuildService>

    @TaskAction
    public fun run() {
        versions.orNull?.checkVersionIsHigherOrSame(version.orNull)

        gitTagBuildService
            .get()
            .pushTag(tagPrefixProperty.get(), projectTagPrefix.get(), version.get())
    }

    public companion object {

        public const val NAME: String = "pushSemverTag"

        internal fun register(
            project: Project,
            versions: Provider<VersionValueSource.Versions>,
            gitTagBuildService: Provider<GitBuildService>
        ) {
            project.tasks.register<PushSemverTagTask>(NAME).configure { task ->
                task.tagPrefixProperty.set(project.tagPrefixProperty)
                task.projectTagPrefix.set(project.projectTagPrefix)
                task.version.set(project.version.toString())
                task.versions.set(versions)
                task.gitTagBuildService.set(gitTagBuildService)
                task.usesService(gitTagBuildService)
            }
        }
    }
}
