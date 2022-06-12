package com.javiersc.semver.gradle.plugin

import com.javiersc.gradle.testkit.extensions.gradleTestKitTest
import com.javiersc.gradle.testkit.extensions.withArgumentsFromTXT
import com.javiersc.semver.gradle.plugin.internal.git.headRevCommitInBranch
import com.javiersc.semver.gradle.plugin.setup.assertVersionFromExpectVersionFiles
import com.javiersc.semver.gradle.plugin.setup.generateInitialCommitAddVersionTagAndAddNewCommit
import com.javiersc.semver.gradle.plugin.setup.git
import java.io.File
import kotlin.test.Test

internal class TagsOrderTest {

    @Test
    fun random() {
        gradleTestKitTest("tags-order/random") {
            projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()

            projectDir.createFileAndCommit("alpha.3", "v0.1.0-alpha.3")

            projectDir.createFileAndCommit("random-1", "random-1")

            projectDir.createFileAndCommit("alpha.4", "v0.1.0-alpha.4")
            projectDir.createFileAndCommit("alpha.2", "v0.1.0-alpha.2")
            projectDir.createFileAndCommit("alpha.7", "v0.1.0-alpha.7")
            projectDir.createFileAndCommit("alpha.6", "v0.1.0-alpha.6")
            projectDir.createFileAndCommit("alpha.8", "v0.1.0-alpha.8")
            projectDir.createFileAndCommit("alpha.5", "v0.1.0-alpha.5")
            projectDir.createFileAndCommit("alpha.10", "v0.1.0-alpha.10")
            projectDir.createFileAndCommit("alpha.15", "v0.1.0-alpha.15")
            projectDir.createFileAndCommit("alpha.13", "v0.1.0-alpha.13")
            projectDir.createFileAndCommit("alpha.11", "v0.1.0-alpha.11")
            projectDir.createFileAndCommit("alpha.14", "v0.1.0-alpha.14")
            projectDir.createFileAndCommit("alpha.12", "v0.1.0-alpha.12")
            projectDir.createFileAndCommit("alpha.17", "v0.1.0-alpha.17")
            projectDir.createFileAndCommit("alpha.19", "v0.1.0-alpha.19")
            projectDir.createFileAndCommit("alpha.18", "v0.1.0-alpha.18")
            projectDir.createFileAndCommit("alpha.16", "v0.1.0-alpha.16")
            projectDir.createFileAndCommit("alpha.22", "v0.1.0-alpha.22")
            projectDir.createFileAndCommit("alpha.21", "v0.1.0-alpha.21")
            projectDir.createFileAndCommit("alpha.20", "v0.1.0-alpha.20")

            projectDir.createFileAndCommit("beta.1", "v0.1.0-beta.1")

            projectDir.createFileAndCommit("random-2", "random-2")

            projectDir.createFileAndCommit("beta.3", "v0.1.0-beta.3")
            projectDir.createFileAndCommit("beta.4", "v0.1.0-beta.4")
            projectDir.createFileAndCommit("beta.2", "v0.1.0-beta.2")
            projectDir.createFileAndCommit("beta.7", "v0.1.0-beta.7")
            projectDir.createFileAndCommit("beta.6", "v0.1.0-beta.6")
            projectDir.createFileAndCommit("beta.8", "v0.1.0-beta.8")
            projectDir.createFileAndCommit("beta.5", "v0.1.0-beta.5")
            projectDir.createFileAndCommit("beta.10", "v0.1.0-beta.10")
            projectDir.createFileAndCommit("beta.15", "v0.1.0-beta.15")
            projectDir.createFileAndCommit("beta.13", "v0.1.0-beta.13")
            projectDir.createFileAndCommit("beta.11", "v0.1.0-beta.11")
            projectDir.createFileAndCommit("beta.14", "v0.1.0-beta.14")
            projectDir.createFileAndCommit("beta.12", "v0.1.0-beta.12")

            projectDir.createFileAndCommit("rc.1", "v0.1.0-rc.1")
            projectDir.createFileAndCommit("rc.3", "v0.1.0-rc.3")
            projectDir.createFileAndCommit("rc.4", "v0.1.0-rc.4")
            projectDir.createFileAndCommit("rc.2", "v0.1.0-rc.2")
            projectDir.createFileAndCommit("rc.7", "v0.1.0-rc.7")
            projectDir.createFileAndCommit("rc.6", "v0.1.0-rc.6")

            projectDir.createFileAndCommit("random-3", "random-3")

            projectDir.createFileAndCommit("rc.8", "v0.1.0-rc.8")
            projectDir.createFileAndCommit("rc.5", "v0.1.0-rc.5")
            projectDir.createFileAndCommit("rc.10", "v0.1.0-rc.10")
            projectDir.createFileAndCommit("rc.15", "v0.1.0-rc.15")
            projectDir.createFileAndCommit("rc.11", "v0.1.0-rc.11")
            projectDir.createFileAndCommit("rc.14", "v0.1.0-rc.14")
            projectDir.createFileAndCommit("rc.12", "v0.1.0-rc.12")

            projectDir.createFileAndCommit("random-4", "random-4")

            projectDir.resolve("rc.13 2").createNewFile()

            git.add().addFilepattern(".").call()
            git.commit().setMessage("Add rc.13 2").call()

            projectDir.createFileAndCommit("random-5", "random-5")

            withArgumentsFromTXT()
            build()

            projectDir.assertVersionFromExpectVersionFiles()
        }
    }
}

private fun File.createFileAndCommit(name: String, version: String) {
    File("$this/$name").createNewFile()
    git.add().addFilepattern(".").call()
    git.commit().setMessage("Add $name").call()
    git.tag().setObjectId(git.headRevCommitInBranch).setName(version).call()
}
