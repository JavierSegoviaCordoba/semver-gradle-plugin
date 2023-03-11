package com.javiersc.semver.settings.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import kotlin.test.Test

internal class MultiProjectTest : GradleTestKitTest() {

    @Test
    fun `multi project test`() {
        gradleTestKitTest("multi-project") {
            println(gradlew("printSemver", stacktrace()).output)
        }
    }
}
