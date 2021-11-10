package com.javiersc.semver.gradle.plugin

import java.time.Instant
import java.util.Date
import org.gradle.api.Project

internal val Project.stageProperty: String?
    get() =
        properties["$path:${SemVerProperties.Stage.key}"]?.toString()
            ?: properties[SemVerProperties.Stage.key]?.toString()

internal val Project.scopeProperty: String?
    get() =
        properties["$path:${SemVerProperties.Scope.key}"]?.toString()
            ?: properties[SemVerProperties.Scope.key]?.toString()

internal val Project.mockDate: Date?
    get() =
        (properties["$path:${SemVerProperties.MockDate.key}"]?.toString()
                ?: properties[SemVerProperties.MockDate.key])?.let { value ->
            checkNotNull(value.toString().toLongOrNull()) {
                "`${SemVerProperties.MockDate.key}` must be a number"
            }
            Date.from(Instant.ofEpochSecond(value.toString().toLong()))
        }

internal enum class SemVerProperties(val key: String) {
    Stage("semver.stage"),
    Scope("semver.scope"),
    MockDate("semver.mockDateOfEpochSecond"),
}

internal enum class Scope(val value: String) {
    Major("major"),
    Minor("minor"),
    Patch("patch"),
}
