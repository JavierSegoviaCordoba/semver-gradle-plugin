package com.javiersc.semver.project.gradle.plugin

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.testkit.runner.GradleRunner

internal val sandboxPath: Path = Paths.get("build/sandbox").apply { toFile().mkdirs() }

internal val GradleRunner.git: Git
    get() =
        Git(
            FileRepositoryBuilder()
                .setGitDir(File("$projectDir/.git"))
                .readEnvironment()
                .findGitDir()
                .build()
        )

val File.git: Git
    get() =
        Git(
            FileRepositoryBuilder()
                .setGitDir(resolve(".git"))
                .readEnvironment()
                .findGitDir()
                .build()
        )

internal fun getResource(resource: String): File =
    File(Thread.currentThread().contextClassLoader?.getResource(resource)?.toURI()!!)

internal fun createSandboxFile(prefix: String): File =
    Files.createTempDirectory(sandboxPath, "$prefix - ").toFile()

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
