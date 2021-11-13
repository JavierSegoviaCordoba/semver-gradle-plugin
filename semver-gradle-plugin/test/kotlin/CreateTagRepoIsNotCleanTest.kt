package com.javiersc.semver.gradle.plugin

import java.io.File
import kotlin.test.Test

class CreateTagRepoIsNotCleanTest {

    @Test
    fun `no clean createSemverTag should fail`() =
        testSandbox(
            sandboxPath = "version-build-dir/no-clean-with-no-tag-current-commit (timestamp)",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()
                File("$this/new-2.txt").createNewFile()
            },
            test = { _, testProjectDir -> testProjectDir.gradlewFailing("createSemverTag") },
        )
}
