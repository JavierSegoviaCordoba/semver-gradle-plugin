package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.internal.Scope
import com.javiersc.semver.gradle.plugin.internal.appliedOnlyOnRootProject
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.checkCleanProperty
import com.javiersc.semver.gradle.plugin.internal.generateVersionFile
import com.javiersc.semver.gradle.plugin.internal.git
import com.javiersc.semver.gradle.plugin.internal.hasCommits
import com.javiersc.semver.gradle.plugin.internal.hasGit
import com.javiersc.semver.gradle.plugin.internal.lastVersionInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.mockDateProperty
import com.javiersc.semver.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.internal.stageProperty
import com.javiersc.semver.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.gradle.plugin.internal.warningLastVersionIsNotHigherVersion
import com.javiersc.semver.gradle.plugin.tasks.CreateSemverTag
import com.javiersc.semver.gradle.plugin.tasks.PushSemverTag
import com.javiersc.semver.gradle.plugin.tasks.isCreatingSemverTag
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SemVerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        when {
            target.hasGit.not() -> {
                target.semverMessage(
                    "semver plugin can't work if the project is not a git repository"
                )
            }
            target.hasCommits.not() -> {
                target.semverMessage("semver plugin can't work if the project has no commits")
            }
            else -> {
                CreateSemverTag.register(target)
                PushSemverTag.register(target)

                target.checkScopeIsCorrect()
                target.checkVersionIsHigherOrSame()

                target.version = target.calculatedVersion
                target.generateVersionFile()

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
        check(calculatedVersion >= lastVersionInCurrentBranch) {
            "Next version should be higher or the same than the current one"
        }
    }
}

private val Project.calculatedVersion: String
    get() =
        git.calculatedVersion(
            warningLastVersionIsNotHigherVersion = ::warningLastVersionIsNotHigherVersion,
            tagPrefix = tagPrefixProperty,
            stageProperty = stageProperty,
            scopeProperty = scopeProperty,
            isCreatingSemverTag = isCreatingSemverTag,
            mockDate = mockDateProperty,
            checkClean = checkCleanProperty,
        )

private val Project.lastVersionInCurrentBranch: Version
    get() =
        git.lastVersionInCurrentBranch(
            warningLastVersionIsNotHigherVersion = ::warningLastVersionIsNotHigherVersion,
            tagPrefix = tagPrefixProperty,
        )
