package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.headRevCommitInBranch
import io.kotest.matchers.shouldBe
import java.io.File
import java.time.Instant
import java.util.Date
import kotlin.test.Test
import org.gradle.testkit.runner.BuildResult

class VersionBuildDirTest {

    @Test
    fun `clean without tag in current commit - hash`() =
        testSandbox(
            sandboxPath = "semver/version-build-dir/clean-with-no-tag (hash)",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                File("$this/expect-version.txt").apply {
                    createNewFile()
                    writeText(expectVersion)
                }
            },
            test = ::testSemVerVersionBuildDir,
        )

    @Test
    fun `no clean without tag in current commit - timestamp`() =
        testSandbox(
            sandboxPath = "semver/version-build-dir/no-clean-with-no-tag (timestamp)",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                File("$this/new-2.txt").createNewFile()
                File("$this/expect-version.txt").apply {
                    createNewFile()
                    writeText(expectVersion)
                }
            },
            test = ::testSemVerVersionBuildDir,
        )

    @Test
    fun `clean 1_0_0`() =
        testSandbox(
            sandboxPath = "semver/version-build-dir/clean 1_0_0",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()
            },
            test = ::testSemVerVersionBuildDir,
        )
}

@Suppress("UNUSED_PARAMETER")
internal fun testSemVerVersionBuildDir(result: BuildResult, testProjectDir: File) {
    val version = File("$testProjectDir/build/semver/version.txt").readText()
    val expectVersion = File("$testProjectDir/expect-version.txt").readText()
    version shouldBe expectVersion
}

internal val File.expectVersion: String
    get() {
        val mockDate = Date.from(Instant.ofEpochSecond(0))
        val additionalData = parentFile.git.calculateAdditionalVersionData(mockDate)
        return """
            |1.0.0$additionalData
            |v1.0.0$additionalData
            |
        """.trimMargin()
    }
