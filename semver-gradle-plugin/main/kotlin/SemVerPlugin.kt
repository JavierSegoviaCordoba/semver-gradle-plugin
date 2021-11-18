package com.javiersc.semver.gradle.plugin

import com.javiersc.semanticVersioning.Version
import com.javiersc.semver.gradle.plugin.internal.Scope
import com.javiersc.semver.gradle.plugin.internal.SemVerException
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

        target.version = VersionTooEarly(target)

        target.gradle.projectsEvaluated {
            target.checkScopeIsCorrect()
            target.checkVersionIsHigherOrSame()

            target.version = target.calculatedVersion
            target.generateVersionFile(target.tagPrefix)

            if (target.appliedOnlyOnRootProject) {
                target.semverMessage("semver: ${target.version}")
                target.allprojects { it.project.version = target.version }
            } else {
                target.semverMessage("semver for ${target.name}: ${target.version}")
            }
        }
    }
}

private class VersionTooEarly(private val project: Project) {

    override fun toString(): String =
        throw SemVerException(
            """
               |`semver` version in the project `${project.name}` is not available yet
               |Workarounds:
               |  - Use `project.afterEvaluate { project.version }`
               |  - Access to the version via Gradle tasks
               |  - Use `gradle.projectsEvaluated { project.version }`
            """.trimMargin()
        )
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
