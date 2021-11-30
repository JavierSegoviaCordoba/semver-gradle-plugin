package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.addNewFile
import com.javiersc.semver.gradle.plugin.git
import com.javiersc.semver.gradle.plugin.initialCommitAnd
import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.lastCommitInCurrentBranch
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import java.io.File
import java.time.Instant
import java.util.Date
import kotlin.test.Test

internal class CalculatedVersionTest {

    @Test
    fun `calculate additional version data`() {
        initialCommitAnd {
            git.calculateAdditionalVersionData(
                    tagPrefix = "v",
                    mockDate = Date.from(Instant.ofEpochMilli(1)),
                )
                .shouldBe(".0+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            addNewFile("2 commit.txt")

            git.calculateAdditionalVersionData(
                    tagPrefix = "v",
                    mockDate = Date.from(Instant.ofEpochMilli(0)),
                )
                .shouldBe(".0+1970-01-01T00:00:00Z")

            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()

            git.calculateAdditionalVersionData("v", null).shouldBeEmpty()

            addNewFile("3 commit.txt")

            git.calculateAdditionalVersionData(
                    tagPrefix = "v",
                    mockDate = Date.from(Instant.ofEpochMilli(1)),
                )
                .shouldBe(".0+1970-01-01T00:00:01Z")

            git.add().addFilepattern(".").call()

            git.calculateAdditionalVersionData(
                    tagPrefix = "v",
                    mockDate = Date.from(Instant.ofEpochMilli(2)),
                )
                .shouldBe(".0+1970-01-01T00:00:02Z")

            git.commit().setMessage("3 commit").call()

            git.calculateAdditionalVersionData(
                    tagPrefix = "v",
                    mockDate = Date.from(Instant.ofEpochMilli(1)),
                )
                .shouldBe(".1+${git.lastCommitInCurrentBranch!!.hash.take(7)}")
        }
    }

    @Test
    fun `calculated version`() {
        initialCommitAnd {
            calculatedVersion().shouldBe("v0.1.0.0+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            calculatedVersion(isCreatingTag = true).shouldBe("v0.1.0")

            calculatedVersion(stage = "auto", scope = "auto", isCreatingTag = false)
                .shouldBe("v0.1.0")

            calculatedVersion(stage = "auto", scope = "auto", isCreatingTag = true)
                .shouldBe("v0.1.0")

            addNewFile("2 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()

            calculatedVersion().shouldBe("v1.0.0")

            calculatedVersion(isCreatingTag = true).shouldBe("v1.0.1")

            calculatedVersion(stage = "auto", scope = "auto").shouldBe("v1.0.1")

            addNewFile("3 commit.txt")
            calculatedVersion(mockDate = Date.from(Instant.ofEpochMilli(0)))
                .shouldBe("v1.0.0.0+1970-01-01T00:00:00Z")

            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()

            calculatedVersion().shouldBe("v1.0.0.1+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            calculatedVersion(stage = "auto", scope = "auto").shouldBe("v1.0.1")
        }
    }
}

private fun File.calculatedVersion(
    warningLastVersionIsNotHigherVersion: (last: Version?, higher: Version?) -> Unit = { _, _ -> },
    tagPrefix: String = "v",
    stage: String? = null,
    scope: String? = null,
    isCreatingTag: Boolean = false,
    mockDate: Date? = null,
) =
    tagPrefix +
        git.calculatedVersion(
            warningLastVersionIsNotHigherVersion = warningLastVersionIsNotHigherVersion,
            tagPrefix = tagPrefix,
            stageProperty = stage,
            scopeProperty = scope,
            isCreatingSemverTag = isCreatingTag,
            mockDate = mockDate,
        )
