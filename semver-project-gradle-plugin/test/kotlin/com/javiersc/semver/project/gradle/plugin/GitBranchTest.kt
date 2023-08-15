package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.project.gradle.plugin.internal.git.currentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.headRef
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GitBranchTest {

    @Test
    internal fun `current branch`() {
        initialCommitAnd {
            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            git.headRef.name.shouldBe(git.currentBranch.refName)

            git.currentBranch.commits.map(GitRef.Commit::message) shouldBe
                listOf("Second commit", "Initial commit")
        }
    }
}
