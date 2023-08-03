package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import kotlin.test.Test

internal class VersionMappingTest : GradleTestKitTest() {

    @Test
    fun `v1_0_0 to v1_0_0+1_9_0`() {
        gradleTestKitTest("version-mapping/v1_0_0 to v1_0_0+1_9_0") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }
}
