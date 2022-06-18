package com.javiersc.semver.gradle.plugin.tasks

import com.javiersc.semver.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.gradle.plugin.semverExtension
import com.javiersc.semver.gradle.plugin.services.GitBuildService
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

public abstract class SemverPushTag : DefaultTask() {

    init {
        group = "semver"
    }

    @get:Internal internal abstract val tagPrefixProperty: Property<String>

    @get:Internal internal abstract val projectTagPrefix: Property<String>

    @get:Internal internal abstract val version: Property<String>

    @get:Internal internal abstract val gitTagBuildService: Property<GitBuildService>

    @TaskAction
    public fun run() {
        gitTagBuildService
            .get()
            .pushTag(tagPrefixProperty.get(), projectTagPrefix.get(), version.get())
    }

    internal companion object {
        public const val taskName = "semverPushTag"

        internal fun register(project: Project, gitTagBuildService: Provider<GitBuildService>) {
            project.tasks.register<SemverPushTag>(taskName).configure { semverPushTag ->
                semverPushTag.tagPrefixProperty.set(project.tagPrefixProperty)
                semverPushTag.projectTagPrefix.set(project.semverExtension.tagPrefix)
                semverPushTag.version.set(project.version.toString())
                semverPushTag.gitTagBuildService.set(gitTagBuildService)
                semverPushTag.usesService(gitTagBuildService)
            }
        }
    }
}
