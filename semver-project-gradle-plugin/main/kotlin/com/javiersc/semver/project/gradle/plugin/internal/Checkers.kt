package com.javiersc.semver.project.gradle.plugin.internal

import org.gradle.api.Project

internal fun Project.checkScopeCorrectness() {
    val scope = scopeProperty.orNull
    check(scope in Scope.values().map(Scope::invoke) || scope.isNullOrBlank()) {
        "`scope` value must be one of ${Scope.values().map(Scope::invoke)} or empty"
    }
}
