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
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v0.1.0-rc.6").call()
                File("$this/new-6").createNewFile()
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add new 6").call()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v0.1.0-rc.3").call()
                File("$this/new-3").createNewFile()
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add new 3").call()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v0.1.0-rc.4").call()
                File("$this/new-4").createNewFile()
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add new 4").call()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v0.1.0-rc.7").call()
                File("$this/new-7").createNewFile()
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add new 7").call()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v0.1.0-rc.11").call()
                File("$this/new-11").createNewFile()
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add new 11").call()
                git.tag().setObjectId(git.headRevCommitInBranch).setName("v0.1.0-rc.10").call()
                File("$this/new-10").createNewFile()
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add new 10").call()
                File("$this/new-12").createNewFile()
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Add new 12").call()
            },
            test = ::testSemVer,
        )
}
