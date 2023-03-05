package com.javiersc.semver.gradle.plugin.internal

import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal const val DefaultTagPrefix = ""

internal val Project.projectTagPrefixProperty: Provider<String>
    get() = providers.gradleProperty(SemverProperties.ProjectTagPrefix.key)

internal val Project.tagPrefixProperty: Provider<String>
    get() = provider {
        providers.gradleProperty(SemverProperties.TagPrefix.key).orNull?.toString()
            ?: DefaultTagPrefix
    }

internal val Project.stageProperty: Provider<String>
    get() = providers.gradleProperty(SemverProperties.Stage.key)

internal val Project.scopeProperty: Provider<String>
    get() = providers.gradleProperty(SemverProperties.Scope.key)

internal val Project.remoteProperty: Provider<String>
    get() = providers.gradleProperty(SemverProperties.Remote.key)

internal val Project.checkCleanProperty: Provider<Boolean>
    get() = provider {
        providers.gradleProperty(SemverProperties.CheckClean.key).orNull?.toBoolean() ?: true
    }

internal enum class SemverProperties(val key: String) {
    ProjectTagPrefix("semver.project.tagPrefix"),
    TagPrefix("semver.tagPrefix"),
    Stage("semver.stage"),
    Scope("semver.scope"),
    Remote("semver.remote"),
    CheckClean("semver.checkClean"),
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
