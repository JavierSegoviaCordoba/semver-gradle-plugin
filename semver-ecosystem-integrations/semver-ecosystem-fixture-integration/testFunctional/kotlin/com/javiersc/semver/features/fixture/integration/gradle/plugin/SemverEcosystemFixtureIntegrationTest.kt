package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class SemverEcosystemFixtureIntegrationTest : GradleTestKitTest() {

    @Test
    fun semverCanBeConfiguredInsideEcosystemFixture() {
        gradleTestKitTest("semver-ecosystem-fixture") {
            withArguments("help", "--stacktrace")

            val result = build()

            assertTrue("Welcome to Gradle" in result.output)
        }
    }
}
