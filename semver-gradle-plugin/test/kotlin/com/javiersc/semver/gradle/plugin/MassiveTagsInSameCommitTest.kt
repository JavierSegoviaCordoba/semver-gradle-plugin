package com.javiersc.semver.gradle.plugin

import com.javiersc.gradle.testkit.extensions.gradleTestKitTest
import com.javiersc.semver.gradle.plugin.setup.assertVersion
import com.javiersc.semver.gradle.plugin.setup.createGitIgnore
import com.javiersc.semver.gradle.plugin.setup.git
import kotlin.test.Test
import org.eclipse.jgit.api.Git

internal class MassiveTagsInSameCommitTest {

    @Test
    @Suppress("ComplexMethod")
    fun `massive tags in same commit`() {
        gradleTestKitTest("examples/one-project") {
            Git.init().setDirectory(projectDir).call()

            projectDir.createGitIgnore()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Initial commit").call()

            projectDir.resolve("config.txt").createNewFile()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Add config").call()

            withArguments("createSemverTag", tagPrefixV)

            build()
            projectDir.assertVersion("v", "0.1.0")

            build()
            projectDir.assertVersion("v", "0.1.1")

            build()
            projectDir.assertVersion("v", "0.1.2")

            withArguments("createSemverTag", tagPrefixV, scope("minor"))

            build()
            projectDir.assertVersion("v", "0.2.0")

            build()
            projectDir.assertVersion("v", "0.3.0")

            build()
            projectDir.assertVersion("v", "0.4.0")

            withArguments("createSemverTag", tagPrefixV, scope("major"))

            build()
            projectDir.assertVersion("v", "1.0.0")

            build()
            projectDir.assertVersion("v", "2.0.0")

            build()
            projectDir.assertVersion("v", "3.0.0")

            withArguments("createSemverTag", tagPrefixV, stage("alpha"))

            build()
            projectDir.assertVersion("v", "3.0.1-alpha.1")

            build()
            projectDir.assertVersion("v", "3.0.1-alpha.2")

            build()
            projectDir.assertVersion("v", "3.0.1-alpha.3")

            withArguments("createSemverTag", tagPrefixV, stage("beta"))

            build()
            projectDir.assertVersion("v", "3.0.1-beta.1")

            build()
            projectDir.assertVersion("v", "3.0.1-beta.2")

            build()
            projectDir.assertVersion("v", "3.0.1-beta.3")

            withArguments("createSemverTag", tagPrefixV, stage("final"))

            build()
            projectDir.assertVersion("v", "3.0.1")

            build()
            projectDir.assertVersion("v", "3.0.2")

            build()
            projectDir.assertVersion("v", "3.0.3")

            withArguments("createSemverTag", tagPrefixV, stage("final"), scope("minor"))

            build()
            projectDir.assertVersion("v", "3.1.0")

            build()
            projectDir.assertVersion("v", "3.2.0")

            build()
            projectDir.assertVersion("v", "3.3.0")

            withArguments("createSemverTag", tagPrefixV, stage("final"), scope("major"))

            build()
            projectDir.assertVersion("v", "4.0.0")

            build()
            projectDir.assertVersion("v", "5.0.0")

            build()
            projectDir.assertVersion("v", "6.0.0")
        }
    }

    private val tagPrefixV = "-Psemver.tagPrefix=v"
    private fun stage(stage: String) = "-Psemver.stage=$stage"
    private fun scope(scope: String) = "-Psemver.scope=$scope"
}
