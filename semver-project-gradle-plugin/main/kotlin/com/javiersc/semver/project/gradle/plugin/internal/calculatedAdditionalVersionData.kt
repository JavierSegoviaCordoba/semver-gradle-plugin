package com.javiersc.semver.project.gradle.plugin.internal

import com.javiersc.semver.project.gradle.plugin.internal.AdditionalVersionData.Companion.DEFAULT_SHORT_HASH_LENGTH
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst

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

    companion object {
        const val DEFAULT_SHORT_HASH_LENGTH: Int = 7
    }
}
