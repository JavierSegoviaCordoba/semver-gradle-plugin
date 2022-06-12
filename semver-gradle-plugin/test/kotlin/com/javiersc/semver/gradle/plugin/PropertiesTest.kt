package com.javiersc.semver.gradle.plugin

import com.javiersc.gradle.testkit.extensions.gradleTestKitTest
import com.javiersc.gradle.testkit.extensions.withArgumentsFromTXT
import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.Purple
import com.javiersc.kotlin.stdlib.ansiColor
import com.javiersc.semver.gradle.plugin.setup.assertVersionFromExpectVersionFiles
import com.javiersc.semver.gradle.plugin.setup.generateInitialCommitAddVersionTagAndAddNewCommit
import com.javiersc.semver.gradle.plugin.setup.getResource
import com.javiersc.semver.gradle.plugin.setup.git
import java.io.File
import kotlin.test.Test

internal class PropertiesTest {

    @Test
    fun empty() {
        runPropertyTestsBasedOnResourceDirectory("empty") {
            resolve("empty.txt").createNewFile()
            git.add().addFilepattern(".")
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
            println("\nTesting: $it".ansiColor(Purple))
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
