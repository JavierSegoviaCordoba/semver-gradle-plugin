package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.Scope.Patch
import java.time.Instant
import java.util.Date
import org.gradle.api.Project

internal val Project.stageProperty: String?
    get() = properties[SemVerProperties.Stage.key]?.toString()

internal val Project.scopeProperty: String?
    get() = properties[SemVerProperties.Scope.key]?.toString()

internal val Project.mockDate: Date?
    get() =
        properties[SemVerProperties.MockDate.key]?.let { value ->
            checkNotNull(value.toString().toLongOrNull()) {
                "`semver.mockDateOfEpochSecond` must be a number"
            }
            Date.from(Instant.ofEpochSecond(value.toString().toLong()))
        }

internal enum class SemVerProperties(val key: String, val value: String?) {
    Stage("semver.stage", ""),
    Scope("semver.scope", Patch.value),
    MockDate("semver.mockDateOfEpochSecond", null),
}

internal enum class Scope(val value: String) {
    Major("major"),
    Minor("minor"),
    Patch("patch"),
}
