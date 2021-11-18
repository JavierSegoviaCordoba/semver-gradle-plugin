package com.javiersc.semver.gradle.plugin.internal

import java.time.Instant
import java.util.Date
import org.gradle.api.Project

internal val Project.tagPrefix: String
    get() = properties[SemVerProperties.TagPrefix.key]?.toString() ?: defaultTagPrefix

internal const val defaultTagPrefix = ""

internal val Project.stageProperty: String?
    get() =
        properties["$propertyPath:${SemVerProperties.Stage.key}"]?.toString()
            ?: properties[SemVerProperties.Stage.key]?.toString()

internal val Project.scopeProperty: String?
    get() =
        properties["$propertyPath:${SemVerProperties.Scope.key}"]?.toString()
            ?: properties[SemVerProperties.Scope.key]?.toString()

internal val Project.mockDate: Date?
    get() {
        val mockDate: String? =
            properties["$propertyPath:${SemVerProperties.MockDate.key}"]?.toString()
                ?: properties[SemVerProperties.MockDate.key]?.toString()
        return mockDate?.let { value ->
            checkNotNull(value.toLongOrNull()) {
                "`${SemVerProperties.MockDate.key}` must be a number"
            }
            Date.from(Instant.ofEpochSecond(value.toLong()))
        }
    }

private val Project.propertyPath: String
    get() =
        path
            .mapIndexedNotNull { index, char -> if (index == 0 && char == ':') null else char }
            .joinToString("")

internal enum class SemVerProperties(val key: String) {
    TagPrefix("semver.tagPrefix"),
    Stage("semver.stage"),
    Scope("semver.scope"),
    MockDate("semver.mockDateOfEpochSecond"),
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
