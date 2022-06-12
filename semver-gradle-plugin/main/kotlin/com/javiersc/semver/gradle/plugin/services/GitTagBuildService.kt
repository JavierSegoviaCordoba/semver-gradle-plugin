package com.javiersc.semver.gradle.plugin.services

import com.javiersc.semver.gradle.plugin.internal.git.git
import com.javiersc.semver.gradle.plugin.internal.git.gitDir
import com.javiersc.semver.gradle.plugin.internal.remoteProperty
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import javax.inject.Inject
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RemoteConfig
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.process.ExecOperations

public abstract class GitTagBuildService
@Inject
constructor(
    private val execOperations: ExecOperations,
) : BuildService<GitTagBuildService.Params>, AutoCloseable {

    private var isCreatingTag: Boolean = false

    private var isPushingTag: Boolean = false

    private val git: Git
        get() =
            checkNotNull(parameters.gitDirectory.orNull?.asFile?.git) {
                "Semver Gradle plugin can't work if git is not configured"
            }

    public fun createTag(tagPrefixProperty: String, projectTagPrefix: String, version: String) {
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
        if (!isPushingTag && tagPrefixProperty == projectTagPrefix) {
            execOperations.exec { exec ->
                val remoteProp: String? = parameters.remoteProperty.orNull

                val remote: String =
                    when {
                        remoteProp != null -> {
                            checkNotNull(git.remotes.firstOrNull { it == remoteProp }) {
                                "There is no remote with the name $remoteProp"
                            }
                        }
                        git.remotes.contains("origin") -> "origin"
                        else -> git.remotes.first()
                    }

                val semverWithTagPrefix = "$projectTagPrefix$version"
                val tag: String =
                    checkNotNull(semverWithTagPrefix) {
                        "The tag provided is not semantic, so it must not be pushed"
                    }

                exec.commandLine("git", "push", remote, tag)
            }
        }
    }

    public interface Params : BuildServiceParameters {
        public val gitDirectory: RegularFileProperty
        public val remoteProperty: Property<String>
    }

    override fun close() {
        isCreatingTag = false
        isPushingTag = false
    }

    public companion object {

        internal fun register(project: Project): Provider<GitTagBuildService> =
            project.gradle.sharedServices.registerIfAbsent(
                "gitTagBuildService",
                GitTagBuildService::class
            ) { service ->
                service.parameters.gitDirectory.set(project.gitDir)
                service.parameters.remoteProperty.set(project.remoteProperty)

                service.maxParallelUsages.set(1)
            }
    }
}

private val Git.remotes: List<String>
    get() = remoteList().call().map(RemoteConfig::getName)
