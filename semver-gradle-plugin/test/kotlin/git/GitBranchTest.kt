package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semver.gradle.plugin.addNewFile
import com.javiersc.semver.gradle.plugin.git
import com.javiersc.semver.gradle.plugin.initialCommitAnd
import com.javiersc.semver.gradle.plugin.internal.GitRef
import com.javiersc.semver.gradle.plugin.internal.currentBranch
import com.javiersc.semver.gradle.plugin.internal.headRef
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GitBranchTest {

    @Test
    fun `current branch`() {
        initialCommitAnd {
            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.headRef.name.shouldBe(git.currentBranch.refName)

            git.currentBranch.commits.map(GitRef.Commit::message) shouldBe
                listOf("Second commit", "Initial commit")
        }
    }
}
