package com.javiersc.semver.gradle.plugin

import com.javiersc.semanticVersioning.Version
import com.javiersc.semver.gradle.plugin.internal.Scope
import com.javiersc.semver.gradle.plugin.internal.appliedOnlyOnRootProject
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.generateVersionFile
import com.javiersc.semver.gradle.plugin.internal.lastSemVer
import com.javiersc.semver.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.tasks.CreateSemverTag
import com.javiersc.semver.gradle.plugin.tasks.PushSemverTag
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class SemVerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<SemVerExtension>(SemVerExtension.name)

        extension.tagPrefix.convention(SemVerExtension.defaultTagPrefix)

        CreateSemverTag.register(target)
        PushSemverTag.register(target)

        target.afterEvaluate { project: Project ->
            project.checkScopeIsCorrect()
            project.checkVersionIsHigherOrSame()

            project.version = project.calculatedVersion
            project.generateVersionFile(project.tagPrefix)

            project.gradle.projectsEvaluated {
                if (project.appliedOnlyOnRootProject) {
                    project.semverMessage("semver: ${project.version}")
                    project.allprojects { it.project.version = project.version }
                } else {
                    project.semverMessage("semver for ${project.name}: ${project.version}")
                }
            }
        }
    }
}

private fun Project.checkScopeIsCorrect() {
    check(scopeProperty in Scope.values().map(Scope::invoke) || scopeProperty.isNullOrBlank()) {
        "`scope` value must be one of ${Scope.values().map(Scope::invoke)} or empty"
    }
}

private fun Project.checkVersionIsHigherOrSame() {
    Version.safe(calculatedVersion).getOrNull()?.let { calculatedVersion ->
        check(calculatedVersion >= lastSemVer) {
            "Next version should be higher or the same than the current one"
        }
    }
}
