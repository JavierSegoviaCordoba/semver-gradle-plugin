package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.gradle.plugin.setup.git
import com.javiersc.semver.gradle.plugin.setup.initialCommitAnd
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import java.io.File
import kotlin.test.Test

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
            calculatedVersion().shouldBe("v0.1.0.0+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            calculatedVersion(isCreatingTag = true).shouldBe("v0.1.0")

            calculatedVersion(stage = "auto", scope = "auto", isCreatingTag = false)
                .shouldBe("v0.1.0")

            calculatedVersion(stage = "auto", scope = "auto", isCreatingTag = true)
                .shouldBe("v0.1.0")

            resolve("2 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()

            calculatedVersion().shouldBe("v1.0.0")

            calculatedVersion(isCreatingTag = true).shouldBe("v1.0.1")

            calculatedVersion(stage = "auto", scope = "auto").shouldBe("v1.0.1")

            resolve("3 commit.txt").createNewFile()
            calculatedVersion().shouldBe("v1.0.0.0+DIRTY")

            calculatedVersion(checkClean = false).shouldBe("v1.0.0")

            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()

            calculatedVersion().shouldBe("v1.0.0.1+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            calculatedVersion(scope = "auto", checkClean = false).shouldBe("v1.0.1")

            calculatedVersion(stage = "auto", scope = "auto").shouldBe("v1.0.1")

            resolve("4 commit.txt").createNewFile()
            calculatedVersion(checkClean = false)
                .shouldBe("v1.0.0.1+${git.lastCommitInCurrentBranch!!.hash.take(7)}")

            calculatedVersion(stage = "snapshot", checkClean = false).shouldBe("v1.0.1-SNAPSHOT")
            calculatedVersion(stage = "alpha", checkClean = false).shouldBe("v1.0.1-alpha.1")
            calculatedVersion(stage = "snapshot", scope = "major", checkClean = false)
                .shouldBe("v2.0.0-SNAPSHOT")
        }
    }
}

private fun File.calculatedVersion(
    tagPrefix: String = "v",
    stage: String? = null,
    scope: String? = null,
    isCreatingTag: Boolean = false,
    checkClean: Boolean = true,
) =
    tagPrefix +
        git.calculatedVersion(
            tagPrefix = tagPrefix,
            stageProperty = stage,
            scopeProperty = scope,
            isCreatingSemverTag = isCreatingTag,
            checkClean = checkClean,
        )
