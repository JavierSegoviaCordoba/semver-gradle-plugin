package com.javiersc.semver.features.hubdle.integration.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

internal class SemverEcosystemHubdleIntegrationTest : GradleTestKitTest() {

    @Test
    @Ignore("Enable when Hubdle removes its built-in versioning.semver feature")
    fun semverCanBeConfiguredInsideHubdleEcosystem() {
        gradleTestKitTest("hubdle-ecosystem") {
            withArguments("help", "--stacktrace")

            val result = build()

            assertTrue("Welcome to Gradle" in result.output)
        }
    }
}
