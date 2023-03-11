package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.kotlin.stdlib.AnsiColor
import com.javiersc.kotlin.stdlib.ansiColor
import java.io.File
import kotlin.test.Test

internal class PropertiesTest : GradleTestKitTest() {

    @Test
    fun empty() {
        runPropertyTestsBasedOnResourceDirectory("empty") {
            resolve("empty.txt").createNewFile()
            git.add().addFilepattern(".").call()
            if (name.endsWith("hash")) git.commit().setMessage("Add empty.txt").call()
        }
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

    private fun runPropertyTestsBasedOnResourceDirectory(
        resourcePath: String,
        beforeTest: File.() -> Unit = {}
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
