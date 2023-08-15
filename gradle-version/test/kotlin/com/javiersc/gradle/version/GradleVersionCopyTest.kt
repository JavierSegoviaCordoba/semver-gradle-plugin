package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.Scope
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionCopyTest {

    @Test
    fun copy_major() {
        GradleVersion("1.0.0").copy(major = 3).shouldBe(GradleVersion("3.0.0"))
        GradleVersion("1.0.1").copy(major = 3).shouldBe(GradleVersion("3.0.1"))
        GradleVersion("1.1.0").copy(major = 3).shouldBe(GradleVersion("3.1.0"))
        GradleVersion("1.1.0-alpha.1").copy(major = 3).shouldBe(GradleVersion("3.1.0-alpha.1"))
    }

    @Test
    fun copy_minor() {
        GradleVersion("1.0.0").copy(minor = 3).shouldBe(GradleVersion("1.3.0"))
        GradleVersion("1.0.1").copy(minor = 3).shouldBe(GradleVersion("1.3.1"))
        GradleVersion("1.1.0").copy(minor = 3).shouldBe(GradleVersion("1.3.0"))
        GradleVersion("1.1.0-alpha.1").copy(minor = 3).shouldBe(GradleVersion("1.3.0-alpha.1"))
    }

    @Test
    fun copy_patch() {
        GradleVersion("1.0.0").copy(patch = 3).shouldBe(GradleVersion("1.0.3"))
        GradleVersion("1.0.1").copy(patch = 3).shouldBe(GradleVersion("1.0.3"))
        GradleVersion("1.1.0").copy(patch = 3).shouldBe(GradleVersion("1.1.3"))
        GradleVersion("1.1.0-alpha.1").copy(patch = 3).shouldBe(GradleVersion("1.1.3-alpha.1"))
    }

    @Test
    fun copy_scope() {
        GradleVersion("1.0.0").copy(scope = Scope(3, 0, 0)).shouldBe(GradleVersion("3.0.0"))
        GradleVersion("1.0.0").copy(scope = Scope(1, 3, 0)).shouldBe(GradleVersion("1.3.0"))
        GradleVersion("1.0.0").copy(scope = Scope(1, 0, 3)).shouldBe(GradleVersion("1.0.3"))
    }

    @Test
    fun copy_stage() {
        GradleVersion("1.0.0")
            .copy(stage = GradleVersion.Stage("alpha", 3))
            .shouldBe(GradleVersion("1.0.0-alpha.3"))
    }

    @Test
    fun copy_stageName() {
        GradleVersion("1.0.0")
            .copy(stageName = "SNAPSHOT")
            .shouldBe(GradleVersion("1.0.0-SNAPSHOT"))
        GradleVersion("1.0.0")
            .copy(stage = GradleVersion.Stage("SNAPSHOT", null))
            .shouldBe(GradleVersion("1.0.0-SNAPSHOT"))
        GradleVersion("1.0.1")
            .copy(stageName = "alpha", stageNum = 3)
            .shouldBe(GradleVersion("1.0.1-alpha.3"))
        GradleVersion("1.0.1")
            .copy(stageName = "SNAPSHOT")
            .shouldBe(GradleVersion("1.0.1-SNAPSHOT"))
        GradleVersion("1.1.0").copy(stageName = "SNAPSHOT") shouldBe GradleVersion("1.1.0-SNAPSHOT")
        GradleVersion("1.1.0-alpha.1")
            .copy(stageName = "SNAPSHOT")
            .shouldBe(GradleVersion("1.1.0-SNAPSHOT"))
    }

    @Test
    fun copy_stageName_stageNum() {
        GradleVersion("1.0.0")
            .copy(stageName = "alpha", stageNum = 3)
            .shouldBe(GradleVersion("1.0.0-alpha.3"))
        GradleVersion("1.1.0")
            .copy(stageName = "alpha", stageNum = 3)
            .shouldBe(GradleVersion("1.1.0-alpha.3"))
        GradleVersion("1.1.0-alpha.1")
            .copy(stageName = "beta", stageNum = 3)
            .shouldBe(GradleVersion("1.1.0-beta.3"))
    }

    @Test
    fun copy_stageNum() {
        GradleVersion("1.1.0-alpha.1").copy(stageNum = 3).shouldBe(GradleVersion("1.1.0-alpha.3"))
    }
}
