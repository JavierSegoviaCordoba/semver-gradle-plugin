package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.headRevCommitInBranch
import java.io.File
import java.time.Instant
import java.util.Date
import kotlin.test.Test

class VersionBuildDirTest {

    @Test
    fun `clean without tag in current commit - hash`() =
        testSandbox(
            sandboxPath = "version-build-dir/clean-with-no-tag (hash)",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                File("$this/expect-version.txt").apply {
                    createNewFile()
                    writeText(expectVersion)
                }
            },
            test = ::testSemVer,
        )

    @Test
    fun `no clean without tag in current commit - timestamp`() =
        testSandbox(
            sandboxPath = "version-build-dir/no-clean-with-no-tag (timestamp)",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                File("$this/new-2.txt").createNewFile()
                File("$this/expect-version.txt").apply {
                    createNewFile()
                    writeText(expectVersion)
                }
            },
            test = ::testSemVer,
        )

    @Test
    fun `clean v1_0_0`() =
        testSandbox(
            sandboxPath = "version-build-dir/clean v1_0_0",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()
            },
            test = ::testSemVer,
        )
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
