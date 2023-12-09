package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.internal.AdditionalVersionData
import com.javiersc.semver.project.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.test.Test

internal class CalculatedVersionTest {

    @Test
    fun `given a repository without tags, when different tags are created, then additional data is calculated`() {
        initialCommitAnd {
            calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(AdditionalVersionData(commits = 0, hash = hash7, metadata = null))
                .shouldNotBeNull()
                .asString()
                .shouldBe(".0+$hash7")

            createNewFile("2 commit.txt")

            calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(AdditionalVersionData(commits = 0, hash = hash7, metadata = "DIRTY"))
                .shouldNotBeNull()
                .asString()
                .shouldBe(".0+$hash7+DIRTY")

            addAllCall()
            commitCall("2 commit")
            tagCall("v1.0.0")

            calculateAdditionalVersionData("v").shouldBeNull()

            createNewFile("3 commit.txt")

            calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(AdditionalVersionData(commits = 0, hash = null, metadata = "DIRTY"))
                .shouldNotBeNull()
                .asString()
                .shouldBe(".0+DIRTY")

            addAllCall()

            calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(AdditionalVersionData(commits = 0, hash = null, metadata = "DIRTY"))
                .shouldNotBeNull()
                .asString()
                .shouldBe(".0+DIRTY")

            commitCall("3 commit")

            calculateAdditionalVersionData(tagPrefix = "v")
                .shouldBe(AdditionalVersionData(commits = 1, hash = hash7, metadata = null))
                .shouldNotBeNull()
                .asString()
                .shouldBe(".1+$hash7")
        }
    }

    @Test
    fun `given a repository without tags, when scope and stage are auto, then version is calculated`() {
        initialCommitAnd {
            calculatedVersion().shouldBe("v0.1.0.0+$hash7")

            calculatedVersion(isCreatingTag = true).shouldBe("v0.1.0")

            calculatedVersion(scope = "auto", stage = "auto", isCreatingTag = false)
                .shouldBe("v0.1.0")
        }
    }

    @Test
    fun `given a repository without tags, when scope is auto and stage is final, then version is calculated`() {
        initialCommitAnd {
            calculatedVersion().shouldBe("v0.1.0.0+$hash7")
            calculatedVersion(isCreatingTag = true).shouldBe("v0.1.0")
            calculatedVersion(scope = "auto", stage = "final", isCreatingTag = false)
                .shouldBe("v0.1.0")
        }
    }

    @Test
    fun `given a repository without tags, when different tags, scopes and stages are provided, then version is calculated`() {
        initialCommitAnd {
            calculatedVersion().shouldBe("v0.1.0.0+$hash7")

            calculatedVersion(isCreatingTag = true).shouldBe("v0.1.0")

            calculatedVersion(scope = "auto", stage = "auto").shouldBe("v0.1.0")

            calculatedVersion(scope = "auto", stage = "auto", isCreatingTag = true)
                .shouldBe("v0.1.0")

            calculatedVersion(scope = "auto", stage = "final").shouldBe("v0.1.0")

            calculatedVersion(scope = "auto", stage = "final", isCreatingTag = true)
                .shouldBe("v0.1.0")

            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v1.0.0")

            calculatedVersion().shouldBe("v1.0.0")

            calculatedVersion(scope = "auto", stage = "auto").shouldBe("v1.0.1")

            calculatedVersion(scope = "auto", stage = "auto", isCreatingTag = true)
                .shouldBe("v1.0.1")

            createNewFile("3 commit.txt")
            calculatedVersion().shouldBe("v1.0.0.0+DIRTY")

            calculatedVersion(checkClean = false).shouldBe("v1.0.0")

            addAllCall()
            commitCall("3 commit")

            calculatedVersion().shouldBe("v1.0.0.1+$hash7")

            createNewFile("4 commit.txt")

            calculatedVersion(scope = "auto", checkClean = false).shouldBe("v1.0.1")

            calculatedVersion(scope = "auto", stage = "auto", checkClean = false).shouldBe("v1.0.1")

            calculatedVersion(checkClean = false).shouldBe("v1.0.0.1+$hash7")

            calculatedVersion(scope = "auto", stage = "snapshot", checkClean = false)

            calculatedVersion(scope = "patch", stage = "snapshot", checkClean = false)
                .shouldBe("v1.0.1-SNAPSHOT")

            calculatedVersion(scope = "auto", stage = "alpha", checkClean = false)

            calculatedVersion(scope = "patch", stage = "alpha", checkClean = false)
                .shouldBe("v1.0.1-alpha.1")

            calculatedVersion(scope = "major", stage = "snapshot", checkClean = false)
                .shouldBe("v2.0.0-SNAPSHOT")

            calculatedVersion(scope = "auto", stage = "beta", checkClean = false)

            shouldThrowVersionException {
                calculatedVersion(scope = "auto", stage = "snapshot", isCreatingTag = true)
            }

            calculatedVersion(
                    scope = "auto",
                    stage = "snapshot",
                    isCreatingTag = true,
                    checkClean = false
                )
                .shouldBe("v1.0.1-SNAPSHOT")

            addAllCall()
            commitCall("4 commit")
            tagCall("v4.5.3")
            calculatedVersion().shouldBe("v4.5.3")
        }
    }

