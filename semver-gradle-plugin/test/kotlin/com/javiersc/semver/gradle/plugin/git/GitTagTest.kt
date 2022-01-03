package com.javiersc.semver.gradle.plugin.git

import com.javiersc.semver.gradle.plugin.addNewFile
import com.javiersc.semver.gradle.plugin.git
import com.javiersc.semver.gradle.plugin.initialCommitAnd
import com.javiersc.semver.gradle.plugin.internal.GitRef
import com.javiersc.semver.gradle.plugin.internal.headCommit
import com.javiersc.semver.gradle.plugin.internal.isThereVersionTag
import com.javiersc.semver.gradle.plugin.internal.lastVersionTagInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.tagName
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentBranchHash
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentBranchName
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentBranchRef
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentCommit
import com.javiersc.semver.gradle.plugin.internal.tagsInRepo
import com.javiersc.semver.gradle.plugin.internal.tagsInRepoHash
import com.javiersc.semver.gradle.plugin.internal.tagsInRepoName
import com.javiersc.semver.gradle.plugin.internal.tagsInRepoRef
import com.javiersc.semver.gradle.plugin.internal.versionTagsInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder
import com.javiersc.semver.gradle.plugin.internal.versionTagsInCurrentCommit
import com.javiersc.semver.gradle.plugin.internal.versionTagsSortedBySemver
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import org.eclipse.jgit.lib.Ref

internal class GitTagTest {

    @Test
    fun `tags in repo`() {
        initialCommitAnd() {
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()
            git.tagsInRepo.map(GitRef.Tag::name).shouldBe(listOf("hello", "v100", "vhello"))

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.tagsInRepo.map(GitRef.Tag::name) shouldBe
                listOf("hello", "v100", "v3.0.0", "vhello")

            git.tag().setName("v1.0.0").call()
            git.tagsInRepo.map(GitRef.Tag::name) shouldBe
                listOf("hello", "v1.0.0", "v100", "v3.0.0", "vhello")

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()

            git.tagsInRepo.map(GitRef.Tag::name) shouldBe
                listOf("hello", "v1.0.0", "v100", "v2.0.0", "v20.20", "v200", "v3.0.0", "vhello")
        }
    }

    @Test
    fun `tags in repo ref`() {
        initialCommitAnd {
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()
            git.tagsInRepoRef.map(Ref::tagName).shouldBe(listOf("hello", "v100", "vhello"))

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.tagsInRepoRef
                .map(Ref::tagName)
                .shouldBe(listOf("hello", "v100", "v3.0.0", "vhello"))

            git.tag().setName("v1.0.0").call()
            git.tagsInRepoRef.map(Ref::tagName) shouldBe
                listOf("hello", "v1.0.0", "v100", "v3.0.0", "vhello")

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
            git.tagsInRepoRef.map(Ref::tagName) shouldBe
                listOf("hello", "v1.0.0", "v100", "v2.0.0", "v20.20", "v200", "v3.0.0", "vhello")
        }
    }

    @Test
    fun `tags in repo hash`() {
        initialCommitAnd {
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })

