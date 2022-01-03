package com.javiersc.semver.gradle.plugin

import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.Purple
import com.javiersc.kotlin.stdlib.ansiColor
import com.javiersc.kotlin.stdlib.remove
import java.io.File
import kotlin.test.Test

class PropertiesTest {

    @Test
    fun `gradle properties`() {
        val projects =
            getResource("properties")
                .walkTopDown()
                .mapNotNull { if (it.name == "settings.gradle.kts") it.parentFile else null }
                .map { it.relativeTo(getResource("properties").parentFile).path }

        projects.forEach {
            println("Testing: ${it.remove("properties${File.separator}")}".ansiColor(Purple))
            testSandbox(
                sandboxPath = it,
                beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
                test = ::testSemVer,
            )
        }
    }
}
