package com.javiersc.semver.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTest
import com.javiersc.semver.gradle.plugin.git.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.git.headRevCommitInBranch
import com.javiersc.semver.gradle.plugin.setup.Insignificant.Dirty
import com.javiersc.semver.gradle.plugin.setup.assertVersion
import com.javiersc.semver.gradle.plugin.setup.assertVersionFromExpectVersionFiles
import com.javiersc.semver.gradle.plugin.setup.generateInitialCommitAddVersionTagAndAddNewCommit
import com.javiersc.semver.gradle.plugin.setup.git
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

internal class VersionBuildDirTest : GradleTest() {

    @Test
    fun `clean v1_0_0`() {
        gradleTestKitTest("version-build-dir/clean v1_0_0") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `clean without tag in current commit - hash`() {
        gradleTestKitTest("version-build-dir/clean-with-no-tag-current-commit (hash)") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `no clean without tag in current commit - dirty`() {
        gradleTestKitTest("version-build-dir/no-clean-with-no-tag-current-commit (dirty)") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            projectDir.resolve("new-2.txt").createNewFile()
            projectDir.resolve("expect-version.txt").apply {
                createNewFile()
                val additionalData = parentFile.git.calculateAdditionalVersionData(tagPrefix = "v")
                writeText(
                    """
                        |1.0.0$additionalData
                        |v1.0.0$additionalData
                        |
                    """.trimMargin()
                )
            }

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `no clean createSemverTag should fail`() {
        gradleTestKitTest("version-build-dir/no-clean-with-no-tag-current-commit (dirty)") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            projectDir.resolve("new-2.txt").createNewFile()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersion("v", "1.0.0", Dirty)

            withArguments("createSemverTag", "-Psemver.tagPrefix=v")
            val result = buildAndFail()
            projectDir.assertVersionFromExpectVersionFiles()
            result.output.shouldContain("A semver tag can't be created if the repo is not clean")
        }
    }
}
