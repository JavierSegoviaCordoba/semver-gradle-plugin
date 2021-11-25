package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semanticVersioning.Version
import com.javiersc.semver.gradle.plugin.addNewFile
import com.javiersc.semver.gradle.plugin.git
import com.javiersc.semver.gradle.plugin.initialCommitAnd
import com.javiersc.semver.gradle.plugin.internal.SemVerException
import com.javiersc.semver.gradle.plugin.internal.lastVersionInCurrentBranch
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class VersionTest {

    @Test
    fun `last version in current branch`() {
        initialCommitAnd {
            addNewFile("2 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()
            git.tag().setName("v1.0.0").call()
            git.lastVersionInCurrentBranch(::shouldNotHappen, "v").shouldBe(Version("1.0.0"))

            git.tag().setName("v1.0.0-alpha.1").call()
            git.lastVersionInCurrentBranch(::shouldNotHappen, "v").shouldBe(Version("1.0.0"))

            addNewFile("3 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            git.lastVersionInCurrentBranch(::shouldNotHappen, "v")
                .shouldBe(Version("1.0.0-alpha.2"))

            addNewFile("4 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("4 commit").call()
            shouldThrow<SemVerException> {
                    git.lastVersionInCurrentBranch(
                        { last, higher -> throw SemVerException("$higher > $last") },
                        "v"
                    )
                }
                .message
                .shouldBe("1.0.0 > 1.0.0-alpha.2")

            addNewFile("5 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("5 commit").call()
            git.tag().setName("v2.0.0-rc.2").call()
            git.lastVersionInCurrentBranch(::shouldNotHappen, "v").shouldBe(Version("2.0.0-rc.2"))
        }
    }
}

internal fun shouldNotHappen(last: Version?, higher: Version?): Unit =
    error("this should not happen, $last, $higher")
