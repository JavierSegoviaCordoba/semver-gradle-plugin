package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.shared.generateInitialCommitAddVersionTag
import com.javiersc.semver.shared.generateInitialCommitAddVersionTagAndAddNewCommit
import com.javiersc.semver.shared.git
import com.javiersc.semver.shared.internal.git.headRevCommitInBranch
import kotlin.test.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.AggregateWith
import org.junit.jupiter.params.provider.CsvFileSource

internal class VersionMappingTest : GradleTestKitTest() {

    @ParameterizedTest
    @CsvFileSource(resources = ["/tables/version-mapping.csv"], numLinesToSkip = 1)
    fun `version mapping`(
        @AggregateWith(VersionMappingCase.Aggregator::class) case: VersionMappingCase
    ) {
        gradleTestKitTest("tables/generated-dcl-project") {
            withEnvironment(
                case.environmentName?.let { name: String ->
                    mapOf(name to checkNotNull(case.environmentValue))
                } ?: emptyMap()
            )
            projectDir.writeDclProject(
                rootTagPrefix = case.rootTagPrefix,
                libraryTagPrefix = null,
                rootLastTag = case.lastTag,
                libraryLastTag = null,
                includeLibrary = false,
                gradlePropertiesTagPrefix = null,
                semverBlock = versionMappingBlock(case),
            )

            if (case.setup == "addNewCommitAndTagHeadV1") {
                projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()
            } else {
                projectDir.generateInitialCommitAddVersionTag()
            }

            val arguments = buildList {
                add(case.task)
                case.scopeProperty?.let { scope: String -> add("-Psemver.scope=$scope") }
                case.stageProperty?.let { stage: String -> add("-Psemver.stage=$stage") }
                case.tagPrefixProperty?.let { tagPrefix: String ->
                    add("-Psemver.tagPrefix=$tagPrefix")
                }
                case.gradlePropertyName?.let { name: String ->
                    add("-P$name=${checkNotNull(case.gradlePropertyValue)}")
                }
            }
            withArguments(*arguments.toTypedArray())
            build()
            projectDir.assertGeneratedVersion(case.expectedRoot, case.expectedRootTag)
        }
    }

    @Test
    fun `rules priority and override`() {
        gradleTestKitTest("version-mapping/rules priority and override") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertGeneratedVersion("1.0.0+high", "1.0.0+high")
        }
    }

    @Test
    fun `metadata is absent`() {
        gradleTestKitTest("version-mapping/rules/metadata is absent") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertGeneratedVersion("1.0.0+metadata", "1.0.0+metadata")
        }
    }

    @Test
    fun `requested tag prefix absent`() {
        gradleTestKitTest("version-mapping/rules/requested tag prefix absent") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertGeneratedVersion("1.0.0", "1.0.0")
        }
    }

    @Test
    fun `mapped kotlin dev metadata`() {
        gradleTestKitTest("version-mapping/rules/mapped kotlin dev metadata") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertGeneratedVersion(
                expectedVersion = "1.0.1+2.2.0-dev-123-SNAPSHOT",
                expectedTagVersion = "1.0.1+2.2.0-dev-123-SNAPSHOT",
            )
        }
    }
}
