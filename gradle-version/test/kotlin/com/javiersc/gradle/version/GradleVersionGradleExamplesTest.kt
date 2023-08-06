package com.javiersc.gradle.version

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionGradleExamplesTest {

    @Test
    fun gradle_examples() {
        GradleVersion("1.0.0-BETA.1").shouldBeGreaterThan(GradleVersion("1.0.0-ALPHA.1"))
        GradleVersion("1.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.0.0-BETA.1"))
        GradleVersion("1.0.0-beta.1").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.1"))

        GradleVersion("1.0.0-ALPHA.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0-rc.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))

        GradleVersion("1.0.0-rc.1").shouldBeGreaterThan(GradleVersion("1.0.0-zeta.1"))
        GradleVersion("1.0.0-snapshot").shouldBeGreaterThan(GradleVersion("1.0.0-rc.1"))
        GradleVersion("1.0.0-ga.1").shouldBeGreaterThan(GradleVersion("1.0.0-snapshot"))
        GradleVersion("1.0.0-release.1").shouldBeGreaterThan(GradleVersion("1.0.0-ga.1"))
        GradleVersion("1.0.0-sp.1").shouldBeGreaterThan(GradleVersion("1.0.0-release.1"))
        GradleVersion("1.0.0").shouldBeGreaterThan(GradleVersion("1.0.0-sp.1"))

        GradleVersion("1.0.0-RC.1").shouldBe(GradleVersion("1.0.0-rc.1"))
    }
}
