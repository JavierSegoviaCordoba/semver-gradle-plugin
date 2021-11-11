package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.semanticVersioning.Version.Increase
import com.javiersc.semver.gradle.plugin.Scope
import com.javiersc.semver.gradle.plugin.lastSemVer
import com.javiersc.semver.gradle.plugin.scopeProperty
import com.javiersc.semver.gradle.plugin.stageProperty
import org.gradle.api.Project

@Suppress("ComplexMethod")
internal fun Project.calculatedVersion(isCreatingTag: Boolean): String {
    val incStage =
        (stageProperty ?: lastSemVer.stage?.name ?: "").run {
            if (equals("final", ignoreCase = true)) "" else this
        }
    return when {
        (stageProperty.isNullOrBlank() && scopeProperty.isNullOrBlank() && !isCreatingTag) ||
            git.status().call().isClean.not() -> {
            "$lastSemVer${calculateAdditionalVersionData()}"
        }
        stageProperty.equals("snapshot", ignoreCase = true) -> {
            when (scopeProperty) {
                Scope.Major.value -> "${lastSemVer.nextSnapshotMajor()}"
                Scope.Minor.value -> "${lastSemVer.nextSnapshotMinor()}"
                Scope.Patch.value -> "${lastSemVer.nextSnapshotPatch()}"
                else -> "${lastSemVer.nextSnapshotPatch()}"
            }
        }
        scopeProperty == Scope.Major.value -> "${lastSemVer.inc(Increase.Major, incStage)}"
        scopeProperty == Scope.Minor.value -> "${lastSemVer.inc(Increase.Minor, incStage)}"
        scopeProperty == Scope.Patch.value -> "${lastSemVer.inc(Increase.Patch, incStage)}"
        scopeProperty == Scope.Auto.value -> {
            if (incStage.isEmpty()) "${lastSemVer.inc(Increase.Patch, incStage)}"
            else "${lastSemVer.inc(stageName = incStage)}"
        }
        isCreatingTag && git.tagsInCurrentBranch.isEmpty() -> "$lastSemVer"
        else -> "${lastSemVer.inc(stageName = incStage)}"
    }
}
