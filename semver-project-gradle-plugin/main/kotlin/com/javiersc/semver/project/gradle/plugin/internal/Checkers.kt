package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.gradle.version.GradleVersion
import org.gradle.api.Project

internal fun Project.checkScopeCorrectness() {
    val scope = scopeProperty.orNull
    check(scope in Scope.values().map(Scope::invoke) || scope.isNullOrBlank()) {
        "`scope` value must be one of ${Scope.values().map(Scope::invoke)} or empty"
    }
}

internal fun checkVersionIsHigherOrSame(
    version: String,
    lastVersionInCurrentBranch: GradleVersion,
) {
    GradleVersion.safe(version).getOrNull()?.let { calculatedVersion ->
        check(calculatedVersion >= lastVersionInCurrentBranch) {
            "The next version($calculatedVersion) should be higher or the same as the current " +
                "one($lastVersionInCurrentBranch)"
        }
    }
}
