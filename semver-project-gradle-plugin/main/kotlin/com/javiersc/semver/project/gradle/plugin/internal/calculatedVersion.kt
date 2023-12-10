package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Major
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Minor
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Patch
import com.javiersc.gradle.version.GradleVersionException
import com.javiersc.gradle.version.isFinal
import com.javiersc.gradle.version.isSnapshot
import com.javiersc.kotlin.stdlib.isNotNullNorBlank
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst

@Suppress("ComplexMethod")
internal fun calculatedVersion(
    lastSemver: GradleVersion,
    stageProperty: String?,
    scopeProperty: String?,
    isCreatingSemverTag: Boolean,
    versionTagsInBranch: List<String>,
    clean: Boolean,
    checkClean: Boolean,
    force: Boolean,
    lastCommitInCurrentBranch: String?,
    commitsInCurrentBranch: List<String>,
    headCommit: String,
    lastVersionCommitInCurrentBranch: String?,
): String {
    if (lastSemver.isSnapshot) {
        gradleVersionError {
            "A version tag with the stage `SNAPSHOT` must not exist, last semver: $lastSemver"
        }
    }
    val isClean: Boolean = clean || !checkClean
    val isDirty: Boolean = !isClean

    val lastStageNameSanitized: String? =
        if (lastSemver.stageName?.isSnapshot == true) "SNAPSHOT" else lastSemver.stageName
    val lastSemverInBranch: GradleVersion =
        lastSemver.copy(
            stageName = lastStageNameSanitized,
            commits = null,
            hash = null,
            metadata = null
        )

    val currentStage: GradleVersion.Stage = lastSemverInBranch.stage ?: GradleVersion.Stage("final")
    val currentStageName: String = currentStage.name

    val providedStageName: String? =
        stageProperty?.let { name -> if (name.isSnapshot) "SNAPSHOT" else name }

    val incStage: String =
        (providedStageName ?: currentStageName).run {
            when {
                isAuto && currentStageName.isNotNullNorBlank() -> currentStageName
                isAuto -> "final"
                else -> this
            }
        }

    check(incStage.isNotBlank()) { "The stage provided cannot be blank" }

    val hasSameStage: Boolean = currentStageName == incStage

    val isNoStagedNoScopedNoCreatingSemverTag: Boolean =
        providedStageName.isNullOrBlank() && scopeProperty.isNullOrBlank() && !isCreatingSemverTag

    val isNoStagedNoScopedCreatingSemverTag: Boolean =
        providedStageName.isNullOrBlank() && scopeProperty.isNullOrBlank() && isCreatingSemverTag

    val isFinalStageWithoutAutoScope: Boolean =
        hasSameStage && currentStageName.isFinal && !scopeProperty.isAuto

    val isSameStageWithAutoOrNullScope: Boolean =
        hasSameStage && scopeProperty.isNotNullNorBlank() && !scopeProperty.isAuto

    val isProvidingHigherStage: Boolean =
        providedStageName?.isIsHigherStageThan(currentStage) ?: false

    val isProvidingHigherStageWithoutAutoScope: Boolean =
        isProvidingHigherStage &&
            scopeProperty.isNotNullNorBlank() &&
            !scopeProperty.isAuto &&
            !force

    val currentStageIsFinalWithProvidedScopeAndHasCommits: Boolean =
        currentStage.isFinal &&
            scopeProperty.isNotNullNorBlank() &&
            commitsInCurrentBranch.isNotEmpty()

    val isProvidingLowerStage: Boolean =
        !isProvidingHigherStage &&
            !hasSameStage &&
            !currentStageIsFinalWithProvidedScopeAndHasCommits

    val reportBrokenCompilation: () -> Nothing = {
        error(
            """ |This `stage` plus `scope` combination is broken, please report it:
                |  - stageProperty: $stageProperty
                |  - scopeProperty: $scopeProperty
                |  - isCreatingSemverTag: $isCreatingSemverTag
                |  - lastSemverMajorInCurrentBranch: ${lastSemver.major}
                |  - lastSemverMinorInCurrentBranch: ${lastSemver.minor}
                |  - lastSemverPatchInCurrentBranch: ${lastSemver.patch}
                |  - lastSemverStageInCurrentBranch: ${lastSemver.stageName}
                |  - lastSemverNumInCurrentBranch: ${lastSemver.stageNum}
                |  - versionTagsInBranch: $versionTagsInBranch
                |  - clean: $clean
                |  - checkClean: $checkClean
                |  - lastCommitInCurrentBranch: $lastCommitInCurrentBranch
                |  - commitsInCurrentBranch: $commitsInCurrentBranch
                |  - headCommit: $headCommit
                |  - lastVersionCommitInCurrentBranch: $lastVersionCommitInCurrentBranch
            """
                .trimMargin(),
        )
    }

    val calculatedVersion: String =
        when {
            isCreatingSemverTag && isDirty -> {
                gradleVersionError { "A semver tag can't be created if the repo is not clean" }
            }
            isNoStagedNoScopedCreatingSemverTag -> {
                gradleVersionError {
                    "A semver tag can't be created if neither stage nor scope is provided"
                }
            }
            isNoStagedNoScopedNoCreatingSemverTag || isDirty -> {
                val additionalVersionData: AdditionalVersionData? =
                    calculateAdditionalVersionData(
                        clean = clean,
                        checkClean = checkClean,
                        lastCommitInCurrentBranch = lastCommitInCurrentBranch,
                        commitsInCurrentBranch = commitsInCurrentBranch,
                        isThereVersionTags = versionTagsInBranch.isNotEmpty(),
                        headCommit = headCommit,
                        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch,
                    )
                val commits: Int? = additionalVersionData?.commits
                val hash: String? = additionalVersionData?.hash
                val metadata: String? = additionalVersionData?.metadata
                lastSemverInBranch
                    .copy(commits = commits, hash = hash, metadata = metadata)
                    .toString()
            }
            isSameStageWithAutoOrNullScope && !isFinalStageWithoutAutoScope && !force -> {
                throwHigherStageException(lastSemverInBranch, scopeProperty, stageProperty)
            }
            isProvidingHigherStageWithoutAutoScope && !force -> {
                throwHigherStageException(lastSemverInBranch, scopeProperty, stageProperty)
            }
            isProvidingLowerStage && !force -> {
                throwHigherStageException(lastSemverInBranch, scopeProperty, stageProperty)
            }
            scopeProperty.isMajor -> "${lastSemverInBranch.inc(Major, incStage)}"
            scopeProperty.isMinor -> "${lastSemverInBranch.inc(Minor, incStage)}"
            scopeProperty.isPatch -> "${lastSemverInBranch.inc(Patch, incStage)}"
            scopeProperty.isAuto -> {
                val currentStageIsFinalOrSnapshot: Boolean =
                    (currentStage.isFinal || currentStage.isSnapshot)
                when {
                    versionTagsInBranch.isEmpty() -> {
                        "$lastSemverInBranch"
                    }
                    hasSameStage && currentStageIsFinalOrSnapshot -> {
                        "${lastSemverInBranch.inc(Patch, incStage)}"
                    }
                    isProvidingHigherStage && !currentStageIsFinalOrSnapshot -> {
                        "${lastSemverInBranch.inc(null, incStage)}"
                    }
                    hasSameStage -> {
                        "${lastSemverInBranch.inc(null, incStage)}"
                    }
                    force -> {
                        "${lastSemverInBranch.inc(null, incStage)}"
                    }
                    currentStageIsFinalWithProvidedScopeAndHasCommits -> {
                        "${lastSemverInBranch.inc(null, incStage)}"
                    }
                    else -> {
                        reportBrokenCompilation() // "${lastSemverInBranch.inc(Patch, incStage)}"
                    }
                }
            }
            isCreatingSemverTag && versionTagsInBranch.isEmpty() -> {
                "$lastSemverInBranch"
            }
            isCreatingSemverTag && hasSameStage && currentStageName.isFinal -> {
                "${lastSemverInBranch.inc(Patch, incStage)}"
            }
            providedStageName != null && hasSameStage && currentStageName.isFinal -> {
                if (force) "${lastSemverInBranch.inc(Patch, incStage)}"
                else throwFinalVersionWithAutoStageAndNullScopeException(lastSemverInBranch)
            }
            else -> {
                "${lastSemverInBranch.inc(stageName = incStage)}"
            }
        }.also {
            if (it.contains("auto", true)) {
                reportBrokenCompilation()
            }
        }

    return calculatedVersion
}

