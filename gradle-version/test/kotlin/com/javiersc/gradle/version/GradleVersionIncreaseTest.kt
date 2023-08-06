package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.Increase.Major
import com.javiersc.gradle.version.GradleVersion.Increase.Minor
import com.javiersc.gradle.version.GradleVersion.Increase.Patch
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionIncreaseTest {
    @Test
    fun increase_stage() {
        GradleVersion("1.0.0").inc(stageName = "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("1.0.0-alpha.1")
            .inc(stageName = "alpha")
            .shouldBe(GradleVersion("1.0.0-alpha.2"))
        GradleVersion("1.0.0-dev.1").inc(stageName = "dev").shouldBe(GradleVersion("1.0.0-dev.2"))
        GradleVersion("1.0.0-alpha.1").inc().shouldBe(GradleVersion("1.0.0-alpha.2"))
        GradleVersion("1.0.0-alpha.1")
            .inc(stageName = "beta")
            .shouldBe(GradleVersion("1.0.0-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(stageName = "rc").shouldBe(GradleVersion("1.1.0-rc.1"))
        GradleVersion("1.1.0").inc(stageName = "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0").inc(stageName = "SNAPSHOT").shouldBe(GradleVersion("1.1.1-SNAPSHOT"))
        GradleVersion("1.1.0").inc(stageName = "snapshot").shouldBe(GradleVersion("1.1.1-SNAPSHOT"))
        shouldThrow<GradleVersionException> {
            GradleVersion("1.1.0-SNAPSHOT").inc(stageName = "tatata")
        }
        shouldThrow<GradleVersionException> {
            GradleVersion("1.1.0-SNAPSHOT").inc(stageName = "rc")
        }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.0-rc.1").inc(stageName = "beta") }
        shouldThrow<GradleVersionException> {
            GradleVersion("1.1.0-beta.1").inc(stageName = "alpha")
        }
    }

    @Test
    fun increase_stage_and_patch() {
        GradleVersion("1.0.0").inc(Patch, "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Patch, "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Patch, "").shouldBe(GradleVersion("1.0.1"))
        GradleVersion("1.0.0-alpha.1").inc(Patch, "beta").shouldBe(GradleVersion("1.0.1-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(Patch, "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0").inc(Patch, "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Patch, "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Patch).shouldBe(GradleVersion("1.1.1"))
        GradleVersion("1.1.0-beta.1").inc(Patch, "alpha").shouldBe(GradleVersion("1.1.1-alpha.1"))
        GradleVersion("1.0.0").inc(Patch, "snapshot").shouldBe(GradleVersion("1.0.1-SNAPSHOT"))
        GradleVersion("1.0.0").inc(Patch, "SNAPSHOT").shouldBe(GradleVersion("1.0.1-SNAPSHOT"))
    }

    @Test
    fun increase_stage_and_minor() {
        GradleVersion("1.0.0").inc(Minor, "alpha").shouldBe(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Minor, "alpha").shouldBe(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Minor, "").shouldBe(GradleVersion("1.1.0"))
        GradleVersion("1.0.0-alpha.1").inc(Minor, "beta").shouldBe(GradleVersion("1.1.0-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(Minor, "rc").shouldBe(GradleVersion("1.2.0-rc.1"))
        GradleVersion("1.1.0").inc(Minor, "rc").shouldBe(GradleVersion("1.2.0-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Minor, "rc").shouldBe(GradleVersion("1.2.0-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Minor).shouldBe(GradleVersion("1.2.0"))
        GradleVersion("1.1.0-beta.1").inc(Minor, "alpha").shouldBe(GradleVersion("1.2.0-alpha.1"))
        GradleVersion("1.0.0").inc(Minor, "snapshot").shouldBe(GradleVersion("1.1.0-SNAPSHOT"))
        GradleVersion("1.0.0").inc(Minor, "SNAPSHOT").shouldBe(GradleVersion("1.1.0-SNAPSHOT"))
    }

    @Test
    fun increase_stage_and_major() {
        GradleVersion("1.0.0").inc(Major, "alpha").shouldBe(GradleVersion("2.0.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Major, "alpha").shouldBe(GradleVersion("2.0.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Major, "").shouldBe(GradleVersion("2.0.0"))
        GradleVersion("1.0.0-alpha.1").inc(Major, "beta").shouldBe(GradleVersion("2.0.0-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(Major, "rc").shouldBe(GradleVersion("2.0.0-rc.1"))
        GradleVersion("1.1.0").inc(Major, "rc").shouldBe(GradleVersion("2.0.0-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Major, "rc").shouldBe(GradleVersion("2.0.0-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Major).shouldBe(GradleVersion("2.0.0"))
        GradleVersion("1.1.0-beta.1").inc(Major, "alpha").shouldBe(GradleVersion("2.0.0-alpha.1"))
        GradleVersion("1.0.0").inc(Major, "snapshot").shouldBe(GradleVersion("2.0.0-SNAPSHOT"))
        GradleVersion("1.0.0").inc(Major, "SNAPSHOT").shouldBe(GradleVersion("2.0.0-SNAPSHOT"))
    }

    @Test
    fun increase_patch() {
        GradleVersion("1.0.0").inc(Patch).shouldBe(GradleVersion("1.0.1"))
        GradleVersion("1.1.0").inc(Patch).shouldBe(GradleVersion("1.1.1"))
        GradleVersion("1.1.1").inc(Patch).shouldBe(GradleVersion("1.1.2"))
        GradleVersion("1.1.1-alpha.1").inc(Patch).shouldBe(GradleVersion("1.1.2"))
        GradleVersion("1.1.1-beta.1").inc(Patch).shouldBe(GradleVersion("1.1.2"))
        GradleVersion("0.1.0-beta.1").inc(Patch).shouldBe(GradleVersion("0.1.1"))
        GradleVersion("10.1.0-rc.3").inc(Patch).shouldBe(GradleVersion("10.1.1"))
        GradleVersion("1.0.0-SNAPSHOT").inc(Patch).shouldBe(GradleVersion("1.0.1"))
        GradleVersion("1.0.19-beta.5")
            .inc(Patch, stageName = "beta")
            .shouldBe(GradleVersion("1.0.20-beta.1"))
        GradleVersion("1.0.19").inc().shouldBe(GradleVersion("1.0.20"))
        GradleVersion("1.0.19-beta.5").inc().shouldBe(GradleVersion("1.0.19-beta.6"))
        GradleVersion("1.0.19").inc(number = null, stageName = "").shouldBe(GradleVersion("1.0.20"))
        GradleVersion("1.0.19-beta.5")
            .inc(number = null, stageName = "")
            .shouldBe(GradleVersion("1.0.19-beta.6"))
    }

    @Test
    fun increase_minor() {
        GradleVersion("1.0.0").inc(Minor).shouldBe(GradleVersion("1.1.0"))
        GradleVersion("1.1.0").inc(Minor).shouldBe(GradleVersion("1.2.0"))
        GradleVersion("1.1.1").inc(Minor).shouldBe(GradleVersion("1.2.0"))
        GradleVersion("1.1.1-alpha.1").inc(Minor).shouldBe(GradleVersion("1.2.0"))
        GradleVersion("1.1.1-beta.1").inc(Minor).shouldBe(GradleVersion("1.2.0"))
        GradleVersion("0.1.0-beta.1").inc(Minor).shouldBe(GradleVersion("0.2.0"))
        GradleVersion("10.1.0-rc.3").inc(Minor).shouldBe(GradleVersion("10.2.0"))
        GradleVersion("1.0.0-SNAPSHOT").inc(Minor).shouldBe(GradleVersion("1.1.0"))
    }

    @Test
    fun increase_major() {
        GradleVersion("1.0.0").inc(Major).shouldBe(GradleVersion("2.0.0"))
        GradleVersion("1.1.0").inc(Major).shouldBe(GradleVersion("2.0.0"))
        GradleVersion("1.1.1").inc(Major).shouldBe(GradleVersion("2.0.0"))
        GradleVersion("1.1.1-alpha.1").inc(Major).shouldBe(GradleVersion("2.0.0"))
        GradleVersion("1.1.1-beta.1").inc(Major).shouldBe(GradleVersion("2.0.0"))
        GradleVersion("0.1.0-beta.1").inc(Major).shouldBe(GradleVersion("1.0.0"))
        GradleVersion("10.1.0-rc.3").inc(Major).shouldBe(GradleVersion("11.0.0"))
        GradleVersion("1.0.0-SNAPSHOT").inc(Major).shouldBe(GradleVersion("2.0.0"))
    }
}
