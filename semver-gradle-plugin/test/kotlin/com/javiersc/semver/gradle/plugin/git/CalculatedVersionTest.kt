package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.gradle.plugin.internal.git.commitsInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.gradle.plugin.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.lastVersionCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.git.versionTagsInCurrentBranch
import com.javiersc.semver.gradle.plugin.setup.git
import com.javiersc.semver.gradle.plugin.setup.initialCommitAnd
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlin.test.Test
import org.eclipse.jgit.api.Git

internal class CalculatedVersionTest {

    @Test
    fun `calculate additional version data`() {
        initialCommitAnd {
            git.calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(".0+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            resolve("2 commit.txt").createNewFile()

            git.calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(".0+${git.lastCommitInCurrentBranch!!.hash.take(7)}+DIRTY")

            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()

            git.calculateAdditionalVersionData("v").shouldBeEmpty()

            resolve("3 commit.txt").createNewFile()

            git.calculateAdditionalVersionData(tagPrefix = "v").shouldBe(".0+DIRTY")

            git.add().addFilepattern(".").call()

            git.calculateAdditionalVersionData(tagPrefix = "v").shouldBe(".0+DIRTY")

            git.commit().setMessage("3 commit").call()

            git.calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(".1+${git.lastCommitInCurrentBranch!!.hash.take(7)}")
        }
    }

    @Test
    fun `calculated version`() {
        initialCommitAnd {
            GitCache(git)
                .calculatedVersion()
                .shouldBe("v0.1.0.0+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            GitCache(git).calculatedVersion(isCreatingTag = true).shouldBe("v0.1.0")

            GitCache(git)
                .calculatedVersion(stage = "auto", scope = "auto", isCreatingTag = false)
                .shouldBe("v0.1.0")

            GitCache(git)
                .calculatedVersion(stage = "auto", scope = "auto", isCreatingTag = true)
                .shouldBe("v0.1.0")

            resolve("2 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()

            GitCache(git).calculatedVersion().shouldBe("v1.0.0")

            GitCache(git).calculatedVersion(isCreatingTag = true).shouldBe("v1.0.1")

            GitCache(git).calculatedVersion(stage = "auto", scope = "auto").shouldBe("v1.0.1")

            resolve("3 commit.txt").createNewFile()
            GitCache(git).calculatedVersion().shouldBe("v1.0.0.0+DIRTY")

            GitCache(git).calculatedVersion(checkClean = false).shouldBe("v1.0.0")

            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()

            GitCache(git)
                .calculatedVersion()
                .shouldBe("v1.0.0.1+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            GitCache(git).calculatedVersion(scope = "auto", checkClean = false).shouldBe("v1.0.1")

            GitCache(git).calculatedVersion(stage = "auto", scope = "auto").shouldBe("v1.0.1")

            resolve("4 commit.txt").createNewFile()
            GitCache(git)
                .calculatedVersion(checkClean = false)
                .shouldBe("v1.0.0.1+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            GitCache(git)
                .calculatedVersion(stage = "snapshot", checkClean = false)
                .shouldBe("v1.0.1-SNAPSHOT")
            GitCache(git)
                .calculatedVersion(stage = "alpha", checkClean = false)
                .shouldBe("v1.0.1-alpha.1")
            GitCache(git)
                .calculatedVersion(stage = "snapshot", scope = "major", checkClean = false)
                .shouldBe("v2.0.0-SNAPSHOT")
        }
    }
}

internal fun GitCache.calculatedVersion(
    tagPrefix: String = "v",
    stage: String? = null,
    scope: String? = null,
    isCreatingTag: Boolean = false,
    checkClean: Boolean = true,
): String {
    val lastSemver = lastVersionInCurrentBranch(tagPrefix = tagPrefix)
    return tagPrefix +
        calculatedVersion(
            stageProperty = stage,
            scopeProperty = scope,
            isCreatingSemverTag = isCreatingTag,
            lastSemverMajorInCurrentBranch = lastSemver.major,
            lastSemverMinorInCurrentBranch = lastSemver.minor,
            lastSemverPatchInCurrentBranch = lastSemver.patch,
            lastSemverStageInCurrentBranch = lastSemver.stage?.name,
            lastSemverNumInCurrentBranch = lastSemver.stage?.num,
            versionTagsInCurrentBranch =
                versionTagsInCurrentBranch(tagPrefix).map(GitRef.Tag::name),
            clean = isClean,
            checkClean = checkClean,
            lastCommitInCurrentBranch = lastCommitInCurrentBranch?.hash,
            commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
            headCommit = headCommit.commit.hash,
            lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch(tagPrefix)?.hash,
        )
}

internal fun Git.calculateAdditionalVersionData(
    tagPrefix: String,
    checkIsClean: Boolean = true,
): String =
    calculateAdditionalVersionData(
        clean = status().call().isClean,
        checkClean = checkIsClean,
        lastCommitInCurrentBranch = lastCommitInCurrentBranch?.hash,
        commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
        isThereVersionTags = versionTagsInCurrentBranch(tagPrefix).isNotEmpty(),
        headCommit = headCommit.commit.hash,
        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch(tagPrefix)?.hash,
    )
