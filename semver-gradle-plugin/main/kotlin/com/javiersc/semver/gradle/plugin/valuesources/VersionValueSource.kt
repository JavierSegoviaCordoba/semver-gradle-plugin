package com.javiersc.semver.gradle.plugin.valuesources

import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.checkCleanProperty
import com.javiersc.semver.gradle.plugin.internal.checkVersionIsHigherOrSame
import com.javiersc.semver.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.gradle.plugin.internal.stageProperty
import com.javiersc.semver.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.gradle.plugin.semverExtension
import com.javiersc.semver.gradle.plugin.services.GitBuildService
import com.javiersc.semver.gradle.plugin.tasks.CreateSemverTagTask
import com.javiersc.semver.gradle.plugin.tasks.PushSemverTagTask
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.kotlin.dsl.of

public abstract class VersionValueSource : ValueSource<String, VersionValueSource.Params> {

    private val cache =
        GitCache(
            Git(
                FileRepositoryBuilder()
                    .setGitDir(parameters.gitDir.get().asFile)
                    .readEnvironment()
                    .findGitDir()
                    .build()
            )
        )

    override fun obtain(): String =
        with(parameters) {
            val isSamePrefix = tagPrefixProperty.get() == projectTagPrefix.get()

            val lastSemver = cache.lastVersionInCurrentBranch(projectTagPrefix.get())
            val lastVersionInCurrentBranch =
                cache.versionsInCurrentBranch(projectTagPrefix.get()).map(Version::toString)

            val version: String =
                calculatedVersion(
                    stageProperty = stageProperty.orNull.takeIf { isSamePrefix },
                    scopeProperty = scopeProperty.orNull.takeIf { isSamePrefix },
                    isCreatingSemverTag = creatingSemverTag.get().takeIf { isSamePrefix } ?: false,
                    lastSemverMajorInCurrentBranch = lastSemver.major,
                    lastSemverMinorInCurrentBranch = lastSemver.minor,
                    lastSemverPatchInCurrentBranch = lastSemver.patch,
                    lastSemverStageInCurrentBranch = lastSemver.stage?.name,
                    lastSemverNumInCurrentBranch = lastSemver.stage?.num,
                    versionTagsInCurrentBranch = lastVersionInCurrentBranch,
                    clean = cache.isClean,
                    checkClean = checkClean.get(),
                    lastCommitInCurrentBranch = cache.lastCommitInCurrentBranch?.hash,
                    commitsInCurrentBranch = commitsInCurrentBranch.get(),
                    headCommit = headCommit.get(),
                    lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch.orNull,
                )

            checkVersionIsHigherOrSame(version, lastSemver)

            version
        }

    internal interface Params : ValueSourceParameters {
        val gitDir: DirectoryProperty
        val tagPrefixProperty: Property<String>
        val projectTagPrefix: Property<String>
        val stageProperty: Property<String?>
        val scopeProperty: Property<String?>
        val creatingSemverTag: Property<Boolean>
        val checkClean: Property<Boolean>
        val commitsInCurrentBranch: ListProperty<String>
        val headCommit: Property<String>
        val lastVersionCommitInCurrentBranch: Property<String?>
    }

    public companion object {

        internal fun register(
            project: Project,
            gitTagBuildService: Provider<GitBuildService>,
        ): Provider<String> =
            with(project) {
                providers
                    .of(VersionValueSource::class) {
                        val cache = gitTagBuildService.map(GitBuildService::gitCache).get()
                        val projectTagPrefix = semverExtension.tagPrefix.get()

                        it.parameters.gitDir.set(
                            objects
                                .directoryProperty()
                                .fileProvider(provider { file("${rootProject.projectDir}/.git") })
                        )
                        it.parameters.projectTagPrefix.set(projectTagPrefix)
                        it.parameters.tagPrefixProperty.set(tagPrefixProperty)
                        it.parameters.stageProperty.set(stageProperty)
                        it.parameters.scopeProperty.set(scopeProperty)
                        it.parameters.creatingSemverTag.set(isCreatingSemverTag)
                        it.parameters.checkClean.set(checkCleanProperty)
                        it.parameters.commitsInCurrentBranch.set(
                            cache.commitsInCurrentBranch.map(GitRef.Commit::hash)
                        )
                        it.parameters.headCommit.set(cache.headCommit.commit.hash)
                        it.parameters.lastVersionCommitInCurrentBranch.set(
                            cache.lastVersionCommitInCurrentBranch(projectTagPrefix)?.hash
                        )
                    }
                    .forUseAtConfigurationTime()
            }
    }
}

private val Project.isCreatingSemverTag: Boolean
    get() =
        gradle.startParameter.taskNames.any { taskName: String ->
            taskName == CreateSemverTagTask.taskName || taskName == PushSemverTagTask.taskName
        }
