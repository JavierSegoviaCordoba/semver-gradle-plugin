package com.javiersc.semver.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class SemVerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<SemVerExtension>(SemVerExtension.name)

        extension.applyToAllProjects.convention(SemVerExtension.defaultApplyToAllProjects)
        extension.tagPrefix.convention(SemVerExtension.defaultTagPrefix)

        project.afterEvaluate {
            if (extension.applyToAllProjects.get()) {
                check(project.rootProject == project) {
                    "if `applyToAllProjects` is true, apply `SemVerPlugin` only in the root project"
                }
            }
        }
    }
}
