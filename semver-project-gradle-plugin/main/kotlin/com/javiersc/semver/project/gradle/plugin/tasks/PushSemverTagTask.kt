package com.javiersc.semver.project.gradle.plugin.tasks

import com.javiersc.semver.project.gradle.plugin.internal.projectTagPrefix
import com.javiersc.semver.project.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.project.gradle.plugin.services.GitBuildService
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

        internal fun register(project: Project, gitTagBuildService: Provider<GitBuildService>) {
            project.tasks.register<PushSemverTagTask>(NAME).configure { task ->
                task.tagPrefixProperty.set(project.tagPrefixProperty)
                task.projectTagPrefix.set(project.projectTagPrefix)
                task.version.set(project.version.toString())
                task.gitTagBuildService.set(gitTagBuildService)
                task.usesService(gitTagBuildService)
            }
        }
    }
}
