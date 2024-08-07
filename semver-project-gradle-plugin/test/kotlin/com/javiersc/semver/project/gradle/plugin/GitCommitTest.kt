package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.project.gradle.plugin.internal.git.commitHash
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsInCurrentBranchFullMessage
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsInCurrentBranchHash
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsInCurrentBranchRevCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.headRef
import com.javiersc.semver.project.gradle.plugin.internal.git.headRevCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.headRevCommitInBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.lastVersionCommitInCurrentBranch
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

internal class GitCommitTest {

    @Test
    fun `head commit`() {
        initialCommitAnd {
            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.headRevCommit.shortMessage.shouldBe("Second commit")
            git.headCommit.commit.message.shouldBe("Second commit")
            git.headRevCommitInBranch.fullMessage.shouldBe("Second commit")
        }
    }

    @Test
    fun `last commit in current branch`() {
        initialCommitAnd {
            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.lastCommitInCurrentBranch?.message.shouldBe("Second commit")
        }
    }

    @Test
    fun `commits in current branch`() {
        initialCommitAnd {
            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            val messages: List<String> = listOf("Second commit", "Initial commit")

            git.commitsInCurrentBranchRevCommit.map(RevCommit::getShortMessage).shouldBe(messages)
            git.commitsInCurrentBranch.map(GitRef.Commit::message).shouldBe(messages)
            git.commitsInCurrentBranchHash.shouldBe(
                git.commitsInCurrentBranch.map(GitRef.Commit::hash))
        }
    }

    @Test
    fun `commit hash`() {
        initialCommitAnd {
            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.commitHash(git.headRef).shouldBe(git.lastCommitInCurrentBranch?.hash)
            git.commitHash(git.headRevCommit.toObjectId())
                .shouldBe(git.lastCommitInCurrentBranch?.hash)
        }
    }

    @Test
    fun `commits between two commits`() {
        initialCommitAnd {
            fun initialCommit() = git.commitsInCurrentBranch.last()
            fun lastCommit() = git.commitsInCurrentBranch.first()

            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            val messages2: List<String> = listOf("Second commit")

            git.commitsBetweenTwoCommitsIncludingLastExcludingFirst(
                    fromCommit = initialCommit(),
                    toCommit = lastCommit(),
                )
                .map { hash ->
                    git.commitsInCurrentBranch.first { commit -> commit.hash == hash }.message
                }
                .shouldBe(messages2)

            resolve("Third commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Third commit").call()

            val messages3: List<String> = listOf("Third commit") + messages2

            git.commitsBetweenTwoCommitsIncludingLastExcludingFirst(
                    fromCommit = initialCommit(),
                    toCommit = lastCommit(),
                )
                .map { hash ->
                    git.commitsInCurrentBranch.first { commit -> commit.hash == hash }.message
                }
                .shouldBe(messages3)
        }
    }

    @Test
    fun `commits in current branch full message`() {
        initialCommitAnd {
            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit\n\nSecond commit full message").call()

            val messages: List<String> =
                listOf("Second commit\n\nSecond commit full message", "Initial commit")

            git.commitsInCurrentBranchFullMessage.shouldBe(messages)
        }
    }

    @Test
    fun `last version commit in current branch`() {
        initialCommitAnd {
            resolve("Second commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()
            git.tag().setName("v1.0.0").call()

            resolve("Third commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Third commit").call()

            git.lastVersionCommitInCurrentBranch(tagPrefix = "v")!!
                .message
                .shouldBe("Second commit")
        }
    }
}

private fun Git.commitsBetweenTwoCommitsIncludingLastExcludingFirst(
    fromCommit: GitRef.Commit?,
    toCommit: GitRef.Commit?,
): List<String> =
    commitsBetweenTwoCommitsIncludingLastExcludingFirst(
        fromCommit = fromCommit?.hash,
        toCommit = toCommit?.hash,
        commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
    )
