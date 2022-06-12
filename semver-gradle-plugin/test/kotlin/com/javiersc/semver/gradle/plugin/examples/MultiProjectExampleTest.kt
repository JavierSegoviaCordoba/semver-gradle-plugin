package com.javiersc.semver.gradle.plugin.examples

import com.javiersc.gradle.testkit.extensions.gradleTestKitTest
import com.javiersc.gradle.testkit.extensions.gradlew
import com.javiersc.semver.gradle.plugin.setup.Insignificant.Dirty
import com.javiersc.semver.gradle.plugin.setup.Insignificant.Hash
import com.javiersc.semver.gradle.plugin.setup.assertVersion
import com.javiersc.semver.gradle.plugin.setup.createGitIgnore
import com.javiersc.semver.gradle.plugin.setup.git
import java.io.File
import kotlin.test.Test
import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.GradleRunner

/**
 * Modules:
 *
 * - `library-one` uses prefix: one
 *
 * - `library-a-four` uses prefix: four
 * - `library-b-four` uses prefix: four
 * - `library-c-four` uses prefix: four
 * - `library-d-four` uses prefix: four
 *
 * - `library-m-three` uses prefix: three
 * - `library-n-three` uses prefix: three
 * - `library-o-three` uses prefix: three
 *
 * - `library-y` uses no prefix
 * - `library-z` uses no prefix
 */
// TODO: TO BE COMPLETED
internal class MultiProjectExampleTest {

    @Test
    fun `multi project`() {
        gradleTestKitTest("examples/multi-project") {
            `0_ Initial repo state`()
            `1_ Run gradlew assemble`()
            `2_ Create a new file in library-one-a and run gradlew assemble`()
            `3_ Add the new file to git and commit it, then run gradlew semverCreateTag with tagPrefix=a`()
            `4_ Run gradlew assemble`()
            `5_ Create, add and commit a new file in library-one-a, then run gradlew assemble`()
            `6_ Run gradlew semverCreateTag tagPrefix=a`()
            `7_ Create, add to git and commit a new file in library-one-a, then run gradlew semverCreateTag stage=alpha tagPrefix=a`()
            `8_ Run gradlew semverCreateTag stage=beta tagPrefix=a`()
            `9_ Create, add to git and commit a new file in library-one-a, then run gradlew semverCreateTag stage=final tagPrefix=a`()
            `10_ Run gradlew semverCreateTag stage=final scope=major tagPrefix=a`()
            `11_ Run gradlew semverPrint stage=snapshot tagPrefix=a`()
        }
    }

    private fun GradleRunner.`0_ Initial repo state`() {
        Git.init().setDirectory(projectDir).call()
        projectDir.createGitIgnore()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Initial commit").call()

        projectDir.resolve("plugin.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add plugin").call()

        git.tag().setName("a1.0.0").call()
        git.tag().setName("b1.0.0").call()
        git.tag().setName("c1.0.0").call()
        git.tag().setName("1.0.0").call()

        projectDir.resolve("config.txt").createNewFile()
        git.add().addFilepattern(".").call()
    }

    private fun GradleRunner.`1_ Run gradlew assemble`() {
        gradlew("assemble")

        projectDirByName("library-one-a").assertVersion("a", "1.0.0", Dirty)
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Dirty)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Dirty)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Dirty)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Dirty)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Dirty)
    }

    private fun GradleRunner.`2_ Create a new file in library-one-a and run gradlew assemble`() {
        projectDirByName("library-one-a").resolve("new2.txt").createNewFile()

        gradlew("assemble")

        projectDirByName("library-one-a").assertVersion("a", "1.0.0", Dirty)
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Dirty)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Dirty)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Dirty)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Dirty)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Dirty)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Dirty)
    }

    private fun GradleRunner
        .`3_ Add the new file to git and commit it, then run gradlew semverCreateTag with tagPrefix=a`() {
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new2 to library-one-a").call()

        gradlew("semverCreateTag", "-Psemver.tagPrefix=a")

        projectDirByName("library-one-a").assertVersion("a", "1.0.1")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner.`4_ Run gradlew assemble`() {
        gradlew("assemble")

        projectDirByName("library-one-a").assertVersion("a", "1.0.1")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner
        .`5_ Create, add and commit a new file in library-one-a, then run gradlew assemble`() {
        projectDirByName("library-one-a").resolve("new5.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new5 to library-one-a").call()

        gradlew("assemble")

        projectDirByName("library-one-a").assertVersion("a", "1.0.1", Hash)
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner.`6_ Run gradlew semverCreateTag tagPrefix=a`() {
        gradlew("semverCreateTag", "-Psemver.tagPrefix=a")

        projectDirByName("library-one-a").assertVersion("a", "1.0.2")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner
        .`7_ Create, add to git and commit a new file in library-one-a, then run gradlew semverCreateTag stage=alpha tagPrefix=a`() {
        projectDirByName("library-one-a").resolve("new7.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new7 to library-one-a").call()

        gradlew("semverCreateTag", "-Psemver.stage=alpha", "-Psemver.tagPrefix=a")

        projectDirByName("library-one-a").assertVersion("a", "1.0.3-alpha.1")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner.`8_ Run gradlew semverCreateTag stage=beta tagPrefix=a`() {
        gradlew("semverCreateTag", "-Psemver.stage=beta", "-Psemver.tagPrefix=a")

        projectDirByName("library-one-a").assertVersion("a", "1.0.3-beta.1")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner
        .`9_ Create, add to git and commit a new file in library-one-a, then run gradlew semverCreateTag stage=final tagPrefix=a`() {
        projectDirByName("library-one-a").resolve("new9.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new9 to library-one-a").call()

        gradlew("semverCreateTag", "-Psemver.stage=final", "-Psemver.tagPrefix=a")

        projectDirByName("library-one-a").assertVersion("a", "1.0.3")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner
        .`10_ Run gradlew semverCreateTag stage=final scope=major tagPrefix=a`() {
        gradlew(
            "semverCreateTag",
            "-Psemver.stage=final",
            "-Psemver.scope=major",
            "-Psemver.tagPrefix=a"
        )

        projectDirByName("library-one-a").assertVersion("a", "2.0.0")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }

    private fun GradleRunner.`11_ Run gradlew semverPrint stage=snapshot tagPrefix=a`() {
        gradlew("semverPrint", "-Psemver.stage=snapshot", "-Psemver.tagPrefix=a")

        projectDirByName("library-one-a").assertVersion("a", "2.0.1-SNAPSHOT")
        projectDirByName("library-two-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-three-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-four-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-five-b").assertVersion("b", "1.0.0", Hash)
        projectDirByName("library-six-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-seven-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-eight-c").assertVersion("c", "1.0.0", Hash)
        projectDirByName("library-nine").assertVersion("", "1.0.0", Hash)
        projectDirByName("library-ten").assertVersion("", "1.0.0", Hash)
    }
}

private fun GradleRunner.projectDirByName(name: String): File =
    projectDir
        .walkTopDown()
        .first { it.isBuildGradleKts && it.parentFile.isDirectory && it.parentFile.name == name }
        .parentFile

private val File.isBuildGradleKts: Boolean
    get() = isFile && name == "build.gradle.kts"
