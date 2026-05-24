package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.shared.git
import com.javiersc.semver.shared.initialCommitAnd
import com.javiersc.semver.shared.internal.git.GitRef
import com.javiersc.semver.shared.internal.git.currentBranch
import com.javiersc.semver.shared.internal.git.headRef
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GitBranchTest {

    @Test
    internal fun `current branch`() {
        initialCommitAnd {
            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.headRef.name.shouldBe(git.currentBranch.refName)

            git.currentBranch.commits.map(GitRef.Commit::message) shouldBe
                listOf("Second commit", "Initial commit")
        }
    }
}
