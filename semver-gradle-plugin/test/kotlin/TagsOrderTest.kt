package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.headRevCommitInBranch
import java.io.File
import kotlin.test.Test

class TagsOrderTest {

    @Test
    fun random() =
        testSandbox(
            sandboxPath = "tags-order/random",
            beforeTest = {
                generateInitialCommitAddVersionTagAndAddNewCommit()

                createFileAndCommit("alpha.3", "v0.1.0-alpha.3")

                createFileAndCommit("random-1", "random-1")

                createFileAndCommit("alpha.4", "v0.1.0-alpha.4")
                createFileAndCommit("alpha.2", "v0.1.0-alpha.2")
                createFileAndCommit("alpha.7", "v0.1.0-alpha.7")
                createFileAndCommit("alpha.6", "v0.1.0-alpha.6")
                createFileAndCommit("alpha.8", "v0.1.0-alpha.8")
                createFileAndCommit("alpha.5", "v0.1.0-alpha.5")
                createFileAndCommit("alpha.10", "v0.1.0-alpha.10")
                createFileAndCommit("alpha.15", "v0.1.0-alpha.15")
                createFileAndCommit("alpha.13", "v0.1.0-alpha.13")
                createFileAndCommit("alpha.11", "v0.1.0-alpha.11")
                createFileAndCommit("alpha.14", "v0.1.0-alpha.14")
                createFileAndCommit("alpha.12", "v0.1.0-alpha.12")
                createFileAndCommit("alpha.17", "v0.1.0-alpha.17")
                createFileAndCommit("alpha.19", "v0.1.0-alpha.19")
                createFileAndCommit("alpha.18", "v0.1.0-alpha.18")
                createFileAndCommit("alpha.16", "v0.1.0-alpha.16")
                createFileAndCommit("alpha.22", "v0.1.0-alpha.22")
                createFileAndCommit("alpha.21", "v0.1.0-alpha.21")
                createFileAndCommit("alpha.20", "v0.1.0-alpha.20")

                createFileAndCommit("beta.1", "v0.1.0-beta.1")

                createFileAndCommit("random-2", "random-2")

                createFileAndCommit("beta.3", "v0.1.0-beta.3")
                createFileAndCommit("beta.4", "v0.1.0-beta.4")
                createFileAndCommit("beta.2", "v0.1.0-beta.2")
                createFileAndCommit("beta.7", "v0.1.0-beta.7")
                createFileAndCommit("beta.6", "v0.1.0-beta.6")
                createFileAndCommit("beta.8", "v0.1.0-beta.8")
                createFileAndCommit("beta.5", "v0.1.0-beta.5")
                createFileAndCommit("beta.10", "v0.1.0-beta.10")
                createFileAndCommit("beta.15", "v0.1.0-beta.15")
                createFileAndCommit("beta.13", "v0.1.0-beta.13")
                createFileAndCommit("beta.11", "v0.1.0-beta.11")
                createFileAndCommit("beta.14", "v0.1.0-beta.14")
                createFileAndCommit("beta.12", "v0.1.0-beta.12")

                createFileAndCommit("rc.1", "v0.1.0-rc.1")
                createFileAndCommit("rc.3", "v0.1.0-rc.3")
                createFileAndCommit("rc.4", "v0.1.0-rc.4")
                createFileAndCommit("rc.2", "v0.1.0-rc.2")
                createFileAndCommit("rc.7", "v0.1.0-rc.7")
                createFileAndCommit("rc.6", "v0.1.0-rc.6")

                createFileAndCommit("random-3", "random-3")

                createFileAndCommit("rc.8", "v0.1.0-rc.8")
                createFileAndCommit("rc.5", "v0.1.0-rc.5")
                createFileAndCommit("rc.10", "v0.1.0-rc.10")
                createFileAndCommit("rc.15", "v0.1.0-rc.15")
                createFileAndCommit("rc.11", "v0.1.0-rc.11")
                createFileAndCommit("rc.14", "v0.1.0-rc.14")
                createFileAndCommit("rc.12", "v0.1.0-rc.12")

                createFileAndCommit("random-4", "random-4")

                File("$this/rc.13 2").createNewFile()

                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add rc.13 2").call()

                createFileAndCommit("random-5", "random-5")
            },
            test = ::testSemVer,
        )
}

fun File.createFileAndCommit(name: String, version: String) {
    File("$this/$name").createNewFile()
    git.add().addFilepattern(".").call()
    git.commit().setMessage("Add $name").call()
    git.tag().setObjectId(git.headRevCommitInBranch).setName(version).call()
}
