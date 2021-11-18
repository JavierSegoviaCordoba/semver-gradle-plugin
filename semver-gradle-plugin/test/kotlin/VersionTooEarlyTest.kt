package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.headRevCommitInBranch
import kotlin.test.Test

class VersionTooEarlyTest {

    @Test
    fun root() =
        testSandbox(
            sandboxPath = "version-too-early/root buildAndFail noGeneratedVersion",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()
            },
            test = ::testSemVer,
        )

    @Test
    fun `root+subproject-with-plugin`() =
        testSandbox(
            sandboxPath =
                "version-too-early/root+subproject-with-plugin buildAndFail noGeneratedVersion",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()
            },
            test = ::testSemVer,
        )

    @Test
    fun `root+subproject-without-plugin`() =
        testSandbox(
            sandboxPath =
                "version-too-early/root+subproject-without-plugin buildAndFail noGeneratedVersion",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()
            },
            test = ::testSemVer,
        )
}
