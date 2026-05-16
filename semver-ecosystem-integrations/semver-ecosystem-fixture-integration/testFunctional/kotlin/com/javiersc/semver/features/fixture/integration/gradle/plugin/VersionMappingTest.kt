package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.shared.assertVersionFromExpectVersionFiles
import com.javiersc.semver.shared.generateInitialCommitAddVersionTag
import com.javiersc.semver.shared.generateInitialCommitAddVersionTagAndAddNewCommit
import com.javiersc.semver.shared.git
import com.javiersc.semver.shared.internal.git.headRevCommitInBranch
import kotlin.test.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class VersionMappingTest : GradleTestKitTest() {

    @Test
    fun `0_2_0+2_0_10`() {
        gradleTestKitTest("version-mapping/v0_2_0+2_0_10") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `v1_0_0 to v1_0_0+1_9_0`() {
        gradleTestKitTest("version-mapping/v1_0_0 to v1_0_0+1_9_0") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `string override`() {
        gradleTestKitTest("version-mapping/string override") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `gradle property mapping`() {
        gradleTestKitTest("version-mapping/gradle property mapping") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @Test
    fun `rules priority and override`() {
        gradleTestKitTest("version-mapping/rules priority and override") {
            projectDir.generateInitialCommitAddVersionTag()

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    @ParameterizedTest
    @MethodSource("ruleMappingCases")
    fun `rules mapping conditions`(case: RuleMappingCase) {
        gradleTestKitTest("version-mapping/rules/${case.path}") {
            withEnvironment(case.environment)
            if (case.addNewCommit) {
                projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            } else {
                projectDir.generateInitialCommitAddVersionTag()
            }

            withArgumentsFromTXT()
            build()
            projectDir.assertVersionFromExpectVersionFiles()
        }
    }

    private companion object {

        @JvmStatic
        fun ruleMappingCases(): List<RuleMappingCase> =
            listOf(
                RuleMappingCase("all conditions"),
                RuleMappingCase("any conditions"),
                RuleMappingCase("none conditions"),
                RuleMappingCase("metadata is absent"),
                RuleMappingCase("requested tag prefix"),
                RuleMappingCase("requested tag prefix absent"),
                RuleMappingCase("contains"),
                RuleMappingCase("ends with"),
                RuleMappingCase("pattern"),
                RuleMappingCase("starts with"),
                RuleMappingCase("ignore case"),
                RuleMappingCase("combined conditions"),
                RuleMappingCase("mapped metadata is present"),
                RuleMappingCase("mapped metadata is absent"),
                RuleMappingCase("mapped fields are present"),
                RuleMappingCase("mapped values"),
                RuleMappingCase("mapped kotlin dev metadata"),
                RuleMappingCase("property is present"),
                RuleMappingCase("property is absent"),
                RuleMappingCase(
                    path = "environment variable is present",
                    environment = mapOf("SEMVER_TEST_MAPPED_ENV" to "true"),
                ),
                RuleMappingCase("environment variable is absent"),
            )
    }

    data class RuleMappingCase(
        val path: String,
        val addNewCommit: Boolean = false,
        val environment: Map<String, String> = emptyMap(),
    ) {
        override fun toString(): String = path
    }
}
