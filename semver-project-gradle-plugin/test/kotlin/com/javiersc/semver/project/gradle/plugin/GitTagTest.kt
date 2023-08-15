package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.project.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.isThereVersionTag
import com.javiersc.semver.project.gradle.plugin.internal.git.lastVersionTagInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.tagName
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInCurrentBranchHash
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInCurrentBranchName
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInCurrentBranchRef
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInCurrentCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInRepo
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInRepoHash
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInRepoName
import com.javiersc.semver.project.gradle.plugin.internal.git.tagsInRepoRef
import com.javiersc.semver.project.gradle.plugin.internal.git.versionTagsInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder
import com.javiersc.semver.project.gradle.plugin.internal.git.versionTagsInCurrentCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.versionTagsSortedBySemver
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import org.eclipse.jgit.lib.Ref

internal class GitTagTest {

    @Test
    fun `tags in repo`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")
            git.tagsInRepo.map(GitRef.Tag::name).shouldBe(listOf("hello", "v100", "vhello"))

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInRepo.map(GitRef.Tag::name) shouldBe
                listOf("hello", "v100", "v3.0.0", "vhello")

            tagCall("v1.0.0")
            git.tagsInRepo.map(GitRef.Tag::name) shouldBe
                listOf("hello", "v1.0.0", "v100", "v3.0.0", "vhello")

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")

