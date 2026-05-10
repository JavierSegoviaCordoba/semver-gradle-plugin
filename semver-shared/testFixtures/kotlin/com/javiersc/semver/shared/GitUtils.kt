package com.javiersc.semver.shared

import com.javiersc.semver.shared.internal.AdditionalVersionData
import com.javiersc.semver.shared.internal.calculateAdditionalVersionData
import com.javiersc.semver.shared.internal.git.GitRef
import com.javiersc.semver.shared.internal.git.commitsInCurrentBranch
import com.javiersc.semver.shared.internal.git.headCommit
import com.javiersc.semver.shared.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.shared.internal.git.lastVersionCommitInCurrentBranch
import com.javiersc.semver.shared.internal.git.versionTagsInCurrentBranch
import org.eclipse.jgit.api.Git

public fun Git.calculateAdditionalVersionData(
    tagPrefix: String,
    checkIsClean: Boolean = true,
): AdditionalVersionData? =
    calculateAdditionalVersionData(
        clean = status().call().isClean,
        checkClean = checkIsClean,
        lastCommitInCurrentBranch = lastCommitInCurrentBranch?.hash,
        commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
        isThereVersionTags = versionTagsInCurrentBranch(tagPrefix).isNotEmpty(),
        headCommit = headCommit.commit.hash,
        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch(tagPrefix)?.hash,
    )
