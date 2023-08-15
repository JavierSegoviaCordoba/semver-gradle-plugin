package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class VersionTest {

    @Test
    fun `last version in current branch`() {
        initialCommitAnd {
            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")
            tagCall("v1.0.0")
            lastVersionInCurrentBranch("v").shouldBe(GradleVersion("1.0.0"))

            tagCall("v1.0.0-alpha.1")
            lastVersionInCurrentBranch("v").shouldBe(GradleVersion("1.0.0"))

            createNewFile("3 commit.txt")
            addAllCall()
            commitCall("3 commit")
            tagCall("v1.0.0-alpha.2")
            lastVersionInCurrentBranch("v").shouldBe(GradleVersion("1.0.0-alpha.2"))

            createNewFile("4 commit.txt")
            addAllCall()
            commitCall("4 commit")

            var isWarningLastVersionIsNotHigherVersion = false
            lastVersionInCurrentBranch("v") { isWarningLastVersionIsNotHigherVersion = it }
            isWarningLastVersionIsNotHigherVersion.shouldBeTrue()

            createNewFile("5 commit.txt")
            addAllCall()
            commitCall("5 commit")
            tagCall("v2.0.0-rc.2")
            lastVersionInCurrentBranch("v").shouldBe(GradleVersion("2.0.0-rc.2"))
        }
    }
}