internal fun calculateAdditionalVersionData(
    clean: Boolean,
    checkClean: Boolean,
    lastCommitInCurrentBranch: String?,
    commitsInCurrentBranch: List<String>,
    isThereVersionTags: Boolean,
    headCommit: String,
    lastVersionCommitInCurrentBranch: String?,
): AdditionalVersionData? {
    val isClean: Boolean = clean || !checkClean
    val isDirty: Boolean = !isClean

    val commitsBetweenCurrentAndLastTagCommit: List<String> =
        commitsBetweenTwoCommitsIncludingLastExcludingFirst(
            lastCommitInCurrentBranch,
            lastVersionCommitInCurrentBranch,
            commitsInCurrentBranch,
        )

    val additionalData: AdditionalVersionData? =
        commitsBetweenCurrentAndLastTagCommit.run {
            val commitsNumber: Int =
                count().takeIf { isThereVersionTags } ?: commitsInCurrentBranch.count()
            val hashLength: Int = DEFAULT_SHORT_HASH_LENGTH
            when {
                !isThereVersionTags && isDirty -> {
                    AdditionalVersionData(commitsNumber, headCommit.take(hashLength), "DIRTY")
                }
                !isThereVersionTags -> {
                    AdditionalVersionData(commitsNumber, headCommit.take(hashLength), null)
                }
                isNotEmpty() && isClean -> {
                    AdditionalVersionData(commitsNumber, first().take(hashLength), null)
                }
                isEmpty() && isClean -> {
                    null
                }
                else -> {
                    AdditionalVersionData(commitsNumber, null, "DIRTY")
                }
            }
        }

    return additionalData
}

