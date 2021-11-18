package com.javiersc.semver.gradle.plugin.tasks

import com.javiersc.semanticVersioning.Version
import com.javiersc.semver.gradle.plugin.internal.git
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.internal.tagPrefix
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

public open class CreateSemverTag : DefaultTask() {

    @TaskAction
    public fun run() {
        check(project.git.status().call().isClean) {
            "A semver tag can't be created if the repo is not clean"
        }
        val semver = "${Version("${project.version}")}"
        val semverWithPrefix = "${project.tagPrefix}$semver"
        project.git.tag().setName(semverWithPrefix).call()
        project.semverMessage("Created new semver tag: $semverWithPrefix")
    }

    internal companion object {
        internal const val name = "createSemverTag"

        internal fun register(project: Project) {
            if (project.rootProject.tasks.findByName(name) == null) {
                project.rootProject.tasks.register<CreateSemverTag>(name)
            }
        }
    }
}

internal val Project.isCreatingSemverTag: Boolean
    get() = gradle.startParameter.taskNames.any { taskName -> taskName == CreateSemverTag.name }
