package com.javiersc.semver.project.gradle.plugin.internal

import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal const val DefaultTagPrefix = ""

internal val Project.projectTagPrefixProperty: Provider<String>
    get() = providers.provider { properties[SemverProperties.ProjectTagPrefix.key]?.toString() }

internal val Project.tagPrefixProperty: Provider<String>
    get() =
        providers.provider {
            properties[SemverProperties.TagPrefix.key]?.toString() ?: DefaultTagPrefix
        }

internal val Project.stageProperty: Provider<String>
    get() = providers.provider { properties[SemverProperties.Stage.key]?.toString() }

internal val Project.scopeProperty: Provider<String>
    get() = providers.provider { properties[SemverProperties.Scope.key]?.toString() }

internal val Project.remoteProperty: Provider<String>
    get() = providers.provider { properties[SemverProperties.Remote.key]?.toString() }

internal val Project.checkCleanProperty: Provider<Boolean>
    get() =
        providers.provider {
            properties[SemverProperties.CheckClean.key]?.toString()?.toBoolean() ?: true
        }

internal val Project.commitsMaxCount: Provider<Int>
    get() =
        providers.provider { properties[SemverProperties.CommitsMaxCount.key]?.toString()?.toInt() }

internal enum class SemverProperties(val key: String) {
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
