package com.javiersc.semver.gradle.plugin

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class GradleFeaturesTest {

    @Test
    fun `android build cache clean v1_0_0`() {
        val sandboxPath = "gradle-features/android build cache clean v1_0_0"
        val testProjectDir: File = createSandboxFile(File(sandboxPath).name)
        sandboxPath copyResourceTo testProjectDir

        val runner =
            GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir)
                .withPluginClasspath()

        runner.withArguments("generateDebugBuildConfig").build()

        runner.withArguments("clean").build()

        val result = runner.withArguments("generateDebugBuildConfig").build()

        result
            .task(":generateDebugBuildConfig")
            .shouldNotBeNull()
            .outcome.shouldBe(TaskOutcome.FROM_CACHE)
    }

    @Test
    fun `android configuration cache clean v1_0_0`() {
        val sandboxPath = "gradle-features/android configuration cache clean v1_0_0"
        val testProjectDir: File = createSandboxFile(File(sandboxPath).name)
        sandboxPath copyResourceTo testProjectDir

        val runner =
            GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir)
                .withPluginClasspath()

        runner.withArguments("generateDebugBuildConfig").build()

        val result = runner.withArguments("generateDebugBuildConfig").build()

        result.output.shouldContain("Reusing configuration cache")
        result
            .task(":generateDebugBuildConfig")
            .shouldNotBeNull()
            .outcome.shouldBe(TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `build cache clean v1_0_0`() {
        val sandboxPath = "gradle-features/build cache clean v1_0_0"
        val testProjectDir: File = createSandboxFile(File(sandboxPath).name)
        sandboxPath copyResourceTo testProjectDir

        val runner =
            GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir)
                .withPluginClasspath()

        runner.withArguments("compileKotlin").build()

        runner.withArguments("clean").build()

        val result = runner.withArguments("compileKotlin").build()

        result.task(":compileKotlin").shouldNotBeNull().outcome.shouldBe(TaskOutcome.FROM_CACHE)
    }

    @Test
    fun `configuration cache clean v1_0_0`() {
        val sandboxPath = "gradle-features/configuration cache clean v1_0_0"
        val testProjectDir: File = createSandboxFile(File(sandboxPath).name)
        sandboxPath copyResourceTo testProjectDir

        val runner =
            GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir)
                .withPluginClasspath()

        runner.withArguments("compileKotlin").build()

        val result = runner.withArguments("compileKotlin").build()

        result.output.shouldContain("Reusing configuration cache")
        result.task(":compileKotlin").shouldNotBeNull().outcome.shouldBe(TaskOutcome.UP_TO_DATE)
    }
}
