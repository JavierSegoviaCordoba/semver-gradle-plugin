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

public abstract class PushSemverTagTask : DefaultTask() {

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

    public companion object {
        public const val taskName: String = "pushSemverTag"

        internal fun register(project: Project, gitTagBuildService: Provider<GitBuildService>) {
            project.tasks.register<PushSemverTagTask>(taskName).configure { pushSemverTag ->
                pushSemverTag.tagPrefixProperty.set(project.tagPrefixProperty)
                pushSemverTag.projectTagPrefix.set(project.semverExtension.tagPrefix)
                pushSemverTag.version.set(project.version.toString())
                pushSemverTag.gitTagBuildService.set(gitTagBuildService)
                pushSemverTag.usesService(gitTagBuildService)
            }
        }
    }
}
