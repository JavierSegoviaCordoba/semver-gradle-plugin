package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.kotlin.stdlib.AnsiColor
import com.javiersc.kotlin.stdlib.ansiColor
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.io.File
import kotlin.test.Test

internal class PropertiesTest : GradleTestKitTest() {

    @Test
    fun empty() {
        runPropertyTestsBasedOnResourceDirectory(
            resourcePath = "empty",
            beforeTest = {
                resolve("empty.txt").createNewFile()
                git.add().addFilepattern(".").call()
                if (name.endsWith("hash")) git.commit().setMessage("Add empty.txt").call()
            },
        )
    }

    @Test
    fun scope() {
        runPropertyTestsBasedOnResourceDirectory("scope")
    }

    @Test
    fun stage() {
        runPropertyTestsBasedOnResourceDirectory("stage")
    }

    @Test
    fun `stage and scope`() {
        runPropertyTestsBasedOnResourceDirectory("stage+scope")
    }

    @Test
    fun `log on all projects`() {
        gradleTestKitTest("properties/log-on-all-projects") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            gradlew("printSemver", "-Psemver.logOnlyOnRootProject=false")
                .output
                .shouldContain("semver for sandbox-project: v1.")
                .shouldContain("semver for library-one: v1.")
                .shouldContain("semver for library-two: v1.")
            gradlew("printSemver")
                .output
                .shouldContain("semver for sandbox-project: v1.")
                .shouldContain("semver for library-one: v1.")
                .shouldContain("semver for library-two: v1.")
        }
    }

    @Test
    fun `log only on root project`() {
        gradleTestKitTest("properties/log-only-on-root-project") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            gradlew("printSemver", "-Psemver.logOnlyOnRootProject=true")
                .output
                .shouldContain("semver for sandbox-project: v1.")
                .shouldNotContain("semver for library-one: v1.")
                .shouldNotContain("semver for library-two: v1.")
        }
    }

    private fun runPropertyTestsBasedOnResourceDirectory(
        resourcePath: String,
        beforeTest: File.() -> Unit = {},
    ) {
        val projects =
            getResource("properties/$resourcePath")
                .walkTopDown()
                .mapNotNull { if (it.name == "settings.gradle.kts") it.parentFile else null }
                .map { it.relativeTo(getResource("properties").parentFile).path }

        projects.forEach {
            println("\nTesting: $it".ansiColor(AnsiColor.Foreground.Purple))
            gradleTestKitTest(it) {
                projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
                beforeTest(projectDir)
                withArgumentsFromTXT()
                if (projectDir.name.contains("buildAndFail")) {
                    buildAndFail()
                } else {
                    build()
                    projectDir.assertVersionFromExpectVersionFiles()
                }
            }
        }
    }
}
