package com.javiersc.semver.gradle.plugin

import org.junit.Test

class SemVerStageTest {

    @Test
    fun `stage=alpha 1_0_0 to 1_0_1-alpha_1`() =
        testSandbox(
            sandboxPath = "semver/stage/alpha 1_0_0 to 1_0_1-alpha_1",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )

    @Test
    fun `stage=alpha 1_0_0-alpha-1 to 1_0_1-alpha_2`() =
        testSandbox(
            sandboxPath = "semver/stage/alpha 1_0_0-alpha-1 to 1_0_1-alpha_2",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )

    @Test
    fun `stage=beta 1_0_0 to 1_0_1-beta_1`() =
        testSandbox(
            sandboxPath = "semver/stage/beta 1_0_0 to 1_0_1-beta_1",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )

    @Test
    fun `stage=final 1_0_0-alpha_1 to 1_0_0`() =
        testSandbox(
            sandboxPath = "semver/stage/final 1_0_0-alpha_1 to 1_0_0",
            beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
            test = ::testSemVer,
        )
}
