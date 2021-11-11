package com.javiersc.semver.gradle.plugin

import com.javiersc.semanticVersioning.SemanticVersionException
import com.javiersc.semanticVersioning.Version
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import kotlin.test.Test
import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.GradleRunner

/** TODO: add to docs the Example from README and reference it here */
class MassiveTagsInSameCommitTest {

    private val testProjectDir: File = createSandboxFile("example-one")
    private val git: Git by lazy { Git.init().setDirectory(testProjectDir).call() }

    init {
        "examples/example-one" copyResourceTo testProjectDir
    }

    @Test
    @Suppress("ComplexMethod")
    fun `massive tags in same commit`() {
        testProjectDir.createGitIgnore()

        git.add().addFilepattern(".").call()
        git.commit().setMessage("Initial commit").call()

        File("$testProjectDir/config.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Add config").call()

        gradlew("createSemverTag")
        assertVersion("v", "0.1.0")
        gradlew("createSemverTag")
        assertVersion("v", "0.1.1")
        gradlew("createSemverTag")
        assertVersion("v", "0.1.2")

        gradlew("createSemverTag", scope("minor")).also { assertVersion("v", "0.2.0") }
        gradlew("createSemverTag", scope("minor")).also { assertVersion("v", "0.3.0") }
        gradlew("createSemverTag", scope("minor")).also { assertVersion("v", "0.4.0") }

        gradlew("createSemverTag", scope("major")).also { assertVersion("v", "1.0.0") }
        gradlew("createSemverTag", scope("major")).also { assertVersion("v", "2.0.0") }
        gradlew("createSemverTag", scope("major")).also { assertVersion("v", "3.0.0") }

        gradlew("createSemverTag", stage("alpha")).also { assertVersion("v", "3.0.1-alpha.1") }
        gradlew("createSemverTag", stage("alpha")).also { assertVersion("v", "3.0.1-alpha.2") }
        gradlew("createSemverTag", stage("alpha")).also { assertVersion("v", "3.0.1-alpha.3") }

        gradlew("createSemverTag", stage("beta")).also { assertVersion("v", "3.0.1-beta.1") }
        gradlew("createSemverTag", stage("beta")).also { assertVersion("v", "3.0.1-beta.2") }
        gradlew("createSemverTag", stage("beta")).also { assertVersion("v", "3.0.1-beta.3") }

        gradlew("createSemverTag", stage("final")).also { assertVersion("v", "3.0.1") }
        gradlew("createSemverTag", stage("final")).also { assertVersion("v", "3.0.2") }
        gradlew("createSemverTag", stage("final")).also { assertVersion("v", "3.0.3") }

        gradlew("createSemverTag", stage("final"), scope("minor")).also {
            assertVersion("v", "3.1.0")
        }
        gradlew("createSemverTag", stage("final"), scope("minor")).also {
            assertVersion("v", "3.2.0")
        }
        gradlew("createSemverTag", stage("final"), scope("minor")).also {
            assertVersion("v", "3.3.0")
        }

        gradlew("createSemverTag", stage("final"), scope("major")).also {
            assertVersion("v", "4.0.0")
        }

        gradlew("createSemverTag", stage("final"), scope("major")).also {
            assertVersion("v", "5.0.0")
        }

        gradlew("createSemverTag", stage("final"), scope("major")).also {
            assertVersion("v", "6.0.0")
        }
    }

    private fun stage(stage: String) = "-Psemver.stage=$stage"
    private fun scope(scope: String) = "-Psemver.scope=$scope"

    private fun gradlew(vararg arguments: String) {
        GradleRunner.create()
            .apply {
                withDebug(true)
                withProjectDir(testProjectDir)
                if (arguments.isNotEmpty()) withArguments(arguments.toList())
                withPluginClasspath()
            }
            .build()
    }

    private fun assertVersion(
        prefix: String,
        version: String,
        insignificant: Insignificant? = null
    ) {
        val buildVersionFile = File("$testProjectDir/build/semver/version.txt")
        val buildVersion = buildVersionFile.readLines().first()
        val buildTagVersion = buildVersionFile.readLines()[1]
        when (insignificant) {
            Insignificant.Hash -> {
                buildVersion.startsWith(version).shouldBeTrue()
                buildTagVersion.startsWith("$prefix$version").shouldBeTrue()
                buildVersion.shouldContain("+")
                shouldThrow<SemanticVersionException> { Version(buildVersion) }
            }
            Insignificant.Timestamp -> {
                buildVersion.startsWith(version).shouldBeTrue()
                buildTagVersion.startsWith("$prefix$version").shouldBeTrue()
                buildVersion.shouldContain("+").shouldContain("T")
                shouldThrow<SemanticVersionException> { Version(buildVersion) }
            }
            else -> {
                buildVersionFile
                    .readText()
                    .shouldBe(
                        """
                           |$version
                           |$prefix$version
                           |
                        """.trimMargin()
                    )
            }
        }
    }

    private enum class Insignificant {
        Hash,
        Timestamp
    }
}
