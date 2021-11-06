package com.javiersc.semver.gradle.plugin

import org.junit.Test

class SemVerStageScopeTest {

    @Test
    fun `stage=alpha + scope=major 1_0_0 to 2_0_0-alpha_1`() =
        testSandbox(
            sandboxPath = "semver/stage+scope/alpha+major 1_0_0 to 2_0_0-alpha_1",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )

    @Test
    fun `stage=alpha + scope=minor 1_0_0 to 1_1_0-alpha_1`() =
        testSandbox(
            sandboxPath = "semver/stage+scope/alpha+minor 1_0_0 to 1_1_0-alpha_1",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )

    @Test
    fun `stage=alpha + scope=patch 1_0_0 to 1_0_1-alpha_1`() =
        testSandbox(
            sandboxPath = "semver/stage+scope/alpha+patch 1_0_0 to 1_0_1-alpha_1",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )
}
