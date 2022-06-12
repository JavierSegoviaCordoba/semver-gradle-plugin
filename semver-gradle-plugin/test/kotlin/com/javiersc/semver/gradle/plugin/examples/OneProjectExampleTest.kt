package com.javiersc.semver.gradle.plugin.examples

import com.javiersc.gradle.testkit.extensions.gradleTestKitTest
import com.javiersc.gradle.testkit.extensions.gradlew
import com.javiersc.semver.gradle.plugin.setup.Insignificant.Dirty
import com.javiersc.semver.gradle.plugin.setup.Insignificant.Hash
import com.javiersc.semver.gradle.plugin.setup.assertVersion
import com.javiersc.semver.gradle.plugin.setup.createGitIgnore
import com.javiersc.semver.gradle.plugin.setup.git
import kotlin.test.Test
import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.GradleRunner

internal class OneProjectExampleTest {

    @Test
    fun `one project`() {
        gradleTestKitTest("examples/one-project") {
            `0_ Initial repo state`()
            `1_ Run gradlew assemble`()
            `2_ Create a new file and run gradlew assemble`()
            `3_ Add the new file to git, commit it, and run gradlew semverCreateTag`()
            `4_ Run gradlew assemble`()
            `5_ Create and add new file and run`()
            `6_ Run gradlew semverCreateTag`()
            `7_ Create and add to git a new file, then run gradlew semverCreateTag stage=alpha`()
            `8_ Run gradlew semverCreateTag stage=beta`()
            `9_ Run gradlew semverCreateTag stage=final`()
            `10_ Run gradlew semverCreateTag stage=final scope=major`()
            `11_ Run gradlew semverCreateTag stage=snapshot`()
        }
    }

    private fun GradleRunner.`0_ Initial repo state`() {
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

    private fun GradleRunner.`1_ Run gradlew assemble`() {
        gradlew("assemble", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.0", Hash)
    }

    private fun GradleRunner.`2_ Create a new file and run gradlew assemble`() {
        projectDir.resolve("new2.txt").createNewFile()

        gradlew("assemble", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.0", Dirty)
    }

    private fun GradleRunner
        .`3_ Add the new file to git, commit it, and run gradlew semverCreateTag`() {
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new2").call()

        gradlew("semverCreateTag", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.0")
    }

    private fun GradleRunner.`4_ Run gradlew assemble`() {
        gradlew("assemble", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.0")
    }

    private fun GradleRunner.`5_ Create and add new file and run`() {
        projectDir.resolve("new5.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new5").call()
        gradlew("assemble", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.0", Hash)
    }

    private fun GradleRunner.`6_ Run gradlew semverCreateTag`() {
        gradlew("semverCreateTag", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.1")
    }

    private fun GradleRunner
        .`7_ Create and add to git a new file, then run gradlew semverCreateTag stage=alpha`() {
        projectDir.resolve("new7.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new7").call()
        gradlew("semverCreateTag", "-Psemver.stage=alpha", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.2-alpha.1")
    }

    private fun GradleRunner.`8_ Run gradlew semverCreateTag stage=beta`() {
        gradlew("semverCreateTag", "-Psemver.stage=beta", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.2-beta.1")
    }

    private fun GradleRunner.`9_ Run gradlew semverCreateTag stage=final`() {
        gradlew("semverCreateTag", "-Psemver.stage=final", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "0.1.2")
    }

    private fun GradleRunner.`10_ Run gradlew semverCreateTag stage=final scope=major`() {
        gradlew(
            "semverCreateTag",
            "-Psemver.stage=final",
            "-Psemver.scope=major",
            "-Psemver.tagPrefix=v"
        )

        projectDir.assertVersion("v", "1.0.0")
    }

    private fun GradleRunner.`11_ Run gradlew semverCreateTag stage=snapshot`() {
        gradlew("semverCreateTag", "-Psemver.stage=snapshot", "-Psemver.tagPrefix=v")

        projectDir.assertVersion("v", "1.0.1-SNAPSHOT")
    }
}