    @Test
    fun `given scope=null stage=final force=true, when current version is v0_9_0, the version is calculated`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v0.9.0")

            shouldThrowVersionException {
                calculatedVersion(tagPrefix = "v", scope = null, stage = "final")
            }
            calculatedVersion(tagPrefix = "v", scope = null, stage = "final", force = true)
                .shouldBe("v0.9.1")
        }
    }

    @Test
    fun `given scope=auto stage=final, when current version is v0_9_0, the version is calculated`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v0.9.0")

            calculatedVersion(tagPrefix = "v", scope = "auto", stage = "final").shouldBe("v0.9.1")
        }
    }

    @Test
    fun `given scope=auto stage=auto, when current version is v0_9_0, the version is calculated`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v0.9.0")

            calculatedVersion(tagPrefix = "v", scope = "auto", stage = "auto").shouldBe("v0.9.1")
        }
    }

    @Test
    fun `given scope=patch stage=final, when current version is v0_9_0, the version is calculated`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v0.9.0")

            calculatedVersion(tagPrefix = "v", scope = "patch", stage = "final").shouldBe("v0.9.1")
        }
    }

    @Test
    fun `given scope=patch stage=auto, when current version is v0_9_0, the version is calculated`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v0.9.0")

            calculatedVersion(tagPrefix = "v", scope = "patch", stage = "auto").shouldBe("v0.9.1")
        }
    }

    @Test
    fun `given scope=null stage=null, when current version is v0_9_0, the version is calculated`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v0.9.0")

            calculatedVersion(tagPrefix = "v", scope = null, stage = null).shouldBe("v0.9.0")
        }
    }

    @Test
    fun `given scope=auto stage=beta, when current version is v1_0_0-alpha_1, then it is v1_0_0-beta_1`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v1.0.0-alpha.1")

            calculatedVersion(tagPrefix = "v", scope = "auto", stage = "beta")
                .shouldBe("v1.0.0-beta.1")
        }
    }

    @Test
    fun `given scope=auto stage=alpha, when current version is v1_0_0-beta_1, then it fails`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v1.0.0-beta.1")

            shouldThrowVersionException {
                calculatedVersion(tagPrefix = "v", scope = "auto", stage = "alpha")
            }
        }
    }

    @Test
    fun `given scope=auto stage=alpha force=true, when current version is v1_0_0-beta_1, then it v1_0_1-alpha_1`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v1.0.0-beta.1")

            calculatedVersion(tagPrefix = "v", scope = "auto", stage = "alpha", force = true)
                .shouldBe("v1.0.1-alpha.1")
        }
    }

    @Test
    fun `given scope=auto stage=alpha, when current version is v1_0_0-rc_11, then it fails`() {
        initialCommitAnd {
            tagCall("v1.0.0-rc.11")

            shouldThrowVersionException {
                calculatedVersion(tagPrefix = "v", scope = "auto", stage = "alpha")
            }

            shouldThrowVersionException {
                calculatedVersion(
                    tagPrefix = "v",
                    scope = "auto",
                    stage = "alpha",
                    isCreatingTag = true,
                )
            }
        }
    }
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
internal fun File.calculateAdditionalVersionData(
    tagPrefix: String,
    checkIsClean: Boolean = true
): AdditionalVersionData? =
    git.calculateAdditionalVersionData(
        tagPrefix = tagPrefix,
        checkIsClean = checkIsClean,
    )

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
internal fun File.lastVersionInCurrentBranch(
    tagPrefix: String,
    isWarningLastVersionIsNotHigherVersion: (Boolean) -> Unit = {},
): GradleVersion =
    gitCache.lastVersionInCurrentBranch(
        tagPrefix = tagPrefix,
        isWarningLastVersionIsNotHigherVersion = isWarningLastVersionIsNotHigherVersion,
    )

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
internal fun File.calculatedVersion(
    tagPrefix: String = "v",
    scope: String? = null,
    stage: String? = null,
    isCreatingTag: Boolean = false,
    checkClean: Boolean = true,
    force: Boolean = false,
): String =
    gitCache.calculatedVersion(
        tagPrefix = tagPrefix,
        scope = scope,
        stage = stage,
        isCreatingTag = isCreatingTag,
        checkClean = checkClean,
        force = force,
    )

internal fun GitCache.calculatedVersion(
    tagPrefix: String = "v",
    scope: String? = null,
    stage: String? = null,
    isCreatingTag: Boolean = false,
    checkClean: Boolean = true,
    force: Boolean = false,
): String {
    val lastSemver: GradleVersion = lastVersionInCurrentBranch(tagPrefix = tagPrefix)
    val calculatedVersion: String =
        calculatedVersion(
            lastSemver = lastSemver,
            stageProperty = stage,
            scopeProperty = scope,
            isCreatingSemverTag = isCreatingTag,
            versionTagsInBranch = versionTagsInCurrentBranch(tagPrefix).map(GitRef.Tag::name),
            clean = isClean,
            force = force,
            checkClean = checkClean,
            lastCommitInCurrentBranch = lastCommitInCurrentBranch?.hash,
            commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
            headCommit = headCommit.commit.hash,
            lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch(tagPrefix)?.hash,
        )
    return "$tagPrefix$calculatedVersion"
}
