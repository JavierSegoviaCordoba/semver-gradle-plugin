package com.javiersc.semver.project.gradle.plugin.internal

import java.util.Properties
import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal const val DefaultTagPrefix = ""

internal val Project.projectTagPrefixProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.ProjectTagPrefix)

internal val Project.tagPrefixProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.TagPrefix).orElse(DefaultTagPrefix)

internal val Project.stageProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.Stage)

internal val Project.scopeProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.Scope)

internal val Project.remoteProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.Remote)

internal val Project.checkCleanProperty: Provider<Boolean>
    get() = getSemverProperty(SemverProperty.CheckClean).map(String::toBoolean).orElse(true)

internal val Project.forceProperty: Provider<Boolean>
    get() = getSemverProperty(SemverProperty.Force).map(String::toBoolean).orElse(false)

internal val Project.commitsMaxCount: Provider<Int>
    get() = getSemverProperty(SemverProperty.CommitsMaxCount).map(String::toInt)

internal enum class SemverProperty(val key: String) {
    ProjectTagPrefix("semver.project.tagPrefix"),
    TagPrefix("semver.tagPrefix"),
    Stage("semver.stage"),
    Scope("semver.scope"),
    Remote("semver.remote"),
    CheckClean("semver.checkClean"),
    CommitsMaxCount("semver.commitsMaxCount"),
    Force("semver.force"),
}

internal enum class Stage(private val value: String) {
    Auto("auto"),
    Final("final"),
    ;

    operator fun invoke(): String = value
}

internal enum class Scope(private val value: String) {
    Auto("auto"),
    Major("major"),
    Minor("minor"),
    Patch("patch");

    operator fun invoke(): String = value
}

private fun Project.getSemverProperty(semverProperty: SemverProperty): Provider<String> =
    when {
        gradle.startParameter.projectProperties[semverProperty.key] != null -> {
            provider { gradle.startParameter.projectProperties[semverProperty.key] }
        }
        projectDir.resolve("gradle.properties").exists() -> {
            provider {
                Properties()
                    .apply { projectDir.resolve("gradle.properties").inputStream().use(::load) }
                    .getProperty(semverProperty.key)
            }
        }
        else -> {
            providers.gradleProperty(semverProperty.key)
        }
    }
