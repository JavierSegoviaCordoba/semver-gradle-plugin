package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.project.gradle.plugin.internal.git.tagName
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import java.io.File
import kotlin.test.Test
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.URIish

class PushSemverTagTaskTest : GradleTestKitTest() {

    @Test
    fun `push tag_1`() {
        gradleTestKitTest("push-tag") {
            // 1.0.0
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            val generatedDir: File = projectDir.resolve("build/generated/").apply(File::mkdirs)

            val remoteBuildGitDir: File = generatedDir.resolve(".git").apply(File::mkdirs)

            val remoteGit =
                Git(
                    FileRepositoryBuilder()
                        .setGitDir(remoteBuildGitDir)
                        .readEnvironment()
                        .findGitDir()
                        .build()
                )

            git.repository.directory.copyRecursively(remoteBuildGitDir)

            git.remoteSetUrl()
                .setRemoteName("origin")
                .setRemoteUri(URIish(remoteBuildGitDir.toURI().toString()))
                .call()

            // 1.0.1
            gradlew("pushSemverTag", "-Psemver.tagPrefix=v", "-Psemver.scope=patch")
            // 1.0.2-dev.1
            gradlew(
                "pushSemverTag",
                "-Psemver.tagPrefix=v",
                "-Psemver.scope=patch",
                "-Psemver.stage=dev"
            )
            // 1.0.2-dev.2
            gradlew("pushSemverTag", "-Psemver.tagPrefix=v")
            // 1.0.2-rc.1
            gradlew("pushSemverTag", "-Psemver.tagPrefix=v", "-Psemver.stage=rc")
            gradlew("pushSemverTag", "-Psemver.tagPrefix=v", "-Psemver.stage=final")

            remoteGit
                .tagList()
                .call()
                .map(Ref::tagName)
                .shouldNotBeEmpty()
                .shouldHaveSize(6)
                .shouldContain("v1.0.0")
                .shouldContain("v1.0.1")
                .shouldContain("v1.0.2-dev.1")
                .shouldContain("v1.0.2-dev.2")
                .shouldContain("v1.0.2-rc.1")
                .shouldContain("v1.0.2")
        }
    }

    @Test
    fun `push tag_2`() {
        gradleTestKitTest("push-tag") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            val generatedDir: File = projectDir.resolve("build/generated/").apply(File::mkdirs)

            val remoteBuildGitDir: File = generatedDir.resolve(".git").apply(File::mkdirs)

            val remoteGit =
                Git(
                    FileRepositoryBuilder()
                        .setGitDir(remoteBuildGitDir)
                        .readEnvironment()
                        .findGitDir()
                        .build()
                )

            git.repository.directory.copyRecursively(remoteBuildGitDir)

            git.remoteSetUrl()
                .setRemoteName("origin")
                .setRemoteUri(URIish(remoteBuildGitDir.toURI().toString()))
                .call()

            gradlew("pushSemverTag", "-Psemver.tagPrefix=v", "-Psemver.stage=auto")

            remoteGit
                .tagList()
                .call()
                .map(Ref::tagName)
                .shouldNotBeEmpty()
                .shouldHaveSize(2)
                .shouldContain("v1.0.0")
                .shouldContain("v1.0.1")
        }
    }

    @Test
    fun `push tag_3`() {
        gradleTestKitTest("push-tag") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            val generatedDir: File = projectDir.resolve("build/generated/").apply(File::mkdirs)

            val remoteBuildGitDir: File = generatedDir.resolve(".git").apply(File::mkdirs)

            val remoteGit =
                Git(
                    FileRepositoryBuilder()
                        .setGitDir(remoteBuildGitDir)
                        .readEnvironment()
                        .findGitDir()
                        .build()
                )

            git.repository.directory.copyRecursively(remoteBuildGitDir)

            git.remoteSetUrl()
                .setRemoteName("origin")
                .setRemoteUri(URIish(remoteBuildGitDir.toURI().toString()))
                .call()

            gradlew(
                "pushSemverTag",
                "-Psemver.tagPrefix=v",
                "-Psemver.stage=alpha",
                "-Psemver.scope=patch"
            )

            remoteGit
                .tagList()
                .call()
                .map(Ref::tagName)
                .shouldNotBeEmpty()
                .shouldHaveSize(2)
                .shouldContain("v1.0.0")
                .shouldContain("v1.0.1-alpha.1")
        }
    }

    @Test
    fun `push tag_4`() {
        gradleTestKitTest("push-tag") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            val generatedDir: File = projectDir.resolve("build/generated/").apply(File::mkdirs)

            val remoteBuildGitDir: File = generatedDir.resolve(".git").apply(File::mkdirs)

            val remoteGit =
                Git(
                    FileRepositoryBuilder()
                        .setGitDir(remoteBuildGitDir)
                        .readEnvironment()
                        .findGitDir()
                        .build()
                )

            git.repository.directory.copyRecursively(remoteBuildGitDir)

            git.remoteSetUrl()
                .setRemoteName("origin")
                .setRemoteUri(URIish(remoteBuildGitDir.toURI().toString()))
                .call()

            gradlew("pushSemverTag", "-Psemver.tagPrefix=v", "-Psemver.stage=auto")

            remoteGit
                .tagList()
                .call()
                .map(Ref::tagName)
                .shouldNotBeEmpty()
                .shouldHaveSize(2)
                .shouldContain("v1.0.0")
                .shouldContain("v1.0.1")
        }
    }
}
