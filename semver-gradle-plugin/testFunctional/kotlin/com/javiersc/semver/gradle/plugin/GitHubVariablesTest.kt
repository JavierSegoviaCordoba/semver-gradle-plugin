package com.javiersc.semver.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import io.kotest.matchers.string.shouldContain
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner

class GitHubVariablesTest : GradleTestKitTest() {

    private val GradleRunner.githubEnvPath: String
        get() = projectDir.resolve("environment/github.env").path

    val GradleRunner.githubEnvFile: File
        get() =
            projectDir.resolve(githubEnvPath).apply {
                parentFile.mkdirs()
                createNewFile()
            }

    private val GradleRunner.githubOutputPath: String
        get() = projectDir.resolve("environment/github.output").path

    private val GradleRunner.githubOutputFile: File
        get() =
            projectDir.resolve(githubOutputPath).apply {
                parentFile.mkdirs()
                createNewFile()
            }

    private fun GradleRunner.setEnvironmentVariables() {
        withEnvironment(
            mapOf(
                "GITHUB_ENV" to githubEnvFile.path,
                "GITHUB_OUTPUT" to githubOutputFile.path,
            )
        )
    }

    @Test
    fun `setting GITHUB_ENV`() {
        gradleTestKitTest("github-variables") {
            setEnvironmentVariables()

            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            gradlew(
                "printSemver",
                "-Psemver.tagPrefix=v",
                "-Psemver.stage=final",
                "--githubOnlyRoot",
                "--githubOutputTag",
                "--githubOutputVersion",
                "--githubOutput",
                "--githubEnvTag",
                "--githubEnvVersion",
                "--githubEnv",
            )

            githubEnvFile
                .readText()
                .shouldContain("SEMVER_TAG=v")
                .shouldContain("SEMVER_VERSION=0.9.1")
                .shouldContain("SEMVER=v0.9.1")

            gradlew(
                "printSemver",
                "-Psemver.tagPrefix=w",
                "-Psemver.stage=final",
                "--githubOutputTag",
                "--githubOutputVersion",
                "--githubOutput",
                "--githubEnvTag",
                "--githubEnvVersion",
                "--githubEnv",
            )

            githubEnvFile
                .readText()
                .shouldContain("SEMVER_TAG_LIBRARY=w")
                .shouldContain("SEMVER_VERSION_LIBRARY=0.1.1")
                .shouldContain("SEMVER_LIBRARY=w0.1.1")

            gradlew(
                "printSemver",
                "-Psemver.tagPrefix=v",
                "-Psemver.stage=alpha",
                "--githubOnlyRoot",
                "--githubOutputTag",
                "--githubOutputVersion",
                "--githubOutput",
                "--githubEnvTag",
                "--githubEnvVersion",
                "--githubEnv",
            )

            githubEnvFile
                .readText()
                .shouldContain("SEMVER_TAG=v")
                .shouldContain("SEMVER_VERSION=0.9.1-alpha.1")
                .shouldContain("SEMVER=v0.9.1-alpha.1")
        }
    }

    @Test
    fun `setting GITHUB_OUTPUT`() {
        gradleTestKitTest("github-variables") {
            setEnvironmentVariables()

            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            gradlew(
                "printSemver",
                "-Psemver.tagPrefix=v",
                "-Psemver.stage=final",
                "--githubOnlyRoot",
                "--githubOutputTag",
                "--githubOutputVersion",
                "--githubOutput",
                "--githubEnvTag",
                "--githubEnvVersion",
                "--githubEnv",
                stacktrace(),
            )

            githubOutputFile
                .readText()
                .shouldContain("SEMVER_TAG=v")
                .shouldContain("SEMVER_VERSION=0.9.1")
                .shouldContain("SEMVER=v0.9.1")

            gradlew(
                "printSemver",
                "-Psemver.tagPrefix=w",
                "-Psemver.stage=final",
                "--githubOutputTag",
                "--githubOutputVersion",
                "--githubOutput",
                "--githubEnvTag",
                "--githubEnvVersion",
                "--githubEnv",
            )

            githubOutputFile
                .readText()
                .shouldContain("SEMVER_TAG_LIBRARY=w")
                .shouldContain("SEMVER_VERSION_LIBRARY=0.1.1")
                .shouldContain("SEMVER_LIBRARY=w0.1.1")

            gradlew(
                "printSemver",
                "-Psemver.tagPrefix=v",
                "-Psemver.stage=alpha",
                "--githubOnlyRoot",
                "--githubOutputTag",
                "--githubOutputVersion",
                "--githubOutput",
                "--githubEnvTag",
                "--githubEnvVersion",
                "--githubEnv",
            )

            githubOutputFile
                .readText()
                .shouldContain("SEMVER_TAG=v")
                .shouldContain("SEMVER_VERSION=0.9.1-alpha.1")
                .shouldContain("SEMVER=v0.9.1-alpha.1")
        }
    }
}
