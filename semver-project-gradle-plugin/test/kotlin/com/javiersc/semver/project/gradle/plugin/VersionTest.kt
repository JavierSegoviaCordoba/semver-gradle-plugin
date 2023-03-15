package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class VersionTest {

    @Test
    fun `last version in current branch`() {
        initialCommitAnd {
            fun cache() = GitCache(gitDir = git.repository.directory)

            resolve("2 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()
            cache().lastVersionInCurrentBranch("v").shouldBe(GradleVersion("1.0.0"))

            git.tag().setName("v1.0.0-alpha.1").call()
            cache().lastVersionInCurrentBranch("v").shouldBe(GradleVersion("1.0.0"))

            resolve("3 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            cache().lastVersionInCurrentBranch("v").shouldBe(GradleVersion("1.0.0-alpha.2"))

            resolve("4 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("4 commit").call()

            var isWarningLastVersionIsNotHigherVersion = false
            cache().lastVersionInCurrentBranch("v") { isWarningLastVersionIsNotHigherVersion = it }
            isWarningLastVersionIsNotHigherVersion.shouldBeTrue()

            resolve("5 commit.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("5 commit").call()
            git.tag().setName("v2.0.0-rc.2").call()
            cache().lastVersionInCurrentBranch("v").shouldBe(GradleVersion("2.0.0-rc.2"))
        }
    }
}
