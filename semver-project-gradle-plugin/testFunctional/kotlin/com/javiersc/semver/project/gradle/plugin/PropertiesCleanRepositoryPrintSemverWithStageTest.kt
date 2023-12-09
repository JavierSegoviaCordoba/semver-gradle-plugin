package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin._internal.SemverGradleTestKit
import com.javiersc.semver.project.gradle.plugin._internal.SemverTestData
import com.javiersc.semver.project.gradle.plugin._internal.SemverTestData.Project
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class PropertiesCleanRepositoryPrintSemverWithStageTest : SemverGradleTestKit() {

    @ParameterizedTest
    @MethodSource(value = ["data"])
    fun `given a clean repository with commits between last tag, when running printSemver with stage, then version is correct`(
        testData: SemverTestData
    ) {
        semverGradleTestKit(testData = testData)
    }

    companion object {

        @JvmStatic
        fun data(): List<SemverTestData> =
            listOf(
                SemverTestData(
                    arguments = "createSemverTag -P semver.stage=auto -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.1"),
                    subprojects = listOf(Project(tagPrefix = "v", expectVersion = "1.0.1")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.stage=auto -P semver.tagPrefix=v",
                    lastTag = "v1.0.0-alpha.1",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.0-alpha.2"),
                    subprojects = listOf(Project(tagPrefix = "v", expectVersion = "1.0.0-alpha.2")),
                ),
                // TODO: this is the first `resources/properties/stage` test
                SemverTestData(
                    arguments = "createSemverTag -P semver.stage=alpha -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.1-alpha.1"),
                    subprojects = listOf(Project(tagPrefix = "v", expectVersion = "1.0.1-alpha.1")),
                ),
            )
    }
}
