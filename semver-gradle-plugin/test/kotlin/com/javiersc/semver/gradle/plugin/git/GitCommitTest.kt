package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semver.gradle.plugin.addNewFile
import com.javiersc.semver.gradle.plugin.git
import com.javiersc.semver.gradle.plugin.initialCommitAnd
import com.javiersc.semver.gradle.plugin.internal.GitRef
import com.javiersc.semver.gradle.plugin.internal.commitHash
import com.javiersc.semver.gradle.plugin.internal.commitsBetweenTwoCommitsIncludingLastExcludingFirst
import com.javiersc.semver.gradle.plugin.internal.commitsInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.commitsInCurrentBranchFullMessage
import com.javiersc.semver.gradle.plugin.internal.commitsInCurrentBranchHash
import com.javiersc.semver.gradle.plugin.internal.commitsInCurrentBranchRevCommit
import com.javiersc.semver.gradle.plugin.internal.headCommit
import com.javiersc.semver.gradle.plugin.internal.headRef
import com.javiersc.semver.gradle.plugin.internal.headRevCommit
import com.javiersc.semver.gradle.plugin.internal.headRevCommitInBranch
import com.javiersc.semver.gradle.plugin.internal.lastCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.lastVersionCommitInCurrentBranch
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import org.eclipse.jgit.revwalk.RevCommit

internal class GitCommitTest {

    @Test
    fun `head commit`() {
        initialCommitAnd {
            addNewFile("Second commit.txt")
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
            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.lastCommitInCurrentBranch?.message.shouldBe("Second commit")
        }
    }

    @Test
    fun `commits in current branch`() {
        initialCommitAnd {
            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            val messages: List<String> = listOf("Second commit", "Initial commit")

            git.commitsInCurrentBranchRevCommit.map(RevCommit::getShortMessage).shouldBe(messages)
            git.commitsInCurrentBranch.map(GitRef.Commit::message).shouldBe(messages)
            git.commitsInCurrentBranchHash.shouldBe(
                git.commitsInCurrentBranch.map(GitRef.Commit::hash)
            )
        }
    }

    @Test
    fun `commit hash`() {
        initialCommitAnd {
            addNewFile("Second commit.txt")
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

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            val messages2: List<String> = listOf("Second commit")

            git.commitsBetweenTwoCommitsIncludingLastExcludingFirst(
                    fromCommit = initialCommit(),
                    toCommit = lastCommit(),
                )
                .map(GitRef.Commit::message)
                .shouldBe(messages2)

            addNewFile("Third commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Third commit").call()

            val messages3: List<String> = listOf("Third commit") + messages2

            git.commitsBetweenTwoCommitsIncludingLastExcludingFirst(
                    fromCommit = initialCommit(),
                    toCommit = lastCommit(),
                )
                .map(GitRef.Commit::message)
                .shouldBe(messages3)
        }
    }

    @Test
    fun `commits in current branch full message`() {
        initialCommitAnd {
            addNewFile("Second commit.txt")
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
            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()
            git.tag().setName("v1.0.0").call()

            addNewFile("Third commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Third commit").call()

            git.lastVersionCommitInCurrentBranch(tagPrefix = "v")!!
                .message.shouldBe("Second commit")
        }
    }
}
