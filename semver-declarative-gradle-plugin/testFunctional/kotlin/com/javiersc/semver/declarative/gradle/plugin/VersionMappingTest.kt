package com.javiersc.semver.declarative.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.project.gradle.plugin.Insignificant
import com.javiersc.semver.project.gradle.plugin.assertVersion
import com.javiersc.semver.project.gradle.plugin.createGitIgnore
import com.javiersc.semver.project.gradle.plugin.git
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test
import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.GradleRunner

internal class VersionMappingTest : GradleTestKitTest() {

    @Test
    fun `GIVEN current-version-metadata WHEN run assemble THEN map version metadata`() {
        gradleTestKitTest("version-mapping/current-version-metadata") {
            initializeRepo()
            gradlew("assemble", "-Psemver.tagPrefix=v")
            projectDir.assertVersion("v", "0.1.0", Insignificant.Hash)
            projectDir
                .resolve("build/semver/version.txt")
                .readLines()
                .first()
                .shouldContain("+1.9.0")
        }
    }

    @Test
    fun `GIVEN major-minor-patch WHEN run assemble THEN map version major minor patch`() {
        gradleTestKitTest("version-mapping/major-minor-patch") {
            initializeRepo()
            gradlew("assemble", "-Psemver.tagPrefix=v")
            projectDir.assertVersion("v", "2.4.5", Insignificant.Hash)
        }
    }

    @Test
    fun `GIVEN override-and-map WHEN run assemble THEN apply override version before map version`() {
        gradleTestKitTest("version-mapping/override-and-map") {
            initializeRepo()
            gradlew("assemble", "-Psemver.tagPrefix=v")
            projectDir.assertVersion("v", "3.5.7+1.9.0")
        }
    }

    @Test
    fun `GIVEN stage WHEN run assemble THEN map version stage`() {
        gradleTestKitTest("version-mapping/stage") {
            initializeRepo()
            gradlew("assemble", "-Psemver.tagPrefix=v")
            projectDir.assertVersion("v", "0.1.7-rc.4", Insignificant.Hash)
        }
    }

    @Test
    fun `GIVEN version-override WHEN run assemble THEN override version`() {
        gradleTestKitTest("version-mapping/version-override") {
            initializeRepo()
            gradlew("assemble", "--stacktrace", "-Psemver.tagPrefix=v")
            projectDir.assertVersion("v", "6.8.9")
        }
    }

    private fun GradleRunner.initializeRepo() {
        Git.init().setDirectory(projectDir).call()
        projectDir.createGitIgnore()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Initial commit").call()

        projectDir.resolve("config.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add config").call()

        projectDir.resolve("plugin.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add plugin").call()
    }
}
