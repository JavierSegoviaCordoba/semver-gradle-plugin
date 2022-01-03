package com.javiersc.semver.gradle.plugin.examples

import com.javiersc.semver.SemanticVersionException
import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.copyResourceTo
import com.javiersc.semver.gradle.plugin.createGitIgnore
import com.javiersc.semver.gradle.plugin.createSandboxFile
import com.javiersc.semver.gradle.plugin.gradlew
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import kotlin.test.Test
import org.eclipse.jgit.api.Git

class AllProjectsExampleTest {

    private val testProjectDir: File = createSandboxFile("all-projects")
    private val git: Git by lazy { Git.init().setDirectory(testProjectDir).call() }

    init {
        "examples/all-projects" copyResourceTo testProjectDir
    }

    @Test
    fun `all projects`() {
        `0_ Initial repo state`()
        `1_ Run gradlew`()
        `2_ Create a new file and run gradlew`()
        `3_ Add the new file to git, commit it, and run gradlew createSemverTag`()
        `4_ Run gradlew`()
        `5_ Create and add new file and run`()
        `6_ Run gradlew createSemverTag`()
        `7_ Create and add to git a new file, then run gradlew createSemverTag -Psemver_stage=alpha`()
        `8_ Run gradlew createSemverTag -Psemver_stage=beta`()
        `9_ Run gradlew createSemverTag -Psemver_stage=final`()
        `10_ Run gradlew createSemverTag -Psemver_stage=final -Psemver_scope=major`()
        `11_ Run gradlew createSemverTag -Psemver_stage=snapshot`()
    }

    private fun `0_ Initial repo state`() {
        testProjectDir.createGitIgnore()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Initial commit").call()

        File("$testProjectDir/config.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add config").call()

        File("$testProjectDir/plugin.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add plugin").call()
    }

    private fun `1_ Run gradlew`() {
        testProjectDir.gradlew()

        assertVersion("v", "0.1.0", Insignificant.Hash)
    }
    private fun `2_ Create a new file and run gradlew`() {
        File("$testProjectDir/new2.txt").createNewFile()

        testProjectDir.gradlew()

        assertVersion("v", "0.1.0", Insignificant.Timestamp)
    }

    private fun `3_ Add the new file to git, commit it, and run gradlew createSemverTag`() {
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new2").call()

        testProjectDir.gradlew("createSemverTag")

        assertVersion("v", "0.1.0")
    }

    private fun `4_ Run gradlew`() {
        testProjectDir.gradlew()

        assertVersion("v", "0.1.0")
    }

    private fun `5_ Create and add new file and run`() {
        File("$testProjectDir/new5.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new5").call()
        testProjectDir.gradlew()

        assertVersion("v", "0.1.0", Insignificant.Hash)
    }

    private fun `6_ Run gradlew createSemverTag`() {
        testProjectDir.gradlew("createSemverTag")

        assertVersion("v", "0.1.1")
    }

    private fun `7_ Create and add to git a new file, then run gradlew createSemverTag -Psemver_stage=alpha`() {
        File("$testProjectDir/new7.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add new7").call()
        testProjectDir.gradlew("createSemverTag", "-Psemver.stage=alpha")

        assertVersion("v", "0.1.2-alpha.1")
    }

    private fun `8_ Run gradlew createSemverTag -Psemver_stage=beta`() {
        testProjectDir.gradlew("createSemverTag", "-Psemver.stage=beta")

        assertVersion("v", "0.1.2-beta.1")
    }

    private fun `9_ Run gradlew createSemverTag -Psemver_stage=final`() {
        testProjectDir.gradlew("createSemverTag", "-Psemver.stage=final")

        assertVersion("v", "0.1.2")
    }

    private fun `10_ Run gradlew createSemverTag -Psemver_stage=final -Psemver_scope=major`() {
        testProjectDir.gradlew("createSemverTag", "-Psemver.stage=final", "-Psemver.scope=major")

        assertVersion("v", "1.0.0")
    }

    private fun `11_ Run gradlew createSemverTag -Psemver_stage=snapshot`() {
        testProjectDir.gradlew("createSemverTag", "-Psemver.stage=snapshot")

        assertVersion("v", "1.0.1-SNAPSHOT")
    }

    private fun assertVersion(
        prefix: String,
        version: String,
        insignificant: Insignificant? = null
    ) {
        val buildVersionFile = File("$testProjectDir/build/semver/version.txt")
        val buildVersion = buildVersionFile.readLines().first()
        println(buildVersion)
        val buildTagVersion = buildVersionFile.readLines()[1]
        println(buildTagVersion)
        when (insignificant) {
            Insignificant.Hash -> {
                buildVersion.startsWith(version).shouldBeTrue()
                buildTagVersion.startsWith("$prefix$version").shouldBeTrue()
                buildVersion.shouldContain("+")
                shouldThrow<SemanticVersionException> { Version(buildVersion) }
            }
            Insignificant.Timestamp -> {
                buildVersion.startsWith(version).shouldBeTrue()
                buildTagVersion.startsWith("$prefix$version").shouldBeTrue()
                buildVersion.shouldContain("+").shouldContain("T")
                shouldThrow<SemanticVersionException> { Version(buildVersion) }
            }
            else -> {
                buildVersionFile
                    .readText()
                    .shouldBe(
                        """
                           |$version
                           |$prefix$version
                           |
                        """.trimMargin()
                    )
            }
        }
    }

    private enum class Insignificant {
        Hash,
        Timestamp
    }
}