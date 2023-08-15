package com.javiersc.gradle.version

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionComparatorTest {

    @Test
    fun same_version() {
        GradleVersion("1.0.0").shouldBe(GradleVersion("1.0.0"))
        GradleVersion("1.1.0").shouldBe(GradleVersion("1.1.0"))
        GradleVersion("1.1.1").shouldBe(GradleVersion("1.1.1"))
        GradleVersion("1.1.0-alpha.1").shouldBe(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.1.1-alpha.1").shouldBe(GradleVersion("1.1.1-alpha.1"))
        GradleVersion("1.1.1-beta.1").shouldBe(GradleVersion("1.1.1-beta.1"))
        GradleVersion("0.1.0-beta.1").shouldBe(GradleVersion("0.1.0-beta.1"))
        GradleVersion("10.1.0-rc.3").shouldBe(GradleVersion("10.1.0-rc.3"))
        GradleVersion("1.0.0-SNAPSHOT").shouldBe(GradleVersion("1.0.0-SNAPSHOT"))
        GradleVersion("2.1.0-SNAPSHOT").shouldBe(GradleVersion("2.1.0-SNAPSHOT"))
        GradleVersion("3.4.2-SNAPSHOT").shouldBe(GradleVersion("3.4.2-SNAPSHOT"))
    }

    @Test
    fun greater_major() {
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.0.0"))
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.0.1"))
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.1.0"))
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.1.1"))
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.2"))
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.2"))
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.1.1-alpha.2"))
        GradleVersion("2.0.0-alpha.2").shouldBeGreaterThan(GradleVersion("1.1.1-alpha.1"))
        GradleVersion("2.0.0-dev.2").shouldBeGreaterThan(GradleVersion("1.1.1-dev.1"))
        GradleVersion("2.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.1.1-alpha.2"))
        GradleVersion("2.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.1.1-beta.2"))
        GradleVersion("2.0.0").shouldBeGreaterThan(GradleVersion("1.0.0-SNAPSHOT"))
        GradleVersion("2.0.0-SNAPSHOT").shouldBeGreaterThan(GradleVersion("1.0.0"))
        GradleVersion("2.0.0-SNAPSHOT").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.1"))
        GradleVersion("2.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.0.0-SNAPSHOT"))
    }

    @Test
    fun greater_minor_with_same_major() {
        GradleVersion("1.1.0").shouldBeGreaterThan(GradleVersion("1.0.1"))
        GradleVersion("1.1.0").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.2"))
        GradleVersion("1.1.0").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.2"))
        GradleVersion("1.1.0-alpha.2").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.1.0-dev.2").shouldBeGreaterThan(GradleVersion("1.1.0-dev.1"))
        GradleVersion("1.1.1-alpha.2").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.1.1-alpha.1").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.2"))
        GradleVersion("1.1.1-alpha.1").shouldBeGreaterThan(GradleVersion("1.1.0-beta.2"))
        GradleVersion("1.1.1-SNAPSHOT").shouldBeGreaterThan(GradleVersion("1.1.0-beta.2"))
    }

    @Test
    fun greater_patch_with_same_major_and_minor() {
        GradleVersion("1.1.1").shouldBeGreaterThan(GradleVersion("1.1.0"))
        GradleVersion("1.1.0").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.2"))
        GradleVersion("1.1.1-alpha.2").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.1.1-dev.2").shouldBeGreaterThan(GradleVersion("1.1.0-dev.1"))
        GradleVersion("1.1.1-alpha.1").shouldBeGreaterThan(GradleVersion("1.1.0-alpha.2"))
        GradleVersion("1.1.1-alpha.1").shouldBeGreaterThan(GradleVersion("1.1.0-beta.2"))
        GradleVersion("1.1.1-SNAPSHOT").shouldBeGreaterThan(GradleVersion("1.1.0-beta.2"))
    }

    @Test
    fun greater_stage_with_same_major_minor_and_patch() {
        GradleVersion("1.0.0-beta.1").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0-beta.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0-foo.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0-rc.1").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.1"))
        GradleVersion("1.0.0-SNAPSHOT").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.1"))
        GradleVersion("1.0.0-SNAPSHOT").shouldBeGreaterThan(GradleVersion("1.0.0-rc.1"))
        GradleVersion("1.0.0").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0").shouldBeGreaterThan(GradleVersion("1.0.0-snapshot"))
        GradleVersion("1.0.0").shouldBeGreaterThan(GradleVersion("1.0.0-SNAPSHOT"))
    }

    @Test
    fun greater_stage_with_same_major_minor_and_patch_special_stages() {
        GradleVersion("1.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.0.0-ALPHA.1"))
        GradleVersion("1.0.0-beta.1").shouldBeGreaterThan(GradleVersion("1.0.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").shouldBeGreaterThan(GradleVersion("1.0.0-BETA.1"))
        GradleVersion("1.0.0-beta.1").shouldBeGreaterThan(GradleVersion("1.0.0-BETA.1"))
        GradleVersion("1.0.0-beta.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0-BETA.1").shouldBeGreaterThan(GradleVersion("1.0.0-dev.1"))
        GradleVersion("1.0.0-beta.1").shouldBeGreaterThan(GradleVersion("1.0.0-DEV.1"))
        GradleVersion("1.0.0-BETA.1").shouldBeGreaterThan(GradleVersion("1.0.0-DEV.1"))
        GradleVersion("1.0.0-dev.1").shouldBe(GradleVersion("1.0.0-DEV.1"))
        GradleVersion("1.0.0-rc.1").shouldBe(GradleVersion("1.0.0-RC.1"))
        GradleVersion("1.0.0-rc.1").shouldBeGreaterThan(GradleVersion("1.0.0-soo.1"))
        GradleVersion("1.0.0-RC.1").shouldBeGreaterThan(GradleVersion("1.0.0-soo.1"))
        GradleVersion("1.0.0-rc.1").shouldBeGreaterThan(GradleVersion("1.0.0-SOO.1"))
        GradleVersion("1.0.0-RC.1").shouldBeGreaterThan(GradleVersion("1.0.0-SOO.1"))
        GradleVersion("1.0.0-SNAPSHOT").shouldBe(GradleVersion("1.0.0-snapshot"))
        GradleVersion("1.0.0-snapshot").shouldBe(GradleVersion("1.0.0-snapshot"))
        GradleVersion("1.0.0-snapshot").shouldBe(GradleVersion("1.0.0-SNAPSHOT"))
        GradleVersion("1.0.0-SNAPSHOT").shouldBe(GradleVersion("1.0.0-SNAPSHOT"))
        GradleVersion("5.9.10-ZASCA.5").shouldBeGreaterThan(GradleVersion("5.9.10-dev.5"))
        GradleVersion("5.9.10-snapshot").shouldBeGreaterThan(GradleVersion("5.9.10-ZASCA.5"))
        GradleVersion("5.9.10-snapshot").shouldBeGreaterThan(GradleVersion("5.9.10-zasca.5"))
        GradleVersion("5.9.10-SNAPSHOT").shouldBeGreaterThan(GradleVersion("5.9.10-ZASCA.5"))
        GradleVersion("5.9.10-SNAPSHOT").shouldBeGreaterThan(GradleVersion("5.9.10-zasca.5"))
        GradleVersion("10.4.2-snapshot").shouldBeGreaterThan(GradleVersion("10.4.2-zasca.5"))
        GradleVersion("1.0.0").shouldBeGreaterThan(GradleVersion("1.0.0-rc.1"))
        GradleVersion("7.3.9-zasca.9+3T4D4T4")
            .shouldBeGreaterThan(GradleVersion("7.3.9-dev.1.6+3T4D4T4"))
    }
}
