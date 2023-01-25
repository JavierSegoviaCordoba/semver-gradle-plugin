package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.gradle.plugin.internal.git.commitsInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.gradle.plugin.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.lastVersionCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.versionTagsInCurrentBranch
import org.eclipse.jgit.api.Git

fun Git.calculateAdditionalVersionData(
    tagPrefix: String,
    checkIsClean: Boolean = true,
): String =
    com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData(
        clean = status().call().isClean,
        checkClean = checkIsClean,
        lastCommitInCurrentBranch = lastCommitInCurrentBranch?.hash,
        commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
        isThereVersionTags = versionTagsInCurrentBranch(tagPrefix).isNotEmpty(),
        headCommit = headCommit.commit.hash,
        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch(tagPrefix)?.hash,
    )
