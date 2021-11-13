package com.javiersc.semver.gradle.plugin

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
            println("Property project: $it")
            testSandbox(
                sandboxPath = it,
                beforeTest = { generateInitialCommitAddVersionTagAndAddNewCommit() },
                test = ::testSemVer,
            )
        }
    }
}
