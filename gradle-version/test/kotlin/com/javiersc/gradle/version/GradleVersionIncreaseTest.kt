package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Major
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Minor
import com.javiersc.gradle.version.GradleVersion.IncreaseScope.Patch
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionIncreaseTest {

    @Test
    fun increase_null_with_stage() {
        GradleVersion("1.0.0").inc(stageName = "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("3.0.0").inc(stageName = "alpha").shouldBe(GradleVersion("3.0.1-alpha.1"))
        GradleVersion("1.0.0-alpha.1")
            .inc(stageName = "alpha")
            .shouldBe(GradleVersion("1.0.0-alpha.2"))
        GradleVersion("1.0.0-dev.1").inc(stageName = "dev").shouldBe(GradleVersion("1.0.0-dev.2"))
        GradleVersion("1.0.0-alpha.1")
            .inc(stageName = "beta")
            .shouldBe(GradleVersion("1.0.0-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(stageName = "rc").shouldBe(GradleVersion("1.1.0-rc.1"))
        GradleVersion("1.1.0").inc(stageName = "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.0.2-rc.2").inc(stageName = "final").shouldBe(GradleVersion("1.0.2"))
        GradleVersion("1.0.2-dev.2").inc(stageName = "rc").shouldBe(GradleVersion("1.0.2-rc.1"))
        GradleVersion("1.1.0").inc(stageName = "SNAPSHOT").shouldBe(GradleVersion("1.1.1-SNAPSHOT"))
        GradleVersion("1.1.0").inc(stageName = "snapshot").shouldBe(GradleVersion("1.1.1-SNAPSHOT"))
        GradleVersion("1.1.0-SNAPSHOT").inc(stageName = "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0-rc.1").inc(stageName = "beta").shouldBe(GradleVersion("1.1.1-beta.1"))
        GradleVersion("1.1.0-beta.1")
            .inc(stageName = "alpha")
            .shouldBe(GradleVersion("1.1.1-alpha.1"))
        GradleVersion("1.1.0-SNAPSHOT")
            .inc(stageName = "tatata")
            .shouldBe(GradleVersion("1.1.1-tatata.1"))
    }

    @Test
    fun increase_patch_with_stage() {
        GradleVersion("1.0.0-rc.11").inc(Patch, "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("1.0.0").inc(Patch, "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Patch, "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Patch, "alpha").shouldBe(GradleVersion("1.0.1-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Patch, "beta").shouldBe(GradleVersion("1.0.1-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(Patch, "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.0.19-beta.5")
            .inc(Patch, stageName = "beta")
            .shouldBe(GradleVersion("1.0.20-beta.1"))
        GradleVersion("1.1.0").inc(Patch, "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0-rc.3").inc(Patch, "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0-rc.2").inc(Patch, "snapshot").shouldBe(GradleVersion("1.1.1-SNAPSHOT"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Patch, "rc").shouldBe(GradleVersion("1.1.1-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Patch, "final").shouldBe(GradleVersion("1.1.1"))
        GradleVersion("1.1.0-beta.1").inc(Patch, "alpha").shouldBe(GradleVersion("1.1.1-alpha.1"))
        GradleVersion("1.0.0").inc(Patch, "snapshot").shouldBe(GradleVersion("1.0.1-SNAPSHOT"))
        GradleVersion("1.0.0").inc(Patch, "SNAPSHOT").shouldBe(GradleVersion("1.0.1-SNAPSHOT"))
        GradleVersion("1.1.0").inc(Patch, "SNAPSHOT").shouldBe(GradleVersion("1.1.1-SNAPSHOT"))
        GradleVersion("1.1.0").inc(Patch, "snapshot").shouldBe(GradleVersion("1.1.1-SNAPSHOT"))
    }

    @Test
    fun increase_minor_with_stage() {
        GradleVersion("1.0.0").inc(Minor, "alpha").shouldBe(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Minor, "alpha").shouldBe(GradleVersion("1.1.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Minor, "final").shouldBe(GradleVersion("1.1.0"))
        GradleVersion("1.0.0-alpha.1").inc(Minor, "final").shouldBe(GradleVersion("1.1.0"))
        GradleVersion("1.0.0-alpha.1").inc(Minor, "beta").shouldBe(GradleVersion("1.1.0-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(Minor, "rc").shouldBe(GradleVersion("1.2.0-rc.1"))
        GradleVersion("1.1.0").inc(Minor, "rc").shouldBe(GradleVersion("1.2.0-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Minor, "rc").shouldBe(GradleVersion("1.2.0-rc.1"))
        GradleVersion("1.1.0-beta.1").inc(Minor, "alpha").shouldBe(GradleVersion("1.2.0-alpha.1"))
        GradleVersion("1.0.0").inc(Minor, "snapshot").shouldBe(GradleVersion("1.1.0-SNAPSHOT"))
        GradleVersion("1.0.0").inc(Minor, "SNAPSHOT").shouldBe(GradleVersion("1.1.0-SNAPSHOT"))
    }

    @Test
    fun increase_major_with_stage() {
        GradleVersion("1.0.0").inc(Major, "alpha").shouldBe(GradleVersion("2.0.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Major, "alpha").shouldBe(GradleVersion("2.0.0-alpha.1"))
        GradleVersion("1.0.0-alpha.1").inc(Major, "final").shouldBe(GradleVersion("2.0.0"))
        GradleVersion("1.0.0-alpha.1").inc(Major, "beta").shouldBe(GradleVersion("2.0.0-beta.1"))
        GradleVersion("1.1.0-beta.1").inc(Major, "rc").shouldBe(GradleVersion("2.0.0-rc.1"))
        GradleVersion("1.1.0").inc(Major, "rc").shouldBe(GradleVersion("2.0.0-rc.1"))
        GradleVersion("1.1.0-SNAPSHOT").inc(Major, "rc").shouldBe(GradleVersion("2.0.0-rc.1"))
        GradleVersion("1.1.0-beta.1").inc(Major, "alpha").shouldBe(GradleVersion("2.0.0-alpha.1"))
        GradleVersion("1.0.0").inc(Major, "snapshot").shouldBe(GradleVersion("2.0.0-SNAPSHOT"))
        GradleVersion("1.0.0").inc(Major, "SNAPSHOT").shouldBe(GradleVersion("2.0.0-SNAPSHOT"))
    }

    @Test
    fun increase_patch() {
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.0").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1-alpha.1").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1-beta.1").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("0.1.0-beta.1").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("10.1.0-rc.3").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT").inc(Patch) }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-alpha.1").inc(Patch) }
    }

    @Test
    fun increase_minor() {
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.0").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1-alpha.1").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1-beta.1").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("0.1.0-beta.1").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("10.1.0-rc.3").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-alpha.1").inc(Minor) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.0-SNAPSHOT").inc(Minor) }
    }

    @Test
    fun increase_major() {
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.0").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1-alpha.1").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1-beta.1").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("0.1.0-beta.1").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("10.1.0-rc.3").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT").inc(Major) }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.0-SNAPSHOT").inc(Major) }
    }

    @Test
    fun increase_with_no_arguments_or_null_or_empty() {
        shouldThrow<GradleVersionException> { GradleVersion("1.0.19").inc() }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.19-beta.5").inc() }
        shouldThrow<GradleVersionException> {
            GradleVersion("1.0.19").inc(increaseScope = null, stageName = "")
        }
        shouldThrow<GradleVersionException> {
            GradleVersion("1.0.19-beta.5").inc(increaseScope = null, stageName = "")
        }
    }
}
