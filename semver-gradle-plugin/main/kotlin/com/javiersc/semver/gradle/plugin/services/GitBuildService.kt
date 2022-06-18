package com.javiersc.semver.gradle.plugin.services

import com.javiersc.semver.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.gradle.plugin.internal.git.commitsInCurrentBranchRevCommit
import com.javiersc.semver.gradle.plugin.internal.remoteProperty
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.internal.semverWarningMessage
import java.io.File
import javax.inject.Inject
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.RemoteConfig
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.process.ExecOperations

internal abstract class GitBuildService
@Inject
constructor(
    private val execOperations: ExecOperations,
) : BuildService<GitBuildService.Params>, AutoCloseable {

    private var isCreatingTag: Boolean = false

    private val gitDir: File =
        checkNotNull(parameters.gitDirectory.orNull?.asFile) {
            semverWarningMessage("semver plugin can't work if there is no git repository")
        }

    private val git: Git =
        Git(FileRepositoryBuilder().setGitDir(gitDir).readEnvironment().findGitDir().build()).also {
            if (!it.hasCommits()) {
                semverWarningMessage("semver plugin can't work if there are no commits")
            }
        }

    internal val gitCache: GitCache = GitCache(git)

    internal fun createTag(tagPrefixProperty: String, projectTagPrefix: String, version: String) {
        if (!isCreatingTag && projectTagPrefix == tagPrefixProperty) {
            isCreatingTag = true

            check(git.status().call().isClean) {
                "A semver tag can't be created if the repo is not clean"
            }

            val semverWithTagPrefix = "$projectTagPrefix$version"
            git.tag().setName(semverWithTagPrefix).call()
            semverMessage("Created new semver tag: $semverWithTagPrefix")
        }
    }

    public fun pushTag(tagPrefixProperty: String, projectTagPrefix: String, version: String) {
        if (!isCreatingTag && tagPrefixProperty == projectTagPrefix) {
            createTag(tagPrefixProperty, projectTagPrefix, version)

            execOperations.exec { exec ->
                val remoteProp: String? = parameters.remoteProperty.orNull

                val remote: String? =
                    when {
                        remoteProp != null -> {
                            checkNotNull(git.remotes.firstOrNull { it == remoteProp }) {
                                "There is no remote with the name $remoteProp"
                            }
                        }
                        git.remotes.contains("origin") -> "origin"
                        else -> git.remotes.firstOrNull()
                    }

                checkNotNull(remote) { "There is no remote repositories" }

                val semverWithTagPrefix = "$projectTagPrefix$version"
                val tag: String = semverWithTagPrefix

                exec.commandLine("git", "push", remote, tag)
            }
        }
    }

    internal interface Params : BuildServiceParameters {
        val gitDirectory: RegularFileProperty
        val remoteProperty: Property<String>
    }

    override fun close() {
        isCreatingTag = false
    }

    internal companion object {

        internal fun register(project: Project): Provider<GitBuildService> =
            project.gradle.sharedServices.registerIfAbsent(
                "gitTagBuildService",
                GitBuildService::class
            ) { service ->
                service.parameters.gitDirectory.set(project.gitDir)
                service.parameters.remoteProperty.set(project.remoteProperty)

                service.maxParallelUsages.set(1)
            }
    }
}

private val Project.gitDir: File
    get() = file("${rootProject.projectDir}/.git")

private fun Git.hasCommits(): Boolean =
    try {
        commitsInCurrentBranchRevCommit.isNotEmpty()
    } catch (exception: NoHeadException) {
        false
    }

private val Git.remotes: List<String>
    get() = remoteList().call().map(RemoteConfig::getName)
