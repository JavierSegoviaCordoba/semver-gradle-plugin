package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.gradle.plugin.setup.git
import com.javiersc.semver.gradle.plugin.setup.initialCommitAnd
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class VersionTest {

    @Test
    fun `last version in current branch`() {
        initialCommitAnd {
            resolve("2 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()
            GitCache(git).lastVersionInCurrentBranch("v").shouldBe(Version("1.0.0"))

            git.tag().setName("v1.0.0-alpha.1").call()
            GitCache(git).lastVersionInCurrentBranch("v").shouldBe(Version("1.0.0"))

            resolve("3 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            GitCache(git).lastVersionInCurrentBranch("v").shouldBe(Version("1.0.0-alpha.2"))

            resolve("4 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("4 commit").call()

            var isWarningLastVersionIsNotHigherVersion = false
            GitCache(git).lastVersionInCurrentBranch("v") {
                isWarningLastVersionIsNotHigherVersion = it
            }
            isWarningLastVersionIsNotHigherVersion.shouldBeTrue()

            resolve("5 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("5 commit").call()
            git.tag().setName("v2.0.0-rc.2").call()
            GitCache(git).lastVersionInCurrentBranch("v").shouldBe(Version("2.0.0-rc.2"))
        }
    }
}
