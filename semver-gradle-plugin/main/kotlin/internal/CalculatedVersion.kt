package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.semanticVersioning.Version.Increase
import com.javiersc.semver.gradle.plugin.tasks.isCreatingSemverTag
import org.gradle.api.Project

@Suppress("ComplexMethod")
internal val Project.calculatedVersion: String
    get() {
        val previousStage: String? = lastSemVer.stage?.name

        val incStage =
            (stageProperty ?: previousStage ?: "").run {
                when {
                    equals(Stage.Auto(), true) && !previousStage.isNullOrBlank() -> previousStage
                    equals(Stage.Auto(), true) -> ""
                    else -> this
                }
            }
        return when {
            (stageProperty.isNullOrBlank() &&
                scopeProperty.isNullOrBlank() &&
                !isCreatingSemverTag) || git.status().call().isClean.not() -> {
                "$lastSemVer${calculateAdditionalVersionData()}"
            }
            stageProperty.equals(Stage.Snapshot(), ignoreCase = true) -> {
                when (scopeProperty) {
                    Scope.Major() -> "${lastSemVer.nextSnapshotMajor()}"
                    Scope.Minor() -> "${lastSemVer.nextSnapshotMinor()}"
                    Scope.Patch() -> "${lastSemVer.nextSnapshotPatch()}"
                    else -> "${lastSemVer.nextSnapshotPatch()}"
                }
            }
            stageProperty.equals(Stage.Final(), ignoreCase = true) -> {
                when (scopeProperty) {
                    Scope.Major() -> "${lastSemVer.inc(Increase.Major, "")}"
                    Scope.Minor() -> "${lastSemVer.inc(Increase.Minor, "")}"
                    Scope.Patch() -> "${lastSemVer.inc(Increase.Patch, "")}"
                    else -> "${lastSemVer.inc(stageName = "")}"
                }
            }
            scopeProperty == Scope.Major() -> "${lastSemVer.inc(Increase.Major, incStage)}"
            scopeProperty == Scope.Minor() -> "${lastSemVer.inc(Increase.Minor, incStage)}"
            scopeProperty == Scope.Patch() -> "${lastSemVer.inc(Increase.Patch, incStage)}"
            scopeProperty == Scope.Auto() -> {
                when {
                    incStage.isEmpty() -> "${lastSemVer.inc(Increase.Patch, incStage)}"
                    else -> "${lastSemVer.inc(stageName = incStage)}"
                }
            }
            isCreatingSemverTag && git.tagsInCurrentBranch.isEmpty() -> "$lastSemVer"
            else -> "${lastSemVer.inc(stageName = incStage)}"
        }.also {
            if (it.contains("final", true) || it.contains("auto", true)) {
                error("Current `stage` plugs `scope` combination is broken, please report it")
            }
        }
    }
