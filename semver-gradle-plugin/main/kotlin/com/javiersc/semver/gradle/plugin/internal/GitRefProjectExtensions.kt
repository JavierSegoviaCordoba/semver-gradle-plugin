package com.javiersc.semver.gradle.plugin.internal

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project

internal val Project.hasGit: Boolean
    get() = file("${rootProject.rootDir}/.git").exists()

internal val Project.hasCommits: Boolean
    get() =
        hasGit &&
            try {
                git.commitsInCurrentBranchRevCommit.isNotEmpty()
            } catch (exception: NoHeadException) {
                false
            }

internal val Project.git: Git
    get() =
        Git(
            FileRepositoryBuilder()
                .setGitDir(file("${rootProject.rootDir}/.git"))
                .readEnvironment()
                .findGitDir()
                .build()
        )