            git.tagsInRepo.map(GitRef.Tag::name) shouldBe
                listOf("hello", "v1.0.0", "v100", "v2.0.0", "v20.20", "v200", "v3.0.0", "vhello")
        }
    }

    @Test
    fun `tags in repo ref`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")
            git.tagsInRepoRef.map(Ref::tagName).shouldBe(listOf("hello", "v100", "vhello"))

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInRepoRef
                .map(Ref::tagName)
                .shouldBe(listOf("hello", "v100", "v3.0.0", "vhello"))

            tagCall("v1.0.0")
            git.tagsInRepoRef.map(Ref::tagName) shouldBe
                listOf("hello", "v1.0.0", "v100", "v3.0.0", "vhello")

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInRepoRef.map(Ref::tagName) shouldBe
                listOf("hello", "v1.0.0", "v100", "v2.0.0", "v20.20", "v200", "v3.0.0", "vhello")
        }
    }

    @Test
    fun `tags in repo hash`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })

            tagCall("v1.0.0")
            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })
        }
    }

    @Test
    fun `tags in repo name`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")
            git.tagsInRepoName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/vhello",
                )
            )

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInRepoName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            tagCall("v1.0.0")
            git.tagsInRepoName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v1.0.0",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInRepoName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v1.0.0",
                    "refs/tags/v100",
                    "refs/tags/v2.0.0",
                    "refs/tags/v20.20",
                    "refs/tags/v200",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )
        }
    }

    @Test
    fun `tags in current branch`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")
            git.tagsInCurrentBranch
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "vhello",
                    )
                )

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInCurrentBranch
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "v3.0.0",
                        "vhello",
                    )
                )

            tagCall("v1.0.0")
            git.tagsInCurrentBranch
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "hello",
                        "v1.0.0",
                        "v100",
                        "v3.0.0",
                        "vhello",
                    )
                )

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInCurrentBranch
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "hello",
                        "v1.0.0",
                        "v100",
                        "v2.0.0",
                        "v20.20",
                        "v200",
                        "v3.0.0",
                        "vhello",
                    )
                )
        }
    }

    @Test
    fun `tags in current branch ref`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")
            git.tagsInCurrentBranchRef
                .map(Ref::tagName)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "vhello",
                    )
                )

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInCurrentBranchRef
                .map(Ref::tagName)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "v3.0.0",
                        "vhello",
                    )
                )

            tagCall("v1.0.0")
            git.tagsInCurrentBranchRef
                .map(Ref::tagName)
                .shouldBe(
                    listOf(
                        "hello",
                        "v1.0.0",
                        "v100",
                        "v3.0.0",
                        "vhello",
                    )
                )

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInCurrentBranchRef
                .map(Ref::tagName)
                .shouldBe(
                    listOf(
                        "hello",
                        "v1.0.0",
                        "v100",
                        "v2.0.0",
                        "v20.20",
                        "v200",
                        "v3.0.0",
                        "vhello",
                    )
                )
        }
    }

    @Test
    fun `tags in current branch hash`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }

            tagCall("v1.0.0")
            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }
        }
    }

    @Test
    fun `tags in current branch name`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")
            git.tagsInCurrentBranchName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/vhello",
                )
            )

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.tagsInCurrentBranchName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            tagCall("v1.0.0")
            git.tagsInCurrentBranchName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v1.0.0",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInCurrentBranchName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v1.0.0",
                    "refs/tags/v100",
                    "refs/tags/v2.0.0",
                    "refs/tags/v20.20",
                    "refs/tags/v200",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )
        }
    }

    @Test
    fun `tags in current commit`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")
            git.tagsInCurrentCommit(git.headCommit.commit.hash)
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "vhello",
                    )
                )

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v1.0.0")
            git.tagsInCurrentCommit(git.headCommit.commit.hash)
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0"))

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.tagsInCurrentCommit(git.headCommit.commit.hash)
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0",
                        "v2.0.0",
                        "v20.20",
                        "v200",
                    )
                )
        }
    }

    @Test
    fun `is there version tag`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v2.0.0"))
            git.isThereVersionTag(tagPrefix = "v").shouldBeTrue()
        }
    }

    @Test
    fun `version tags in current commit`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v3.0.0"))

            tagCall("v1.0.0")
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            tagCall("v2.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))
        }
    }

    @Test
    fun `version tags in current branch`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.versionTagsInCurrentBranch("v").map(GitRef.Tag::name).shouldBe(emptyList())

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.versionTagsInCurrentBranch("v").map(GitRef.Tag::name).shouldBe(listOf("v3.0.0"))

            tagCall("v1.0.0")
            git.versionTagsInCurrentBranch("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            tagCall("v2.0.0")
            git.versionTagsInCurrentBranch("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))

            tagCall("v1.0.0-alpha.22")
            tagCall("v1.0.0-alpha.11")
            tagCall("v1.0.0-alpha.10")
            tagCall("v1.0.0-alpha.2")
            tagCall("v1.0.0-alpha.1")
            git.versionTagsInCurrentBranch("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0",
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.22",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )

            tagCall("v1.0.0-rc.22")
            tagCall("v1.0.0-rc.11")
            tagCall("v1.0.0-rc.10")
            tagCall("v1.0.0-rc.2")
            tagCall("v1.0.0-rc.1")
            git.versionTagsInCurrentBranch("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0",
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.22",
                        "v1.0.0-rc.1",
                        "v1.0.0-rc.10",
                        "v1.0.0-rc.11",
                        "v1.0.0-rc.2",
                        "v1.0.0-rc.22",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )

            tagCall("v200")
            tagCall("v20.20")
            git.versionTagsInCurrentBranch("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0",
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.22",
                        "v1.0.0-rc.1",
                        "v1.0.0-rc.10",
                        "v1.0.0-rc.11",
                        "v1.0.0-rc.2",
                        "v1.0.0-rc.22",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )
        }
    }

    @Test
    fun `version tags sorted by semver`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.versionTagsSortedBySemver("v").map(GitRef.Tag::name).shouldBe(emptyList())

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.versionTagsSortedBySemver("v").map(GitRef.Tag::name).shouldBe(listOf("v3.0.0"))

            tagCall("v1.0.0")
            git.versionTagsSortedBySemver("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            tagCall("v2.0.0")
            git.versionTagsSortedBySemver("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))

            tagCall("v1.0.0-alpha.22")
            tagCall("v1.0.0-alpha.11")
            tagCall("v1.0.0-alpha.10")
            tagCall("v1.0.0-alpha.2")
            tagCall("v1.0.0-alpha.1")

            git.versionTagsSortedBySemver("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.22",
                        "v1.0.0",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )

            tagCall("v1.0.0-rc.22")
            tagCall("v1.0.0-rc.11")
            tagCall("v1.0.0-rc.10")
            tagCall("v1.0.0-rc.2")
            tagCall("v1.0.0-rc.1")

            git.versionTagsSortedBySemver("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.22",
                        "v1.0.0-rc.1",
                        "v1.0.0-rc.2",
                        "v1.0.0-rc.10",
                        "v1.0.0-rc.11",
                        "v1.0.0-rc.22",
                        "v1.0.0",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )

            tagCall("v200")
            tagCall("v20.20")

            git.versionTagsSortedBySemver("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.22",
                        "v1.0.0-rc.1",
                        "v1.0.0-rc.2",
                        "v1.0.0-rc.10",
                        "v1.0.0-rc.11",
                        "v1.0.0-rc.22",
                        "v1.0.0",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )
        }
    }

    @Test
    fun `version tags sorted by timeline or semver 1`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            createNewFile("Second commit.txt")
            addAllCall()
            commitCall("Second commit")

            tagCall("v3.0.0")
            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v3.0.0"))

            tagCall("v1.0.0")
            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            tagCall("v2.0.0")
            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))

            tagCall("v1.0.0-alpha.22")
            tagCall("v1.0.0-alpha.11")
            tagCall("v1.0.0-alpha.10")
            tagCall("v1.0.0-alpha.2")
            tagCall("v1.0.0-alpha.1")

            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.22",
                        "v1.0.0",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )

            tagCall("v1.0.0-rc.22")
            tagCall("v1.0.0-rc.11")
            tagCall("v1.0.0-rc.10")
            tagCall("v1.0.0-rc.2")
            tagCall("v1.0.0-rc.1")

            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.22",
                        "v1.0.0-rc.1",
                        "v1.0.0-rc.2",
                        "v1.0.0-rc.10",
                        "v1.0.0-rc.11",
                        "v1.0.0-rc.22",
                        "v1.0.0",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )

            tagCall("v200")
            tagCall("v20.20")

            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0-alpha.1",
                        "v1.0.0-alpha.2",
                        "v1.0.0-alpha.10",
                        "v1.0.0-alpha.11",
                        "v1.0.0-alpha.22",
                        "v1.0.0-rc.1",
                        "v1.0.0-rc.2",
                        "v1.0.0-rc.10",
                        "v1.0.0-rc.11",
                        "v1.0.0-rc.22",
                        "v1.0.0",
                        "v2.0.0",
                        "v3.0.0",
                    )
                )
        }
    }

    @Test
    fun `version tags sorted by timeline or semver 2`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")

            fun assertTags(vararg versionTag: String) {
                git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                    .map(GitRef.Tag::name)
                    .shouldBe(versionTag.toList())
            }

            createNewFile("3 commit.txt")
            addAllCall()
            commitCall("3 commit")
            tagCall("v4.0.0")
            assertTags("v4.0.0")

            createNewFile("4 commit.txt")
            addAllCall()
            commitCall("4 commit")
            tagCall("v1.0.0")
            assertTags("v4.0.0", "v1.0.0")

            createNewFile("5 commit.txt")
            addAllCall()
            commitCall("5 commit")
            tagCall("v2.0.0")
            assertTags("v4.0.0", "v1.0.0", "v2.0.0")

            createNewFile("6 commit.txt")
            addAllCall()
            commitCall("6 commit")
            tagCall("v1.0.0-alpha.22")
            tagCall("v1.0.0-alpha.11")
            tagCall("v1.0.0-alpha.10")
            assertTags(
                "v4.0.0",
                "v1.0.0",
                "v2.0.0",
                "v1.0.0-alpha.10",
                "v1.0.0-alpha.11",
                "v1.0.0-alpha.22",
            )

            createNewFile("7 commit.txt")
            addAllCall()
            commitCall("7 commit")
            tagCall("v1.0.0-alpha.2")
            tagCall("v1.0.0-alpha.1")
            tagCall("v1.0.0-rc.22")
            tagCall("v1.0.0-rc.11")
            assertTags(
                "v4.0.0",
                "v1.0.0",
                "v2.0.0",
                "v1.0.0-alpha.10",
                "v1.0.0-alpha.11",
                "v1.0.0-alpha.22",
                "v1.0.0-alpha.1",
                "v1.0.0-alpha.2",
                "v1.0.0-rc.11",
                "v1.0.0-rc.22",
            )

            createNewFile("8 commit.txt")
            addAllCall()
            commitCall("8 commit")
            tagCall("v1.0.0-rc.10")
            tagCall("v1.0.0-rc.2")
            tagCall("v1.0.0-rc.1")
            assertTags(
                "v4.0.0",
                "v1.0.0",
                "v2.0.0",
                "v1.0.0-alpha.10",
                "v1.0.0-alpha.11",
                "v1.0.0-alpha.22",
                "v1.0.0-alpha.1",
                "v1.0.0-alpha.2",
                "v1.0.0-rc.11",
                "v1.0.0-rc.22",
                "v1.0.0-rc.1",
                "v1.0.0-rc.2",
                "v1.0.0-rc.10",
            )

            createNewFile("9 commit.txt")
            addAllCall()
            commitCall("9 commit")
            tagCall("v3.0.0")
            assertTags(
                "v4.0.0",
                "v1.0.0",
                "v2.0.0",
                "v1.0.0-alpha.10",
                "v1.0.0-alpha.11",
                "v1.0.0-alpha.22",
                "v1.0.0-alpha.1",
                "v1.0.0-alpha.2",
                "v1.0.0-rc.11",
                "v1.0.0-rc.22",
                "v1.0.0-rc.1",
                "v1.0.0-rc.2",
                "v1.0.0-rc.10",
                "v3.0.0",
            )

            createNewFile("10 commit.txt")
            addAllCall()
            commitCall("8 commit")
            tagCall("v2.0.0-rc.22")
            tagCall("v2.0.0-rc.11")
            tagCall("v2.0.0-rc.20")
            tagCall("v2.0.0-rc.10")
            tagCall("v2.0.0-rc.3")
            tagCall("v2.0.0-rc.4")
            tagCall("v2.0.0-rc.21")
            tagCall("v2.0.0-rc.2")
            tagCall("v2.0.0-rc.1")

            assertTags(
                "v4.0.0",
                "v1.0.0",
                "v2.0.0",
                "v1.0.0-alpha.10",
                "v1.0.0-alpha.11",
                "v1.0.0-alpha.22",
                "v1.0.0-alpha.1",
                "v1.0.0-alpha.2",
                "v1.0.0-rc.11",
                "v1.0.0-rc.22",
                "v1.0.0-rc.1",
                "v1.0.0-rc.2",
                "v1.0.0-rc.10",
                "v3.0.0",
                "v2.0.0-rc.1",
                "v2.0.0-rc.2",
                "v2.0.0-rc.3",
                "v2.0.0-rc.4",
                "v2.0.0-rc.10",
                "v2.0.0-rc.11",
                "v2.0.0-rc.20",
                "v2.0.0-rc.21",
                "v2.0.0-rc.22",
            )

            createNewFile("11 commit.txt")
            tagCall("v5.0.0")
            tagCall("v200")
            tagCall("v20.20")
            assertTags(
                "v4.0.0",
                "v1.0.0",
                "v2.0.0",
                "v1.0.0-alpha.10",
                "v1.0.0-alpha.11",
                "v1.0.0-alpha.22",
                "v1.0.0-alpha.1",
                "v1.0.0-alpha.2",
                "v1.0.0-rc.11",
                "v1.0.0-rc.22",
                "v1.0.0-rc.1",
                "v1.0.0-rc.2",
                "v1.0.0-rc.10",
                "v3.0.0",
                "v2.0.0-rc.1",
                "v2.0.0-rc.2",
                "v2.0.0-rc.3",
                "v2.0.0-rc.4",
                "v2.0.0-rc.10",
                "v2.0.0-rc.11",
                "v2.0.0-rc.20",
                "v2.0.0-rc.21",
                "v2.0.0-rc.22",
                "v5.0.0",
            )
        }
    }

    @Test
    fun `last version tag in current branch`() {
        initialCommitAnd {
            tagCall("hello")
            tagCall("vhello")
            tagCall("v100")

            git.lastVersionTagInCurrentBranch("v").shouldBeNull()

            createNewFile("2 commit.txt")
            addAllCall()
            commitCall("2 commit")

            createNewFile("3 commit.txt")
            addAllCall()
            commitCall("3 commit")
            tagCall("v4.0.0")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v4.0.0")

            createNewFile("4 commit.txt")
            addAllCall()
            commitCall("4 commit")
            tagCall("v1.0.0")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0")

            createNewFile("5 commit.txt")
            addAllCall()
            commitCall("5 commit")
            tagCall("v2.0.0")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v2.0.0")

            createNewFile("6 commit.txt")
            addAllCall()
            commitCall("6 commit")
            tagCall("v1.0.0-alpha.22")
            tagCall("v1.0.0-alpha.11")
            tagCall("v1.0.0-alpha.10")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0-alpha.22")

            createNewFile("7 commit.txt")
            addAllCall()
            commitCall("7 commit")
            tagCall("v1.0.0-alpha.2")
            tagCall("v1.0.0-alpha.1")
            tagCall("v1.0.0-rc.22")
            tagCall("v1.0.0-rc.11")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0-rc.22")

            createNewFile("8 commit.txt")
            addAllCall()
            commitCall("8 commit")
            tagCall("v1.0.0-rc.10")
            tagCall("v1.0.0-rc.2")
            tagCall("v1.0.0-rc.1")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0-rc.10")

            createNewFile("9 commit.txt")
            addAllCall()
            commitCall("9 commit")
            tagCall("v3.0.0")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v3.0.0")

            createNewFile("10 commit.txt")
            addAllCall()
            commitCall("8 commit")
            tagCall("v2.0.0-rc.22")
            tagCall("v2.0.0-rc.11")
            tagCall("v2.0.0-rc.20")
            tagCall("v2.0.0-rc.10")
            tagCall("v2.0.0-rc.3")
            tagCall("v2.0.0-rc.4")
            tagCall("v2.0.0-rc.21")
            tagCall("v2.0.0-rc.2")
            tagCall("v2.0.0-rc.1")

            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v2.0.0-rc.22")

            createNewFile("11 commit.txt")
            tagCall("v5.0.0")
            tagCall("v200")
            tagCall("v20.20")
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v5.0.0")
        }
    }
}
