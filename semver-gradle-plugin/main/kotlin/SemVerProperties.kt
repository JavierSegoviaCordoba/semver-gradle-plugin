package com.javiersc.semver.gradle.plugin

import java.time.Instant
import java.util.Date
import org.gradle.api.Project

internal val Project.stageProperty: String?
    get() =
        if (project.hasSemVerPlugin && project != rootProject) {
            properties["$path:${SemVerProperties.Stage.key}"]?.toString()
        } else properties[SemVerProperties.Stage.key]?.toString()

internal val Project.scopeProperty: String?
    get() {
        return if (project.hasSemVerPlugin && project != rootProject) {
            properties["$path:${SemVerProperties.Scope.key}"]?.toString()
        } else {
            properties[SemVerProperties.Scope.key]?.toString()
        }
    }

internal val Project.mockDate: Date?
    get() {
        val mockDate: String? =
            if (project.hasSemVerPlugin && project != rootProject) {
                properties["$path:${SemVerProperties.MockDate.key}"]?.toString()
            } else properties[SemVerProperties.MockDate.key]?.toString()
        return mockDate?.let { value ->
            checkNotNull(value.toLongOrNull()) {
                "`${SemVerProperties.MockDate.key}` must be a number"
            }
            Date.from(Instant.ofEpochSecond(value.toLong()))
        }
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
    Auto("auto"),
}
