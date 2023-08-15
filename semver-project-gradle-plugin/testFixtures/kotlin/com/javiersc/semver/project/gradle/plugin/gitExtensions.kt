package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.internal.AdditionalVersionData
import com.javiersc.semver.project.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.lastVersionCommitInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.versionTagsInCurrentBranch
import java.io.File
import java.time.Instant
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit

// `this` is `projectDir`
internal fun File.generateInitialCommitAddVersionTagAndAddNewCommit(
    doBefore: Git.() -> Unit = {},
    doAfter: Git.() -> Unit = {},
): List<Commit> {
    createGitIgnore()
    val git: Git = Git.init().setDirectory(this).call()
    doBefore(git)
    git.add().addFilepattern(".").call()
    val commit = git.commit().setMessage("Initial commit").call()
    resolve("new.txt").createNewFile()
    git.tag().setObjectId(commit).setName(resolve("last-tag.txt").readLines().first()).call()
    git.add().addFilepattern(".").call()
    git.commit().setMessage("Add new").call()
    resolve("library/last-tag.txt").apply {
        if (exists()) git.tag().setObjectId(commit).setName(readLines().first()).call()
    }
    resolve("new2.txt").createNewFile()
    git.add().addFilepattern(".").call()
    git.commit().setMessage("Add new2").call()
    doAfter(git)

    return listOf(
        Commit(
            message = "Add new2",
            fullMessage = "Add new2",
            hash = "f099fed42808c387d38f8e34934d4ef2d6f3c2c5",
            timestampEpochSecond = Instant.now().epochSecond,
            tags = emptyList()
        ),
        Commit(
            message = "Add new",
            fullMessage = "Add new",
            hash = "0d9be4d12a836435c91fc38e0d64bcd3c14c89c8",
            timestampEpochSecond = Instant.now().epochSecond,
            tags = emptyList()
        ),
        Commit(
            message = "Initial commit",
            fullMessage = "Initial commit",
            hash = "e2592aafc259bd797e32f1ea4fe60cc7c0698f70",
            timestampEpochSecond = Instant.now().epochSecond,
            tags =
                listOf(
                    Tag(name = "1.0.0", refName = "refs/tags/1.0.0"),
                )
        )
    )
}

internal fun File.createGitIgnore() {
    resolve(".gitignore").apply {
        createNewFile()
        writeText(
            """
                |.idea/
                |build/
                |.gradle/
                |local.properties
                |environment/
                |
            """
                .trimMargin()
        )
    }
}

fun initialCommitAnd(and: File.() -> Unit) {
    val fileName: String =
        Thread.currentThread()
            .stackTrace
            .first { element -> element.methodName.count { char -> char.isWhitespace() } > 0 }
            .methodName

    createSandboxFile(fileName).apply {
        val git: Git = Git.init().setDirectory(this).call()
        resolve("Initial commit.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Initial commit").call()
        and(this)
    }
}

internal fun Git.calculateAdditionalVersionData(
    tagPrefix: String,
    checkIsClean: Boolean = true,
): AdditionalVersionData? =
    calculateAdditionalVersionData(
        clean = status().call().isClean,
        checkClean = checkIsClean,
        lastCommitInCurrentBranch = lastCommitInCurrentBranch?.hash,
        commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
        isThereVersionTags = versionTagsInCurrentBranch(tagPrefix).isNotEmpty(),
        headCommit = headCommit.commit.hash,
        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch(tagPrefix)?.hash,
    )

val File.hash7: String
    get() = git.lastCommitInCurrentBranch!!.hash.take(7)

fun File.tagCall(name: String): Ref = git.tag().setName(name).call()

fun File.commitCall(message: String): RevCommit = git.commit().setMessage(message).call()

fun File.addAllCall() {
    git.add().addFilepattern(".").call()
}

fun File.createNewFile(file: String) = resolve(file).createNewFile()

internal val File.gitCache: GitCache
    get() = GitCache(gitDir = git.repository.directory)
