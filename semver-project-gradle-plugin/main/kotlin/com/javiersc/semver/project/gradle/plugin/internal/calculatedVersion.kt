package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Major
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Minor
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Patch
import com.javiersc.gradle.version.GradleVersionException
import com.javiersc.gradle.version.isFinal
import com.javiersc.gradle.version.isSnapshot
import com.javiersc.kotlin.stdlib.isNotNullNorBlank

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

    val hasSameFinalStage: Boolean = currentStageName == incStage && currentStageName.isFinal

    val isNotAutoBlankScope: Boolean = !scopeProperty.isAuto && scopeProperty.isNotNullNorBlank()

    val isBlankScope: Boolean = scopeProperty.isNullOrBlank()

    val isBlankStageAndScope: Boolean = providedStageName.isNullOrBlank() && isBlankScope

    val hasSameFinalStageWithoutAutoScope: Boolean = hasSameFinalStage && !scopeProperty.isAuto

    val isProvidingHigherStage: Boolean =
        providedStageName?.isIsHigherStageThan(currentStage) ?: false

    val isProvidingHigherStageWithoutAutoScope: Boolean =
        isProvidingHigherStage && isNotAutoBlankScope && !force

    val currentStageIsFinalWithProvidedScopeAndHasCommits: Boolean =
        currentStage.isFinal && !isBlankScope && commitsInCurrentBranch.isNotEmpty()

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
            isBlankStageAndScope && isCreatingSemverTag -> {
                gradleVersionError {
                    "A semver tag can't be created if neither stage nor scope is provided"
                }
            }
            isBlankStageAndScope || isDirty -> {
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
            !currentStage.isFinal && !scopeProperty.isAuto && stageProperty.isAuto -> {
                throwHigherStageException(lastSemverInBranch, scopeProperty, stageProperty)
            }
            hasSameStage && isNotAutoBlankScope && !hasSameFinalStageWithoutAutoScope && !force -> {
                throwHigherStageException(lastSemverInBranch, scopeProperty, stageProperty)
            }
            hasSameFinalStage && isBlankScope && !force -> {
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
