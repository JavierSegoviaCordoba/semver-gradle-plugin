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
    fun `push tag`() {
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
                        .build())

            git.repository.directory.copyRecursively(remoteBuildGitDir)

            git.remoteSetUrl()
                .setRemoteName("origin")
                .setRemoteUri(URIish(remoteBuildGitDir.toURI().toString()))
                .call()

            gradlew("pushSemverTag", "-Psemver.tagPrefix=v")

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
