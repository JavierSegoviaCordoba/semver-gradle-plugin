package com.javiersc.semver.gradle.plugin.internal

import org.gradle.api.Project

internal val Project.projectTagPrefixProperty: String?
    get() = properties[SemverProperties.ProjectTagPrefix.key]?.toString()

internal val Project.tagPrefixProperty: String
    get() = properties[SemverProperties.TagPrefix.key]?.toString() ?: DefaultTagPrefix

internal const val DefaultTagPrefix = ""

internal val Project.stageProperty: String?
    get() = properties[SemverProperties.Stage.key]?.toString()

internal val Project.scopeProperty: String?
    get() = properties[SemverProperties.Scope.key]?.toString()

internal val Project.remoteProperty: String?
    get() = properties[SemverProperties.Remote.key]?.toString()

internal val Project.checkCleanProperty: Boolean
    get() = properties[SemverProperties.CheckClean.key]?.toString()?.toBoolean() ?: true

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
    Snapshot("snapshot");

    operator fun invoke(): String = value
}

internal enum class Scope(private val value: String) {
    Auto("auto"),
    Major("major"),
    Minor("minor"),
    Patch("patch");

    operator fun invoke(): String = value
}
