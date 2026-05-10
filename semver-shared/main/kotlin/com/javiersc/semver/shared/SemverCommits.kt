package com.javiersc.semver.shared

import com.javiersc.semver.shared.internal.git.GitCache
import com.javiersc.semver.shared.internal.semverWarningMessage
import java.io.File
import org.gradle.api.provider.Provider

public fun semverCommits(gitDir: File?, commitsMaxCount: Provider<Int>): List<Commit> {
    val existingGitDir: File =
        gitDir?.takeIf { it.exists() }
            ?: run {
                semverWarningMessage("There is no git directory")
                return emptyList()
            }
    return GitCache(gitDir = existingGitDir, maxCount = commitsMaxCount)
        .commitsInTheCurrentBranchPublicApi
}
