package com.javiersc.semver.gradle.plugin.internal.git

import java.io.File
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project

internal val Project.gitDir: File
    get() = file("${rootProject.rootDir}/.git")

internal val Project.git: Git
    get() = gitDir.git

internal val File.git: Git
    get() = Git(FileRepositoryBuilder().setGitDir(this).readEnvironment().findGitDir().build())

internal val Project.hasGit: Boolean
    get() = gitDir.exists()

internal val Project.hasCommits: Boolean
    get() =
        hasGit &&
            try {
                git.commitsInCurrentBranchRevCommit.isNotEmpty()
            } catch (exception: NoHeadException) {
                false
            }
