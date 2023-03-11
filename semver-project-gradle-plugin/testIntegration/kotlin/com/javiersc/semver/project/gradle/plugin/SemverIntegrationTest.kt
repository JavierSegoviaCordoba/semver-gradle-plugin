package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.project.test.extensions.GradleProjectTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import java.time.Instant
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class SemverIntegrationTest : GradleProjectTest() {

    @Test
    fun `given a project which has no git when it builds then it only register semver extension`() {
        gradleProjectTest {
            pluginManager.apply(SemverProjectPlugin::class)
            extensions.findByName("semver").shouldNotBeNull()
            extensions.findByType<SemverExtension>().shouldNotBeNull()
            tasks.names.shouldNotContain("printSemver")
        }
    }

    @Test
    fun `given a project which has commits when it builds then it register semver extension`() {
        val beforeCommitTimestamp: Instant = Instant.now()
        gradleProjectTest {
            projectDir.resolve("last-tag.txt").apply {
                createNewFile()
                writeText("1.0.0\n")
            }
            val expectedCommits: List<com.javiersc.semver.project.gradle.plugin.Commit> =
                projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            pluginManager.apply(SemverProjectPlugin::class)
            extensions.findByName("semver").shouldNotBeNull()
            val semver = extensions.findByType<SemverExtension>().shouldNotBeNull()

            val semverCommits: List<com.javiersc.semver.project.gradle.plugin.Commit> = semver.commits.get()
            semverCommits.shouldHaveSize(expectedCommits.size)
            val afterCommitTimestamp: Instant = Instant.now()
            for ((commit, expectedCommit) in semverCommits.zip(expectedCommits)) {
                commit.hash.shouldNotBeEmpty()
                commit.message.shouldBe(expectedCommit.message)
                commit.fullMessage.shouldBe(expectedCommit.fullMessage)
                val beforeCommitSeconds = beforeCommitTimestamp.epochSecond.seconds - 10.seconds
                val afterCommitSeconds = afterCommitTimestamp.epochSecond.seconds + 10.seconds
                val commitSeconds = commit.timestampEpochSecond.seconds
                beforeCommitSeconds.shouldBeLessThan(commitSeconds)
                afterCommitSeconds.shouldBeGreaterThan(commitSeconds)
                commit.tags.shouldBe(expectedCommit.tags)
            }

            tasks.names.contains("printSemver")
        }
    }
}
