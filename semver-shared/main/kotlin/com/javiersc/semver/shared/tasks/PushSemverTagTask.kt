package com.javiersc.semver.shared.tasks

import com.javiersc.semver.shared.internal.projectTagPrefix
import com.javiersc.semver.shared.internal.tagPrefixProperty
import com.javiersc.semver.shared.services.GitBuildService
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
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

        public const val NAME: String = "pushSemverTag"

        public fun register(
            project: Project,
            tagPrefix: Provider<String>,
            gitTagBuildService: Provider<GitBuildService>,
        ) {
            project.tasks.register<PushSemverTagTask>(NAME).configure { task ->
                task.tagPrefixProperty.set(project.tagPrefixProperty)
                task.projectTagPrefix.set(project.projectTagPrefix(tagPrefix))
                task.version.set(project.version.toString())
                task.gitTagBuildService.set(gitTagBuildService)
                task.usesService(gitTagBuildService)
            }
        }
    }
}
