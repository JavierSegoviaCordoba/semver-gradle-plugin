package com.javiersc.semver.gradle.plugin

import com.javiersc.semanticVersioning.Version
import com.javiersc.semver.gradle.plugin.internal.Scope
import com.javiersc.semver.gradle.plugin.internal.appliedOnlyOnRootProject
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.generateVersionFile
import com.javiersc.semver.gradle.plugin.internal.lastSemVer
import com.javiersc.semver.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.internal.tagPrefix
import com.javiersc.semver.gradle.plugin.tasks.CreateSemverTag
import com.javiersc.semver.gradle.plugin.tasks.PushSemverTag
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SemVerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        CreateSemverTag.register(target)
        PushSemverTag.register(target)

        target.checkScopeIsCorrect()
        target.checkVersionIsHigherOrSame()

        target.version = target.calculatedVersion
        target.generateVersionFile(target.tagPrefix)

        if (target == target.rootProject) {
            target.allprojects { it.project.version = target.version }
        }

        target.gradle.projectsEvaluated {
            if (target.appliedOnlyOnRootProject) {
                target.semverMessage("semver: ${target.version}")
            } else {
                target.semverMessage("semver for ${target.name}: ${target.version}")
            }
        }

        target.afterEvaluate {}
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
