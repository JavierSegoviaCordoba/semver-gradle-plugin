package com.javiersc.semver.ecosystem.fixture.integration.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class SemverEcosystemFixtureIntegrationTest : GradleTestKitTest() {

    @Test
    fun semverCanBeConfiguredInsideEcosystemFixture() {
        gradleTestKitTest("semver-ecosystem-fixture") {
            withArguments("help", "--stacktrace")

            val result = build()

            assertTrue("SemverEcosystemFixtureIntegration applied" in result.output)
        }
    }
}
