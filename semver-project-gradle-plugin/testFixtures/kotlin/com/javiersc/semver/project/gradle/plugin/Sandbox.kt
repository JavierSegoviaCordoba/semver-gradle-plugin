package com.javiersc.semver.project.gradle.plugin

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
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
