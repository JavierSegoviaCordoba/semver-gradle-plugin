package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.gradle.version.GradleVersion.Increase
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst

@Suppress("ComplexMethod")
internal fun calculatedVersion(
    stageProperty: String?,
    scopeProperty: String?,
    isCreatingSemverTag: Boolean,
    lastSemverMajorInCurrentBranch: Int,
    lastSemverMinorInCurrentBranch: Int,
    lastSemverPatchInCurrentBranch: Int,
    lastSemverStageInCurrentBranch: String?,
    lastSemverNumInCurrentBranch: Int?,
    versionTagsInCurrentBranch: List<String>,
    clean: Boolean,
    checkClean: Boolean,
    lastCommitInCurrentBranch: String?,
    commitsInCurrentBranch: List<String>,
    headCommit: String,
    lastVersionCommitInCurrentBranch: String?,
): String {
    val isClean: Boolean = clean || !checkClean
    val isDirty: Boolean = !isClean

    val stagePropertySanitized =
        stageProperty?.let { name ->
            if (name.equals("SNAPSHOT", ignoreCase = true)) "SNAPSHOT" else name
        }

    val lastSemverStageInCurrentBranchSanitized =
        lastSemverStageInCurrentBranch?.let { name ->
            if (name.equals("SNAPSHOT", ignoreCase = true)) "SNAPSHOT" else name
        }

    val lastSemverInCurrentBranch =
        GradleVersion(
            major = lastSemverMajorInCurrentBranch,
            minor = lastSemverMinorInCurrentBranch,
            patch = lastSemverPatchInCurrentBranch,
            stageName = lastSemverStageInCurrentBranchSanitized,
            stageNum = lastSemverNumInCurrentBranch,
        )

    val previousStage: String? = lastSemverInCurrentBranch.stage?.name

    val incStage: String =
        (stagePropertySanitized ?: previousStage ?: "").run {
            when {
                equals(Stage.Auto(), true) && !previousStage.isNullOrBlank() -> previousStage
                equals(Stage.Auto(), true) -> ""
                else -> this
            }
        }

    val isNoStagedNoScopedNoCreatingSemverTag: Boolean =
        stagePropertySanitized.isNullOrBlank() &&
            scopeProperty.isNullOrBlank() &&
            !isCreatingSemverTag

    val calculatedVersion: String =
        when {
            isNoStagedNoScopedNoCreatingSemverTag || isDirty -> {
                val additionalVersionData: String =
                    calculateAdditionalVersionData(
                        clean = clean,
                        checkClean = checkClean,
                        lastCommitInCurrentBranch = lastCommitInCurrentBranch,
                        commitsInCurrentBranch = commitsInCurrentBranch,
                        isThereVersionTags = versionTagsInCurrentBranch.isNotEmpty(),
                        headCommit = headCommit,
                        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch,
                    )
                "$lastSemverInCurrentBranch$additionalVersionData"
            }
            stagePropertySanitized.equals(Stage.Final(), ignoreCase = true) -> {
                when (scopeProperty) {
                    Scope.Major() -> "${lastSemverInCurrentBranch.inc(Increase.Major, "")}"
                    Scope.Minor() -> "${lastSemverInCurrentBranch.inc(Increase.Minor, "")}"
                    Scope.Patch() -> "${lastSemverInCurrentBranch.inc(Increase.Patch, "")}"
                    else -> "${lastSemverInCurrentBranch.inc(stageName = "")}"
                }
            }
            scopeProperty == Scope.Major() -> {
                "${lastSemverInCurrentBranch.inc(Increase.Major, incStage)}"
            }
            scopeProperty == Scope.Minor() -> {
                "${lastSemverInCurrentBranch.inc(Increase.Minor, incStage)}"
            }
            scopeProperty == Scope.Patch() -> {
                "${lastSemverInCurrentBranch.inc(Increase.Patch, incStage)}"
            }
            scopeProperty == Scope.Auto() -> {
                when {
                    versionTagsInCurrentBranch.isEmpty() -> "$lastSemverInCurrentBranch"
                    incStage.isEmpty() ->
                        "${lastSemverInCurrentBranch.inc(Increase.Patch, incStage)}"
                    else -> "${lastSemverInCurrentBranch.inc(stageName = incStage)}"
                }
            }
            isCreatingSemverTag && versionTagsInCurrentBranch.isEmpty() -> {
                "$lastSemverInCurrentBranch"
            }
            else -> {
                "${lastSemverInCurrentBranch.inc(stageName = incStage)}"
            }
        }.also {
            if (it.contains("final", true) || it.contains("auto", true)) {
                error("`stage` plus `scope` combination is broken, please report it")
            }
        }

    return calculatedVersion
}

internal fun calculateAdditionalVersionData(
    clean: Boolean = true,
    checkClean: Boolean = true,
    lastCommitInCurrentBranch: String?,
    commitsInCurrentBranch: List<String>,
    isThereVersionTags: Boolean,
    headCommit: String,
    lastVersionCommitInCurrentBranch: String?,
): String {
    val isClean: Boolean = clean || !checkClean
    val isDirty: Boolean = !isClean

    val commitsBetweenCurrentAndLastTagCommit: List<String> =
        commitsBetweenTwoCommitsIncludingLastExcludingFirst(
            lastCommitInCurrentBranch,
            lastVersionCommitInCurrentBranch,
            commitsInCurrentBranch,
        )

    val additionalData: String =
        commitsBetweenCurrentAndLastTagCommit.run {
            val hashLength = DEFAULT_SHORT_HASH_LENGTH
            when {
                !isThereVersionTags && isDirty -> ".$size+${headCommit.take(hashLength)}+DIRTY"
                !isThereVersionTags -> ".$size+${headCommit.take(hashLength)}"
                isNotEmpty() && isClean -> ".$size+${first().take(hashLength)}"
                isEmpty() && isClean -> ""
                else -> ".$size+DIRTY"
            }
        }

    return additionalData
}

internal const val DEFAULT_SHORT_HASH_LENGTH = 7