            git.tag().setName("v1.0.0").call()
            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
            git.tagsInRepoHash.shouldBe(git.tagsInRepo.map { tag -> tag.commit.hash })
        }
    }

    @Test
    fun `tags in repo name`() {
        initialCommitAnd {
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()
            git.tagsInRepoName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/vhello",
                )
            )

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.tagsInRepoName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            git.tag().setName("v1.0.0").call()
            git.tagsInRepoName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v1.0.0",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()
            git.tagsInCurrentBranch
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "vhello",
                    )
                )

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
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

            git.tag().setName("v1.0.0").call()
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

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()
            git.tagsInCurrentBranchRef
                .map(Ref::tagName)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "vhello",
                    )
                )

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
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

            git.tag().setName("v1.0.0").call()
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

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }

            git.tag().setName("v1.0.0").call()
            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
            git.tagsInCurrentBranchHash shouldBe
                git.tagsInCurrentBranch.map { tag -> tag.commit.hash }
        }
    }

    @Test
    fun `tags in current branch name`() {
        initialCommitAnd {
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()
            git.tagsInCurrentBranchName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/vhello",
                )
            )

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.tagsInCurrentBranchName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            git.tag().setName("v1.0.0").call()
            git.tagsInCurrentBranchName.shouldBe(
                listOf(
                    "refs/tags/hello",
                    "refs/tags/v1.0.0",
                    "refs/tags/v100",
                    "refs/tags/v3.0.0",
                    "refs/tags/vhello",
                )
            )

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()
            git.tagsInCurrentCommit(git.headCommit.commit.hash)
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "hello",
                        "v100",
                        "vhello",
                    )
                )

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v1.0.0").call()
            git.tagsInCurrentCommit(git.headCommit.commit.hash)
                .map(GitRef.Tag::name)
                .shouldBe(
                    listOf(
                        "v1.0.0",
                    )
                )

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v2.0.0"))
            git.isThereVersionTag(tagPrefix = "v").shouldBeTrue()
        }
    }

    @Test
    fun `version tags in current commit`() {
        initialCommitAnd {
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v3.0.0"))

            git.tag().setName("v1.0.0").call()
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            git.tag().setName("v2.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
            git.versionTagsInCurrentCommit(hash = git.headCommit.commit.hash, tagPrefix = "v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))
        }
    }

    @Test
    fun `version tags in current branch`() {
        initialCommitAnd {
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.versionTagsInCurrentBranch("v").map(GitRef.Tag::name).shouldBe(emptyList())

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.versionTagsInCurrentBranch("v").map(GitRef.Tag::name).shouldBe(listOf("v3.0.0"))

            git.tag().setName("v1.0.0").call()
            git.versionTagsInCurrentBranch("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            git.tag().setName("v2.0.0").call()
            git.versionTagsInCurrentBranch("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))

            git.tag().setName("v1.0.0-alpha.22").call()
            git.tag().setName("v1.0.0-alpha.11").call()
            git.tag().setName("v1.0.0-alpha.10").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            git.tag().setName("v1.0.0-alpha.1").call()
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

            git.tag().setName("v1.0.0-rc.22").call()
            git.tag().setName("v1.0.0-rc.11").call()
            git.tag().setName("v1.0.0-rc.10").call()
            git.tag().setName("v1.0.0-rc.2").call()
            git.tag().setName("v1.0.0-rc.1").call()
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

            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.versionTagsSortedBySemver("v").map(GitRef.Tag::name).shouldBe(emptyList())

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.versionTagsSortedBySemver("v").map(GitRef.Tag::name).shouldBe(listOf("v3.0.0"))

            git.tag().setName("v1.0.0").call()
            git.versionTagsSortedBySemver("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            git.tag().setName("v2.0.0").call()
            git.versionTagsSortedBySemver("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))

            git.tag().setName("v1.0.0-alpha.22").call()
            git.tag().setName("v1.0.0-alpha.11").call()
            git.tag().setName("v1.0.0-alpha.10").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            git.tag().setName("v1.0.0-alpha.1").call()

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

            git.tag().setName("v1.0.0-rc.22").call()
            git.tag().setName("v1.0.0-rc.11").call()
            git.tag().setName("v1.0.0-rc.10").call()
            git.tag().setName("v1.0.0-rc.2").call()
            git.tag().setName("v1.0.0-rc.1").call()

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

            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()

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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            addNewFile("Second commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Second commit").call()

            git.tag().setName("v3.0.0").call()
            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v3.0.0"))

            git.tag().setName("v1.0.0").call()
            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v3.0.0"))

            git.tag().setName("v2.0.0").call()
            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(listOf("v1.0.0", "v2.0.0", "v3.0.0"))

            git.tag().setName("v1.0.0-alpha.22").call()
            git.tag().setName("v1.0.0-alpha.11").call()
            git.tag().setName("v1.0.0-alpha.10").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            git.tag().setName("v1.0.0-alpha.1").call()

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

            git.tag().setName("v1.0.0-rc.22").call()
            git.tag().setName("v1.0.0-rc.11").call()
            git.tag().setName("v1.0.0-rc.10").call()
            git.tag().setName("v1.0.0-rc.2").call()
            git.tag().setName("v1.0.0-rc.1").call()

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

            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()

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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                .map(GitRef.Tag::name)
                .shouldBe(emptyList())

            addNewFile("2 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()

            fun assertTags(vararg versionTag: String) {
                git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder("v")
                    .map(GitRef.Tag::name)
                    .shouldBe(versionTag.toList())
            }

            addNewFile("3 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()
            git.tag().setName("v4.0.0").call()
            assertTags("v4.0.0")

            addNewFile("4 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("4 commit").call()
            git.tag().setName("v1.0.0").call()
            assertTags("v4.0.0", "v1.0.0")

            addNewFile("5 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("5 commit").call()
            git.tag().setName("v2.0.0").call()
            assertTags("v4.0.0", "v1.0.0", "v2.0.0")

            addNewFile("6 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("6 commit").call()
            git.tag().setName("v1.0.0-alpha.22").call()
            git.tag().setName("v1.0.0-alpha.11").call()
            git.tag().setName("v1.0.0-alpha.10").call()
            assertTags(
                "v4.0.0",
                "v1.0.0",
                "v2.0.0",
                "v1.0.0-alpha.10",
                "v1.0.0-alpha.11",
                "v1.0.0-alpha.22",
            )

            addNewFile("7 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("7 commit").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            git.tag().setName("v1.0.0-alpha.1").call()
            git.tag().setName("v1.0.0-rc.22").call()
            git.tag().setName("v1.0.0-rc.11").call()
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

            addNewFile("8 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("8 commit").call()
            git.tag().setName("v1.0.0-rc.10").call()
            git.tag().setName("v1.0.0-rc.2").call()
            git.tag().setName("v1.0.0-rc.1").call()
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

            addNewFile("9 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("9 commit").call()
            git.tag().setName("v3.0.0").call()
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

            addNewFile("10 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("8 commit").call()
            git.tag().setName("v2.0.0-rc.22").call()
            git.tag().setName("v2.0.0-rc.11").call()
            git.tag().setName("v2.0.0-rc.20").call()
            git.tag().setName("v2.0.0-rc.10").call()
            git.tag().setName("v2.0.0-rc.3").call()
            git.tag().setName("v2.0.0-rc.4").call()
            git.tag().setName("v2.0.0-rc.21").call()
            git.tag().setName("v2.0.0-rc.2").call()
            git.tag().setName("v2.0.0-rc.1").call()

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

            addNewFile("11 commit.txt")
            git.tag().setName("v5.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
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
            git.tag().setName("hello").call()
            git.tag().setName("vhello").call()
            git.tag().setName("v100").call()

            git.lastVersionTagInCurrentBranch("v").shouldBeNull()

            addNewFile("2 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("2 commit").call()

            addNewFile("3 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("3 commit").call()
            git.tag().setName("v4.0.0").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v4.0.0")

            addNewFile("4 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("4 commit").call()
            git.tag().setName("v1.0.0").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0")

            addNewFile("5 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("5 commit").call()
            git.tag().setName("v2.0.0").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v2.0.0")

            addNewFile("6 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("6 commit").call()
            git.tag().setName("v1.0.0-alpha.22").call()
            git.tag().setName("v1.0.0-alpha.11").call()
            git.tag().setName("v1.0.0-alpha.10").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0-alpha.22")

            addNewFile("7 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("7 commit").call()
            git.tag().setName("v1.0.0-alpha.2").call()
            git.tag().setName("v1.0.0-alpha.1").call()
            git.tag().setName("v1.0.0-rc.22").call()
            git.tag().setName("v1.0.0-rc.11").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0-rc.22")

            addNewFile("8 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("8 commit").call()
            git.tag().setName("v1.0.0-rc.10").call()
            git.tag().setName("v1.0.0-rc.2").call()
            git.tag().setName("v1.0.0-rc.1").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v1.0.0-rc.10")

            addNewFile("9 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("9 commit").call()
            git.tag().setName("v3.0.0").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v3.0.0")

            addNewFile("10 commit.txt")
            git.add().addFilepattern(".").call()
            git.commit().setMessage("8 commit").call()
            git.tag().setName("v2.0.0-rc.22").call()
            git.tag().setName("v2.0.0-rc.11").call()
            git.tag().setName("v2.0.0-rc.20").call()
            git.tag().setName("v2.0.0-rc.10").call()
            git.tag().setName("v2.0.0-rc.3").call()
            git.tag().setName("v2.0.0-rc.4").call()
            git.tag().setName("v2.0.0-rc.21").call()
            git.tag().setName("v2.0.0-rc.2").call()
            git.tag().setName("v2.0.0-rc.1").call()

            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v2.0.0-rc.22")

            addNewFile("11 commit.txt")
            git.tag().setName("v5.0.0").call()
            git.tag().setName("v200").call()
            git.tag().setName("v20.20").call()
            git.lastVersionTagInCurrentBranch("v")?.name.shouldBe("v5.0.0")
        }
    }
}
