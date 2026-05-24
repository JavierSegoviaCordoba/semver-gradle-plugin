package com.javiersc.semver.shared.services

import com.javiersc.semver.shared.internal.commitsMaxCount
import com.javiersc.semver.shared.internal.git.GitCache
import com.javiersc.semver.shared.internal.git.commitsInCurrentBranchRevCommit
import com.javiersc.semver.shared.internal.remoteProperty
import com.javiersc.semver.shared.internal.semverMessage
import com.javiersc.semver.shared.internal.semverWarningMessage
import java.io.File
import javax.inject.Inject
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.transport.RemoteConfig
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.process.ExecOperations

public abstract class GitBuildService
@Inject
constructor(private val execOperations: ExecOperations) :
    BuildService<GitBuildService.Params>, AutoCloseable {

    private val createdTagPrefixes: MutableSet<String> = mutableSetOf()

    private val git: Git?
        get() = parameters.run {
            val gitDir: File? = gitDir.orNull?.asFile?.takeIf { it.exists() }
            if (gitDir == null) {
                semverWarningMessage("There is no git directory")
                return null
            }
            GitCache(gitDir = gitDir, maxCount = commitsMaxCount).git
        }

    internal fun createTag(tagPrefixProperty: String, projectTagPrefix: String, version: String) {
        val git: Git = git ?: return
        if (tagPrefixProperty !in createdTagPrefixes && projectTagPrefix == tagPrefixProperty) {
            createdTagPrefixes.add(tagPrefixProperty)

            check(git.status().call().isClean) {
                "A semver tag can't be created if the repo is not clean"
            }

            val semverWithTagPrefix = "$projectTagPrefix$version"
            git.tag().setName(semverWithTagPrefix).call()
            semverMessage("Created new semver tag: $semverWithTagPrefix")
        }
    }

    internal fun pushTag(tagPrefixProperty: String, projectTagPrefix: String, version: String) {
        val git: Git = git ?: return
        if (tagPrefixProperty !in createdTagPrefixes && projectTagPrefix == tagPrefixProperty) {
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
                exec.commandLine("git", "push", remote, semverWithTagPrefix)
            }
        }
    }

    internal interface Params : BuildServiceParameters {
        val gitDir: DirectoryProperty
        val commitsMaxCount: Property<Int>
        val remoteProperty: Property<String>
    }

    override fun close() {
        createdTagPrefixes.clear()
    }

    public companion object {

        public fun register(
            project: Project,
            gitDir: Provider<out Directory>,
            commitsMaxCount: Provider<Int>,
        ): Provider<GitBuildService> =
            project.gradle.sharedServices.registerIfAbsent(
                "gitTagBuildService",
                GitBuildService::class,
            ) { buildService ->
                val maxCount: Provider<Int> = project.commitsMaxCount.orElse(commitsMaxCount)
                buildService.parameters.gitDir.set(gitDir)
                buildService.parameters.commitsMaxCount.set(maxCount)
                buildService.parameters.remoteProperty.set(project.remoteProperty)

                buildService.maxParallelUsages.set(1)
            }
    }
}

internal fun Git.hasCommits(): Boolean =
    try {
        commitsInCurrentBranchRevCommit.isNotEmpty()
    } catch (_: NoHeadException) {
        false
    }

internal fun Git.hasNotCommits(): Boolean = !hasCommits()

private val Git.remotes: List<String>
    get() = remoteList().call().map(RemoteConfig::getName)
