package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.semver.Version
import org.gradle.api.Project

internal fun Project.checkScopeCorrectness() {
    val scope = scopeProperty.orNull
    check(scope in Scope.values().map(Scope::invoke) || scope.isNullOrBlank()) {
        "`scope` value must be one of ${Scope.values().map(Scope::invoke)} or empty"
    }
}

internal fun checkVersionIsHigherOrSame(
    version: String,
    lastVersionInCurrentBranch: Version,
) {
    Version.safe(version).getOrNull()?.let { calculatedVersion ->
        check(calculatedVersion >= lastVersionInCurrentBranch) {
            "Next version should be higher or the same as the current one"
        }
    }
}
