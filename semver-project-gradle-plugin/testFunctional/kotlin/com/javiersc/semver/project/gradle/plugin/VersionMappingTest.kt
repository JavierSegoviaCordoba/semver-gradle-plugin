package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.project.gradle.plugin.internal.git.headRevCommitInBranch
import kotlin.test.Test

internal class VersionMappingTest : GradleTestKitTest() {

    @Test
    fun `0_2_0+2_0_10`() {
        gradleTestKitTest("version-mapping/v0_2_0+2_0_10") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `v1_0_0 to v1_0_0+1_9_0`() {
        gradleTestKitTest("version-mapping/v1_0_0 to v1_0_0+1_9_0") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }
}
