package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.project.gradle.plugin.internal.git.headRevCommitInBranch
import kotlin.test.Test

internal class VersionMappingTest : GradleTestKitTest() {

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

    @Test
    fun `v1_0_0 to v1_0_0+2_0_0-develop`() {
        gradleTestKitTest("version-mapping/v1_0_0 to v1_0_0+2_0_0-develop") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            git.branchCreate().setName("develop").call()
            git.checkout().setName("develop").call()
            git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }
}
