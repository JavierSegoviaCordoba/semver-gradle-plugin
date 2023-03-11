package com.javiersc.semver.project.gradle.plugin.internal

import java.util.Properties
import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal const val DefaultTagPrefix = ""

internal val Project.projectTagPrefixProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.ProjectTagPrefix)

internal val Project.tagPrefixProperty: Provider<String>
    get() =
        providers.provider {
            getSemverProperty(SemverProperty.TagPrefix).orNull ?: DefaultTagPrefix
        }

internal val Project.stageProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.Stage)

internal val Project.scopeProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.Scope)

internal val Project.remoteProperty: Provider<String>
    get() = getSemverProperty(SemverProperty.Remote)

internal val Project.checkCleanProperty: Provider<Boolean>
    get() =
        providers.provider {
            getSemverProperty(SemverProperty.CheckClean).orNull?.toBoolean() ?: true
        }

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
                    .apply {
                        projectDir.resolve("gradle.properties").inputStream().use { load(it) }
                    }
                    .getProperty(semverProperty.key)
            }
        }
        else -> {
            providers.gradleProperty(semverProperty.key)
        }
    }
