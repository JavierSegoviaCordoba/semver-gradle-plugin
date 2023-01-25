package com.javiersc.semver.gradle.plugin

import com.javiersc.gradle.project.test.extensions.GradleProjectTest
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.test.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class SemverIntegrationTest : GradleProjectTest() {

    @Test
    fun `given a project which has no git when it builds then it only register semver extension`() {
        gradleProjectTest {
            pluginManager.apply(SemverPlugin::class)
            extensions.findByName("semver").shouldNotBeNull()
            extensions.findByType<SemverExtension>().shouldNotBeNull()
            tasks.names.shouldNotContain("printSemver")
        }
    }

    @Test
    fun `given a project which has commits when it builds then it only register semver extension`() {
        gradleProjectTest {
            projectDir.resolve("last-tag.txt").apply {
                createNewFile()
                writeText("1.0.0\n")
            }
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
            pluginManager.apply(SemverPlugin::class)
            extensions.findByName("semver").shouldNotBeNull()
            extensions.findByType<SemverExtension>().shouldNotBeNull()
            tasks.names.contains("printSemver")
        }
    }
}
