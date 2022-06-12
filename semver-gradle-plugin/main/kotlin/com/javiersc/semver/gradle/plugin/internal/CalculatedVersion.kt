package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.semver.Version
import com.javiersc.semver.Version.Increase
import com.javiersc.semver.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst
import com.javiersc.semver.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.gradle.plugin.internal.git.isThereVersionTag
import com.javiersc.semver.gradle.plugin.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.lastVersionCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.lastVersionInCurrentBranch
import org.eclipse.jgit.api.Git

@Suppress("ComplexMethod")
internal fun Git.calculatedVersion(
    tagPrefix: String,
    stageProperty: String?,
    scopeProperty: String?,
    isCreatingSemverTag: Boolean,
    checkClean: Boolean,
): String {
    val lastSemver: Version = lastVersionInCurrentBranch(tagPrefix = tagPrefix)

    val previousStage: String? = lastSemver.stage?.name

    val incStage: String =
        (stageProperty ?: previousStage ?: "").run {
            when {
                equals(Stage.Auto(), true) && !previousStage.isNullOrBlank() -> previousStage
                equals(Stage.Auto(), true) -> ""
                else -> this
            }
        }

    val isNoStagedNoScopedNoCreatingSemverTag: Boolean =
        stageProperty.isNullOrBlank() && scopeProperty.isNullOrBlank() && !isCreatingSemverTag

    val isCleanAndShouldBeChecked: Boolean = status().call().isClean.not() && checkClean

    val calculatedVersion: String =
        when {
            isNoStagedNoScopedNoCreatingSemverTag || isCleanAndShouldBeChecked -> {
                "$lastSemver${calculateAdditionalVersionData(tagPrefix, checkClean)}"
            }
            stageProperty.equals(Stage.Snapshot(), ignoreCase = true) -> {
                when (scopeProperty) {
                    Scope.Major() -> "${lastSemver.nextSnapshotMajor()}"
                    Scope.Minor() -> "${lastSemver.nextSnapshotMinor()}"
                    Scope.Patch() -> "${lastSemver.nextSnapshotPatch()}"
                    else -> "${lastSemver.nextSnapshotPatch()}"
                }
            }
            stageProperty.equals(Stage.Final(), ignoreCase = true) -> {
                when (scopeProperty) {
                    Scope.Major() -> "${lastSemver.inc(Increase.Major, "")}"
                    Scope.Minor() -> "${lastSemver.inc(Increase.Minor, "")}"
                    Scope.Patch() -> "${lastSemver.inc(Increase.Patch, "")}"
                    else -> "${lastSemver.inc(stageName = "")}"
                }
            }
            scopeProperty == Scope.Major() -> "${lastSemver.inc(Increase.Major, incStage)}"
            scopeProperty == Scope.Minor() -> "${lastSemver.inc(Increase.Minor, incStage)}"
            scopeProperty == Scope.Patch() -> "${lastSemver.inc(Increase.Patch, incStage)}"
            scopeProperty == Scope.Auto() -> {
                when {
                    !isThereVersionTag(tagPrefix) -> "$lastSemver"
                    incStage.isEmpty() -> "${lastSemver.inc(Increase.Patch, incStage)}"
                    else -> "${lastSemver.inc(stageName = incStage)}"
                }
            }
            isCreatingSemverTag && !isThereVersionTag(tagPrefix) -> "$lastSemver"
            else -> "${lastSemver.inc(stageName = incStage)}"
        }.also {
            if (it.contains("final", true) || it.contains("auto", true)) {
                error("`stage` plus `scope` combination is broken, please report it")
            }
        }

    return calculatedVersion
}

internal fun Git.calculateAdditionalVersionData(
    tagPrefix: String,
    checkClean: Boolean = true,
): String {
    val isClean = status().call().isClean || !checkClean

    val commitsBetweenCurrentAndLastTagCommit: List<GitRef.Commit> =
        commitsBetweenTwoCommitsIncludingLastExcludingFirst(
            lastCommitInCurrentBranch,
            lastVersionCommitInCurrentBranch(tagPrefix)
        )

    val noVersionTags: Boolean = isThereVersionTag(tagPrefix).not()

    val additionalData: String =
        commitsBetweenCurrentAndLastTagCommit.run {
            when {
                noVersionTags && isClean -> {
                    ".$size+${headCommit.commit.hash.take(DEFAULT_SHORT_HASH_LENGTH)}"
                }
                noVersionTags && !isClean -> {
                    ".$size+${headCommit.commit.hash.take(DEFAULT_SHORT_HASH_LENGTH)}+DIRTY"
                }
                isNotEmpty() && isClean -> {
                    ".$size+${first().hash.take(DEFAULT_SHORT_HASH_LENGTH)}"
                }
                isEmpty() && isClean -> {
                    ""
                }
                else -> {
                    ".$size+DIRTY"
                }
            }
        }

    return additionalData
}

internal const val DEFAULT_SHORT_HASH_LENGTH = 7
