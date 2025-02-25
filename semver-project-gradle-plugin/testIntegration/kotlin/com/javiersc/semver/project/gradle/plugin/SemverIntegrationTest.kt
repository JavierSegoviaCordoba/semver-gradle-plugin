package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.project.test.extensions.GradleProjectTest
import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.extensions.isAlpha
import com.javiersc.semver.project.gradle.plugin.extensions.isBeta
import com.javiersc.semver.project.gradle.plugin.extensions.isDev
import com.javiersc.semver.project.gradle.plugin.extensions.isNotAlpha
import com.javiersc.semver.project.gradle.plugin.extensions.isNotBeta
import com.javiersc.semver.project.gradle.plugin.extensions.isNotDev
import com.javiersc.semver.project.gradle.plugin.extensions.isNotRC
import com.javiersc.semver.project.gradle.plugin.extensions.isNotSnapshot
import com.javiersc.semver.project.gradle.plugin.extensions.isRC
import com.javiersc.semver.project.gradle.plugin.extensions.isSnapshot
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.string.shouldStartWith
import java.time.Instant
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class SemverIntegrationTest : GradleProjectTest() {

    @Test
    fun `given a project which has commits when it builds then it register semver extension`() {
        val beforeCommitTimestamp: Instant = Instant.now()
        gradleProjectTest {
            projectDir.resolve("last-tag.txt").apply {
                createNewFile()
                writeText("1.0.0\n")
            }
            val expectedCommits: List<Commit> =
                projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            pluginManager.apply(SemverProjectPlugin::class)
            extensions.findByName("semver").shouldNotBeNull()
            val semver = extensions.findByType<SemverExtension>().shouldNotBeNull()
            semver.mapVersion { version -> version.copy(metadata = "testM3t4d4Ta").toString() }

            val semverCommits: List<Commit> = semver.commits.get()
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
            version.toString().shouldStartWith("1.0.0").shouldEndWith("+testM3t4d4Ta")
            afterEvaluate { proj ->
                extensions.findByType<SemverExtension>().shouldNotBeNull()
                "${proj.version}".shouldStartWith("1.0.0").shouldEndWith("-testM3t4d4Ta")
            }
        }
    }

    @Test
    fun `given a project with some version when it builds then the is extensions work`() {
        gradleProjectTest {
            assignVersion("1.0.0-alpha.1").isAlpha.get().shouldBeTrue()
            assignVersion("1.0.0-alpha.1").isNotAlpha.get().shouldBeFalse()
            assignVersion("1.0.0-alpha.1").isBeta.get().shouldBeFalse()
            assignVersion("1.0.0-alpha.1").isNotBeta.get().shouldBeTrue()
            assignVersion("1.0.0-alpha.1").isDev.get().shouldBeFalse()
            assignVersion("1.0.0-alpha.1").isNotDev.get().shouldBeTrue()
            assignVersion("1.0.0-alpha.1").isRC.get().shouldBeFalse()
            assignVersion("1.0.0-alpha.1").isNotRC.get().shouldBeTrue()
            assignVersion("1.0.0-alpha.1").isSnapshot.get().shouldBeFalse()
            assignVersion("1.0.0-alpha.1").isNotSnapshot.get().shouldBeTrue()

            assignVersion("1.0.0-beta.1").isAlpha.get().shouldBeFalse()
            assignVersion("1.0.0-beta.1").isNotAlpha.get().shouldBeTrue()
            assignVersion("1.0.0-beta.1").isBeta.get().shouldBeTrue()
            assignVersion("1.0.0-beta.1").isNotBeta.get().shouldBeFalse()
            assignVersion("1.0.0-beta.1").isDev.get().shouldBeFalse()
            assignVersion("1.0.0-beta.1").isNotDev.get().shouldBeTrue()
            assignVersion("1.0.0-beta.1").isRC.get().shouldBeFalse()
            assignVersion("1.0.0-beta.1").isNotRC.get().shouldBeTrue()
            assignVersion("1.0.0-beta.1").isSnapshot.get().shouldBeFalse()
            assignVersion("1.0.0-beta.1").isNotSnapshot.get().shouldBeTrue()

            assignVersion("1.0.0-dev.1").isAlpha.get().shouldBeFalse()
            assignVersion("1.0.0-dev.1").isNotAlpha.get().shouldBeTrue()
            assignVersion("1.0.0-dev.1").isBeta.get().shouldBeFalse()
            assignVersion("1.0.0-dev.1").isNotBeta.get().shouldBeTrue()
            assignVersion("1.0.0-dev.1").isDev.get().shouldBeTrue()
            assignVersion("1.0.0-dev.1").isNotDev.get().shouldBeFalse()
            assignVersion("1.0.0-dev.1").isRC.get().shouldBeFalse()
            assignVersion("1.0.0-dev.1").isNotRC.get().shouldBeTrue()
            assignVersion("1.0.0-dev.1").isSnapshot.get().shouldBeFalse()
            assignVersion("1.0.0-dev.1").isNotSnapshot.get().shouldBeTrue()

            assignVersion("1.0.0-rc.1").isAlpha.get().shouldBeFalse()
            assignVersion("1.0.0-rc.1").isNotAlpha.get().shouldBeTrue()
            assignVersion("1.0.0-rc.1").isBeta.get().shouldBeFalse()
            assignVersion("1.0.0-rc.1").isNotBeta.get().shouldBeTrue()
            assignVersion("1.0.0-rc.1").isDev.get().shouldBeFalse()
            assignVersion("1.0.0-rc.1").isNotDev.get().shouldBeTrue()
            assignVersion("1.0.0-rc.1").isRC.get().shouldBeTrue()
            assignVersion("1.0.0-rc.1").isNotRC.get().shouldBeFalse()
            assignVersion("1.0.0-rc.1").isSnapshot.get().shouldBeFalse()
            assignVersion("1.0.0-rc.1").isNotSnapshot.get().shouldBeTrue()

            assignVersion("1.0.0-SNAPSHOT").isAlpha.get().shouldBeFalse()
            assignVersion("1.0.0-SNAPSHOT").isNotAlpha.get().shouldBeTrue()
            assignVersion("1.0.0-SNAPSHOT").isBeta.get().shouldBeFalse()
            assignVersion("1.0.0-SNAPSHOT").isNotBeta.get().shouldBeTrue()
            assignVersion("1.0.0-SNAPSHOT").isDev.get().shouldBeFalse()
            assignVersion("1.0.0-SNAPSHOT").isNotDev.get().shouldBeTrue()
            assignVersion("1.0.0-SNAPSHOT").isRC.get().shouldBeFalse()
            assignVersion("1.0.0-SNAPSHOT").isNotRC.get().shouldBeTrue()
            assignVersion("1.0.0-SNAPSHOT").isSnapshot.get().shouldBeTrue()
            assignVersion("1.0.0-SNAPSHOT").isNotSnapshot.get().shouldBeFalse()
        }
    }

    private fun Project.assignVersion(version: String): Project {
        this.version = GradleVersion(version)
        return this
    }
}
