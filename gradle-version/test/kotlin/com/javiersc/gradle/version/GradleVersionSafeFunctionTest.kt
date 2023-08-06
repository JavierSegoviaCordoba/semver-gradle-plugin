package com.javiersc.gradle.version

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionSafeFunctionTest {

    @Test
    fun safe_call() {
        GradleVersion.safe(value = "6.8.3").getOrNull().toString().shouldBe("6.8.3")
        GradleVersion.safe(value = "6.8.3aaaaa").getOrNull().shouldBeNull()
        GradleVersion.safe(major = 6, minor = 8, patch = 3).getOrNull().toString().shouldBe("6.8.3")
        GradleVersion.safe(scope = "6.8.3", stage = "alpha.1")
            .getOrNull()
            .toString()
            .shouldBe("6.8.3-alpha.1")
        GradleVersion.safe(scope = "6.8.3", stage = "alpha.1aaa").getOrNull().shouldBeNull()
        GradleVersion.safe(major = 6, minor = 8, patch = 3, stageName = "alpha", stageNum = 1)
            .getOrNull()
            .toString()
            .shouldBe("6.8.3-alpha.1")
        GradleVersion.safe(major = 6, minor = 8, patch = 3, stageName = "alpha", stageNum = null)
            .getOrNull()
            .shouldBeNull()
    }
}
