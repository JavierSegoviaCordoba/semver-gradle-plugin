package com.javiersc.semver.gradle.plugin

import org.junit.Test

class SemVerScopeTest {

    @Test
    fun `scope=major 1_0_0 to 2_0_0`() =
        testSandbox(
            sandboxPath = "semver/scope/major 1_0_0 to 2_0_0",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )

    @Test
    fun `scope=malformed (1)`() =
        testSandbox(
            sandboxPath = "semver/scope/malformed (1)",
            buildAndFail = true,
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = { _, _ -> },
        )

    @Test
    fun `scope=minor 1_0_0 to 1_1_0`() =
        testSandbox(
            sandboxPath = "semver/scope/minor 1_0_0 to 1_1_0",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )

    @Test
    fun `scope=patch 1_0_0 to 1_0_1`() =
        testSandbox(
            sandboxPath = "semver/scope/patch 1_0_0 to 1_0_1",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )
}
