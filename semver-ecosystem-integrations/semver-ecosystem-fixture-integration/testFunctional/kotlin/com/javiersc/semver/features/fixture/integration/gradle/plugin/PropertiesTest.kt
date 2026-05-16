package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.shared.generateInitialCommitAddVersionTagAndAddNewCommit
import com.javiersc.semver.shared.git
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlin.test.Test
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.AggregateWith
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.ArgumentsAggregator
import org.junit.jupiter.params.provider.CsvFileSource

internal class PropertiesTest : GradleTestKitTest() {

    @ParameterizedTest
    @CsvFileSource(resources = ["/tables/properties.csv"], numLinesToSkip = 1)
    fun properties(@AggregateWith(PropertyCase.Aggregator::class) case: PropertyCase) {
        gradleTestKitTest("tables/generated-dcl-project") {
            projectDir.writeDclProject(
                rootTagPrefix = case.rootTagPrefix,
                libraryTagPrefix = case.libraryTagPrefix,
                rootLastTag = case.lastTag,
                libraryLastTag = case.libraryLastTag,
                includeLibrary = case.includeLibrary,
                gradlePropertiesTagPrefix = case.gradlePropertiesTagPrefix,
            )
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            when (case.dirtyMode) {
                "committed" -> {
                    projectDir.resolve("empty.txt").createNewFile()
                    git.add().addFilepattern(".").call()
                    git.commit().setMessage("Add empty.txt").call()
                }
                "staged" -> {
                    projectDir.resolve("empty.txt").createNewFile()
                    git.add().addFilepattern(".").call()
                }
            }

            withArguments(*case.arguments.toTypedArray())
            if (case.shouldFail) {
                buildAndFail()
            } else {
                build()
                projectDir.assertGeneratedVersion(case.expectedRoot, case.expectedRootTag)
                projectDir
                    .resolve("library")
                    .assertGeneratedVersion(case.expectedLibrary, case.expectedLibraryTag)
            }
        }
    }

    @Test
    fun `log-on-all-projects`() {
        gradleTestKitTest("properties/log-on-all-projects") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            gradlew("printSemver", "-Psemver.logOnlyOnRootProject=false")
                .output
                .shouldContain("semver for sandbox-project: v1.")
                .shouldContain("semver for library-one: v1.")
                .shouldContain("semver for library-two: v1.")
            gradlew("printSemver")
                .output
                .shouldContain("semver for sandbox-project: v1.")
                .shouldContain("semver for library-one: v1.")
                .shouldContain("semver for library-two: v1.")
        }
    }

    @Test
    fun `log-only-on-root-project`() {
        gradleTestKitTest("properties/log-only-on-root-project") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            gradlew("printSemver", "-Psemver.logOnlyOnRootProject=true")
                .output
                .shouldContain("semver for sandbox-project: v1.")
                .shouldNotContain("semver for library-one: v1.")
                .shouldNotContain("semver for library-two: v1.")
        }
    }

    data class PropertyCase(
        val name: String,
        val lastTag: String,
        val libraryLastTag: String?,
        val rootTagPrefix: String?,
        val libraryTagPrefix: String?,
        val task: String,
        val scopeProperty: String?,
        val stageProperty: String?,
        val tagPrefixProperty: String?,
        val gradlePropertiesTagPrefix: String?,
        val expectedRoot: String?,
        val expectedRootTag: String?,
        val expectedLibrary: String?,
        val expectedLibraryTag: String?,
        val shouldFail: Boolean,
        val dirtyMode: String?,
        val includeLibrary: Boolean,
    ) {
        val arguments: List<String>
            get() = buildList {
                add(task)
                scopeProperty?.let { add("-Psemver.scope=$it") }
                stageProperty?.let { add("-Psemver.stage=$it") }
                tagPrefixProperty?.let { add("-Psemver.tagPrefix=$it") }
            }

        override fun toString(): String = name

        object Aggregator : ArgumentsAggregator {
            override fun aggregateArguments(
                accessor: ArgumentsAccessor,
                context: ParameterContext,
            ): PropertyCase =
                PropertyCase(
                    name = accessor.getString(0),
                    lastTag = accessor.getString(1),
                    libraryLastTag = accessor.stringOrNull(2),
                    rootTagPrefix = accessor.stringOrNull(3),
                    libraryTagPrefix = accessor.stringOrNull(4),
                    task = accessor.getString(5),
                    scopeProperty = accessor.stringOrNull(6),
                    stageProperty = accessor.stringOrNull(7),
                    tagPrefixProperty = accessor.stringOrNull(8),
                    gradlePropertiesTagPrefix = accessor.stringOrNull(9),
                    expectedRoot = accessor.stringOrNull(10),
                    expectedRootTag = accessor.stringOrNull(11),
                    expectedLibrary = accessor.stringOrNull(12),
                    expectedLibraryTag = accessor.stringOrNull(13),
                    shouldFail = accessor.boolean(14),
                    dirtyMode = accessor.stringOrNull(15),
                    includeLibrary = accessor.boolean(16),
                )
        }
    }
}
