package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.project.gradle.plugin.Insignificant.Dirty
import com.javiersc.semver.project.gradle.plugin.Insignificant.Hash
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class GradleFeaturesTest : GradleTestKitTest() {

    @Test
    fun `android configuration cache clean v1_0_0`() {
        gradleTestKitTest("gradle-features/android configuration cache clean v1_0_0") {
            beforeTest()
            testConfigurationCache(expectTaskOutcome = TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `android build cache clean v1_0_0`() {
        gradleTestKitTest("gradle-features/android build cache clean v1_0_0") {
            beforeTest()
            testBuildCache()
        }
    }

    @Test
    fun `build cache clean v1_0_0`() {
        gradleTestKitTest("gradle-features/build cache clean v1_0_0") {
            beforeTest()
            testBuildCache()
        }
    }

    @Test
    fun `configuration cache clean v1_0_0`() {
        gradleTestKitTest("gradle-features/configuration cache clean v1_0_0") {
            beforeTest() // initial commit
            testConfigurationCache(expectTaskOutcome = TaskOutcome.SUCCESS)
            projectDir.resolve("one.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("One").call() // change hash
            testConfigurationCache(expectTaskOutcome = TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `project isolation clean v1_0_0`() {
        gradleTestKitTest("gradle-features/project isolation clean v1_0_0") { beforeTest() }
    }

    private fun GradleRunner.beforeTest() {
        withArgumentsFromTXT()

        projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
        withArgumentsFromTXT()

        build()
        projectDir.assertVersion("v", "0.9.0", Hash)

        projectDir.resolve("dirty.txt").createNewFile()
        git.add().addFilepattern(".").call()

        build()
        projectDir.assertVersion("v", "0.9.0", Dirty)

        projectDir
            .resolve("expect-version.txt")
            .writeText(
                """
                    |0.9.0+DIRTY
                    |v0.9.0+DIRTY
                    |
                """
                    .trimMargin()
            )
        projectDir.assertVersion("v", "0.9.0", Dirty)

        build()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Change expect-version").call()

        build()
        projectDir.assertVersion("v", "0.9.0", Hash)

        val LAST_0_9_0_HASH_1 = projectDir.resolve("build/semver/version.txt").readLines()[1]
        projectDir
            .resolve("expect-version.txt")
            .writeText(
                """
                    |0.9.1
                    |v0.9.1
                    |
                """
                    .trimMargin()
            )

        git.add().addFilepattern(".").call()
        git.commit().setMessage("Change expect-version again").call()

        build()
        val LAST_0_9_0_HASH_2 = projectDir.resolve("build/semver/version.txt").readLines()[1]

        LAST_0_9_0_HASH_1.shouldNotBe(LAST_0_9_0_HASH_2)

        gradlew("createSemverTag", "-Psemver.tagPrefix=v")
        projectDir.assertVersion("v", "0.9.1")

        withArgumentsFromTXT()

        build()
        projectDir.assertVersion("v", "0.9.1")

        build()
        projectDir.assertVersion("v", "0.9.1")
    }
}
