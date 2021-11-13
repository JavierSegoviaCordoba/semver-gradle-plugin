package com.javiersc.semver.gradle.plugin.tasks

import com.javiersc.semver.gradle.plugin.internal.git
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

public open class PushSemverTag : DefaultTask() {

    @TaskAction
    public fun run() {
        dependsOn(CreateSemverTag.name)
        project.git.push().setPushTags().call()
    }

    internal companion object {
        internal const val name = "pushSemverTag"

        internal fun register(project: Project) {
            if (project.rootProject.tasks.findByName(name) == null) {
                project.rootProject.tasks.register<PushSemverTag>(name)
            }
        }
    }
}
