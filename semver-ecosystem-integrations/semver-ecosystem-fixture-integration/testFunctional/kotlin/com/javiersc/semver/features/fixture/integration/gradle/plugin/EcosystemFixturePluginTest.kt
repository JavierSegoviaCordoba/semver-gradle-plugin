package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class EcosystemFixturePluginTest : GradleTestKitTest() {

    @Test
    fun `ecosystem-fixture-project-type-can-be-used-from-DCL`() {
        gradleTestKitTest("fixture-plugin") {
            withArguments("help", "--stacktrace")

            val result = build()

            assertTrue("Welcome to Gradle" in result.output)
        }
    }
}
