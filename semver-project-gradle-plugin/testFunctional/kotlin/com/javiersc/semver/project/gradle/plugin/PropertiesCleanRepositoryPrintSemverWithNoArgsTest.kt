package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin._internal.SemverGradleTestKit
import com.javiersc.semver.project.gradle.plugin._internal.SemverTestData
import com.javiersc.semver.project.gradle.plugin._internal.SemverTestData.Project
import com.javiersc.semver.project.gradle.plugin._internal.generateInitialCommitAddVersionTagAndAddNewCommit
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class PropertiesCleanRepositoryPrintSemverWithNoArgsTest : SemverGradleTestKit() {

    @ParameterizedTest
    @MethodSource(value = ["data"])
    fun `given a clean repository with commits between last tag, when running printSemver with no arguments, then version contains commit hash`(
        testData: SemverTestData
    ) {
        semverGradleTestKit(
            testData = testData,
            initialTestData = {
                val dir = it.projectDir
                dir.generateInitialCommitAddVersionTagAndAddNewCommit(lastTags = this.lastTags)
                dir.resolve("empty.txt").createNewFile()
                dir.git.add().addFilepattern(".").call()
                dir.git.commit().setMessage("Add empty.txt").call()
            }
        )
    }

    companion object {
        @JvmStatic
        fun data(): List<SemverTestData> =
            listOf(
                SemverTestData(
                    arguments = "printSemver -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.0.3+HASH"),
                    subprojects = listOf(Project(tagPrefix = "", expectVersion = "0.1.0.4+HASH")),
                ),
                SemverTestData(
                    arguments = "printSemver",
                    lastTag = "1.0.0",
                    root = Project(tagPrefix = "", expectVersion = "1.0.0.3+HASH"),
                    subprojects = listOf(Project(tagPrefix = "", expectVersion = "1.0.0.3+HASH")),
                )
            )
    }
}