private fun throwFinalVersionWithAutoStageAndNullScopeException(
    lastVersion: GradleVersion
): Nothing {
    val message: String =
        """ | The stage `auto` or `final` can't be applied if the last version is a `final` version and the scope is `null`.
            | Pass a valid scope or use `-P semver.force=true` to force the version.
            |   - Last version: $lastVersion
        """
            .trimMargin()
    gradleVersionError { message }
}

private fun throwHigherStageException(
    lastVersion: GradleVersion,
    providedScope: String?,
    providedStage: String?,
): Nothing {
    val message: String =
        """ |The stage `$providedStage` can't be combined with the scope `$providedScope` in the current state.
            |This prevents generating a higher version than the current one, $lastVersion, with a different patch, minor or major.
            |Use `-P semver.force=true` to force creating a higher version with the scope `$providedScope`.
            |For example:
            |  - `1.0.0-beta.1` -> `1.1.0-beta.1` // `scope=minor`, `stage=beta`
            |   
        """
            .trimMargin()
    gradleVersionError { message }
}

private fun String.isIsHigherStageThan(currentStage: GradleVersion.Stage): Boolean =
    when {
        isAuto -> false
        isFinal || isSnapshot -> GradleVersion.Stage(this) > currentStage
        else -> GradleVersion.Stage(this, currentStage.num ?: 1) > currentStage
    }

internal data class AdditionalVersionData(
    val commits: Int,
    val hash: String?,
    val metadata: String?,
) {
    fun asString(): String = buildString {
        append(".")
        append(commits)
        if (hash != null) {
            append("+")
            append(hash)
        }
        if (metadata != null) {
            append("+")
            append(metadata)
        }
    }
}

private fun gradleVersionError(message: () -> String): Nothing =
    throw GradleVersionException(message())

private val String?.isMajor: Boolean
    get() = equals(Scope.Major(), true)

private val String?.isMinor: Boolean
    get() = equals(Scope.Minor(), true)

private val String?.isPatch: Boolean
    get() = equals(Scope.Patch(), true)

private val String?.isAuto: Boolean
    get() = equals(Stage.Auto(), true)

internal const val DEFAULT_SHORT_HASH_LENGTH = 7
