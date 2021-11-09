package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import io.kotest.matchers.shouldBe
import java.io.File
import java.time.Instant
import java.util.Date
import kotlin.test.Test
import org.gradle.testkit.runner.BuildResult

class VersionBuildDirTest {

    @Test
    fun `clean without tag in current commit`() =
        testSandbox(
            sandboxPath = "semver/version-build-dir/clean-with-no-tag",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                File("$this/expect-version.txt").apply {
                    createNewFile()
                    writeText("1.0.0${parentFile.git.calculateAdditionalVersionData()}\n")
                }
            },
            test = ::testSemVerVersionBuildDir,
        )

    @Test
    fun `no clean without tag in current commit`() =
        testSandbox(
            sandboxPath = "semver/version-build-dir/no-clean-with-no-tag",
            beforeTest = {
                val mockDate = Date.from(Instant.ofEpochSecond(0))
                generateInitialCommitAddVersionTagAndAddNewCommit()
                File("$this/new-2.txt").createNewFile()
                File("$this/expect-version.txt").apply {
                    createNewFile()
                    writeText("1.0.0${parentFile.git.calculateAdditionalVersionData(mockDate)}\n")
                }
            },
            test = ::testSemVerVersionBuildDir,
        )
}

@Suppress("UNUSED_PARAMETER")
internal fun testSemVerVersionBuildDir(result: BuildResult, testProjectDir: File) {
    val version = File("$testProjectDir/build/semver/version.txt").readText()
    val expectVersion = File("$testProjectDir/expect-version.txt").readLines().first()
    version shouldBe expectVersion
}
