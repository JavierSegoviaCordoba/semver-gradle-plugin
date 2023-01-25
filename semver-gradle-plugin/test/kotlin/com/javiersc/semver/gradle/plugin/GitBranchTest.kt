package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.gradle.plugin.internal.git.currentBranch
import com.javiersc.semver.gradle.plugin.internal.git.headRef
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GitBranchTest {

    @Test
    fun `current branch`() {
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
