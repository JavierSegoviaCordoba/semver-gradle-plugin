package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin._internal.SemverGradleTestKit
import com.javiersc.semver.project.gradle.plugin._internal.SemverTestData
import com.javiersc.semver.project.gradle.plugin._internal.SemverTestData.Project
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class PropertiesCleanRepositoryPrintSemverWithScopeTest : SemverGradleTestKit() {

    @ParameterizedTest
    @MethodSource(value = ["data"])
    fun `given a clean repository with commits between last tag, when running printSemver with scope, then version is correct`(
        testData: SemverTestData
    ) {
        semverGradleTestKit(testData = testData)
    }

    companion object {

        @JvmStatic
        fun data(): List<SemverTestData> =
            listOf(
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=auto -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.1"),
                    subprojects = listOf(Project(tagPrefix = "v", expectVersion = "1.0.1")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=auto -P semver.tagPrefix=v",
                    lastTag = "v1.0.0-alpha.1",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.0-alpha.2"),
                    subprojects = listOf(Project(tagPrefix = "v", expectVersion = "1.0.0-alpha.2")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=auto -P semver.tagPrefix=v",
                    lastTag = "v1.0.0-beta.5",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.0-beta.6"),
                    subprojects = listOf(Project(tagPrefix = "v", expectVersion = "1.0.0-beta.6")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=auto",
                    lastTag = "1.0.0",
                    root = Project(tagPrefix = "", expectVersion = "1.0.1"),
                    subprojects = listOf(Project(tagPrefix = "", expectVersion = "1.0.1")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=major -P semver.tagPrefix=w",
                    lastTags = listOf("v1.0.0", "w3.0.0"),
                    root = Project(tagPrefix = "v", expectVersion = "1.0.0.2+HASH"),
                    subprojects = listOf(Project(tagPrefix = "w", expectVersion = "4.0.0")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=major -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = "2.0.0"),
                    subprojects = listOf(Project(tagPrefix = "v", expectVersion = "2.0.0")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=major -P semver.tagPrefix=v",
                    lastTags = listOf("v1.0.0", "w3.0.0"),
                    root = Project(tagPrefix = "v", expectVersion = "2.0.0"),
                    subprojects = listOf(Project(tagPrefix = "w", expectVersion = "3.0.0.2+HASH")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=fdfadsaa -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = ""),
                    subprojects = emptyList(),
                    buildAndFail = true,
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=minor -P semver.tagPrefix=w",
                    lastTags = listOf("v1.0.0", "w3.0.0"),
                    root = Project(tagPrefix = "v", expectVersion = "1.0.0.2+HASH"),
                    subprojects = listOf(Project(tagPrefix = "w", expectVersion = "3.1.0")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=minor -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = "1.1.0"),
                    subprojects = emptyList(),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=minor -P semver.tagPrefix=v",
                    lastTags = listOf("v1.0.0", "w3.0.0"),
                    root = Project(tagPrefix = "v", expectVersion = "1.1.0"),
                    subprojects = listOf(Project(tagPrefix = "w", expectVersion = "3.0.0.2+HASH")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=patch -P semver.tagPrefix=w",
                    lastTags = listOf("v1.0.0", "w3.0.0"),
                    root = Project(tagPrefix = "v", expectVersion = "1.0.0.2+HASH"),
                    subprojects = listOf(Project(tagPrefix = "w", expectVersion = "3.0.1")),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=patch -P semver.tagPrefix=v",
                    lastTag = "v1.0.0",
                    root = Project(tagPrefix = "v", expectVersion = "1.0.1"),
                    subprojects = emptyList(),
                ),
                SemverTestData(
                    arguments = "createSemverTag -P semver.scope=patch -P semver.tagPrefix=v",
                    lastTags = listOf("v1.0.0", "w3.0.0"),
                    root = Project(tagPrefix = "v", expectVersion = "1.0.1"),
                    subprojects = listOf(Project(tagPrefix = "w", expectVersion = "3.0.0.2+HASH")),
                ),
            )
    }
}
