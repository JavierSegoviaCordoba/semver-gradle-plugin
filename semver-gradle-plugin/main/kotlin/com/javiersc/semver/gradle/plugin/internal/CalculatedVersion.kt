package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.semver.Version
import com.javiersc.semver.Version.Increase
import java.util.Date
import org.eclipse.jgit.api.Git

@Suppress("ComplexMethod")
internal fun Git.calculatedVersion(
    warningLastVersionIsNotHigherVersion: (last: Version?, higher: Version?) -> Unit,
    tagPrefix: String,
    stageProperty: String?,
    scopeProperty: String?,
    isCreatingSemverTag: Boolean,
    mockDate: Date?,
    checkClean: Boolean,
): String {
    val lastSemVer =
        lastVersionInCurrentBranch(
            warningLastVersionIsNotHigherVersion = warningLastVersionIsNotHigherVersion,
            tagPrefix = tagPrefix,
        )

    val previousStage: String? = lastSemVer.stage?.name

    val incStage =
        (stageProperty ?: previousStage ?: "").run {
            when {
                equals(Stage.Auto(), true) && !previousStage.isNullOrBlank() -> previousStage
                equals(Stage.Auto(), true) -> ""
                else -> this
            }
        }
    val calculatedVersion: String =
        when {
            (stageProperty.isNullOrBlank() &&
                scopeProperty.isNullOrBlank() &&
                !isCreatingSemverTag) || status().call().isClean.not() && checkClean -> {
                "$lastSemVer${calculateAdditionalVersionData(tagPrefix, mockDate, checkClean)}"
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
                    !isThereVersionTag(tagPrefix) -> "$lastSemVer"
                    incStage.isEmpty() -> "${lastSemVer.inc(Increase.Patch, incStage)}"
                    else -> "${lastSemVer.inc(stageName = incStage)}"
                }
            }
            isCreatingSemverTag && !isThereVersionTag(tagPrefix) -> "$lastSemVer"
            else -> "${lastSemVer.inc(stageName = incStage)}"
        }.also {
            if (it.contains("final", true) || it.contains("auto", true)) {
                error("`stage` plus `scope` combination is broken, please report it")
            }
        }

    return calculatedVersion
}

internal fun Git.calculateAdditionalVersionData(
    tagPrefix: String,
    mockDate: Date?,
    checkClean: Boolean = true,
): String {
    val isClean = status().call().isClean || !checkClean

    val commitsBetweenCurrentAndLastTagCommit =
        commitsBetweenTwoCommitsIncludingLastExcludingFirst(
            lastCommitInCurrentBranch,
            lastVersionCommitInCurrentBranch(tagPrefix)
        )

    val noVersionTags = isThereVersionTag(tagPrefix).not()

    val additionalData: String =
        commitsBetweenCurrentAndLastTagCommit.run {
            when {
                noVersionTags && isClean -> {
                    ".$size+${headCommit.commit.hash.take(DEFAULT_SHORT_HASH_LENGTH)}"
                }
                noVersionTags && !isClean -> {
                    ".$size+${timestamp(mockDate)}"
                }
                isClean && isEmpty() -> {
                    ""
                }
                isNotEmpty() && isClean -> {
                    ".$size+${first().hash.take(DEFAULT_SHORT_HASH_LENGTH)}"
                }
                else -> ".$size+${timestamp(mockDate)}"
            }
        }

    return additionalData
}

internal const val DEFAULT_SHORT_HASH_LENGTH = 7