package com.javiersc.semver.gradle.plugin.tasks

import com.javiersc.semver.gradle.plugin.internal.projectTagPrefix
import com.javiersc.semver.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.gradle.plugin.services.GitBuildService
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

public abstract class CreateSemverTagTask : DefaultTask() {

    @get:Internal internal abstract val tagPrefixProperty: Property<String>

    @get:Internal internal abstract val projectTagPrefix: Property<String>

    @get:Internal internal abstract val version: Property<String>

    @get:Internal internal abstract val gitTagBuildService: Property<GitBuildService>

    init {
        group = "semver"
    }

    @TaskAction
    public fun run() {
        gitTagBuildService
            .get()
            .createTag(tagPrefixProperty.get(), projectTagPrefix.get(), version.get())
    }

    public companion object {
        public const val taskName: String = "createSemverTag"

        internal fun register(project: Project, gitTagBuildService: Provider<GitBuildService>) {
            project.tasks
                .register<CreateSemverTagTask>(
                    taskName,
                )
                .configure {
                    this.tagPrefixProperty.set(project.tagPrefixProperty)
                    this.projectTagPrefix.set(project.projectTagPrefix)
                    this.version.set(project.version.toString())
                    this.gitTagBuildService.set(gitTagBuildService)
                    this.usesService(gitTagBuildService)
                }
        }
    }
}
