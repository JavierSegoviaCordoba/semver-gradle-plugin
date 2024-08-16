package com.javiersc.semver.project.gradle.plugin.valuesources

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.SemverExtension
import com.javiersc.semver.project.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.project.gradle.plugin.internal.checkCleanProperty
import com.javiersc.semver.project.gradle.plugin.internal.checkVersionIsHigherOrSame
import com.javiersc.semver.project.gradle.plugin.internal.commitsMaxCount
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.project.gradle.plugin.internal.git.currentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.lastVersionTagInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.projectTagPrefix
import com.javiersc.semver.project.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.project.gradle.plugin.internal.stageProperty
import com.javiersc.semver.project.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.project.gradle.plugin.semverExtension
import com.javiersc.semver.project.gradle.plugin.tasks.CreateSemverTagTask
import com.javiersc.semver.project.gradle.plugin.tasks.PushSemverTagTask
import java.io.File
import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.kotlin.dsl.of

internal abstract class VersionValueSource : ValueSource<String, VersionValueSource.Params> {

    override fun obtain(): String =
        with(parameters) {
            val isSamePrefix: Boolean = tagPrefixProperty.get() == projectTagPrefix.get()

            fun cache() =
                GitCache(
                    gitDir = parameters.gitDir.get(),
                    maxCount = parameters.commitsMaxCount,
                )

            val lastSemver: GradleVersion =
                cache().lastVersionInCurrentBranch(projectTagPrefix.get())
            val lastVersionInCurrentBranch =
                cache().versionsInCurrentBranch(projectTagPrefix.get()).map(GradleVersion::toString)

            val lastVersionCommitInCurrentBranch =
                cache().lastVersionCommitInCurrentBranch(projectTagPrefix.get())?.hash

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
                    clean = cache().isClean,
                    checkClean = checkClean.get(),
                    lastCommitInCurrentBranch = cache().lastCommitInCurrentBranch?.hash,
                    commitsInCurrentBranch =
                        cache().commitsInCurrentBranch.map(GitRef.Commit::hash),
                    headCommit = cache().headCommit.commit.hash,
                    lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch,
                )

            val gradleVersion = GradleVersion(version)
            val mappedGradleVersion: GradleVersion =
                versionMapper.get().map(gradleVersion).let(GradleVersion::invoke)

            val gitData: SemverExtension.GitData = cache().git.buildGitData(tagPrefixProperty)
            val mappedVersionAndGit: String =
                versionAndGitMapper.get().map(mappedGradleVersion, gitData)

            checkVersionIsHigherOrSame(
                version = mappedVersionAndGit,
                lastVersionInCurrentBranch = lastSemver,
            )

            mappedVersionAndGit
        }

    internal interface Params : ValueSourceParameters {
        val versionMapper: Property<SemverExtension.VersionMapper>
        val versionAndGitMapper: Property<SemverExtension.VersionAndGitMapper>
        val gitDir: Property<File>
        val commitsMaxCount: Property<Int>
        val tagPrefixProperty: Property<String>
        val projectTagPrefix: Property<String>
        val stageProperty: Property<String?>
        val scopeProperty: Property<String?>
        val creatingSemverTag: Property<Boolean>
        val checkClean: Property<Boolean>
    }

    companion object {

        fun register(project: Project): Provider<String> =
            project.providers.of(VersionValueSource::class) { valueSourceSpec ->
                val parameters: Params = valueSourceSpec.parameters
                val semverExtension: SemverExtension = project.semverExtension

                val gitDir: Provider<File> =
                    project.provider { semverExtension.gitDir.get().asFile }

                parameters.versionMapper.set(semverExtension.versionMapper)
                parameters.versionAndGitMapper.set(semverExtension.versionAndGitMapper)
                parameters.gitDir.set(gitDir)
                val commitsMaxCount: Int =
                    project.commitsMaxCount.orNull ?: semverExtension.commitsMaxCount.get()
                parameters.commitsMaxCount.set(commitsMaxCount)
                parameters.projectTagPrefix.set(project.projectTagPrefix.get())
                parameters.tagPrefixProperty.set(project.tagPrefixProperty.get())
                parameters.stageProperty.set(project.stageProperty.orNull)
                parameters.scopeProperty.set(project.scopeProperty.orNull)
                parameters.creatingSemverTag.set(project.isCreatingSemverTag)
                parameters.checkClean.set(project.checkCleanProperty.get())
            }
    }
}

private val Project.isCreatingSemverTag: Boolean
    get() =
        gradle.startParameter.taskNames.any { taskName: String ->
            taskName == CreateSemverTagTask.NAME || taskName == PushSemverTagTask.NAME
        }

private fun Git.buildGitData(tagPrefix: Property<String>): SemverExtension.GitData {
    return SemverExtension.GitData(
        tag =
            lastVersionTagInCurrentBranch(tagPrefix.get())?.let { tag ->
                SemverExtension.GitData.Tag(
                    name = tag.name,
                    refName = tag.refName,
                    commit =
                        SemverExtension.GitData.Commit(
                            message = tag.commit.message,
                            fullMessage = tag.commit.fullMessage,
                            hash = tag.commit.hash,
                        ),
                )
            },
        commit =
            SemverExtension.GitData.Commit(
                message = headCommit.commit.message,
                fullMessage = headCommit.commit.fullMessage,
                hash = headCommit.commit.hash,
            ),
        branch =
            SemverExtension.GitData.Branch(
                name = currentBranch.name,
                refName = currentBranch.refName,
                commits =
                    currentBranch.commits.map { commit ->
                        SemverExtension.GitData.Commit(
                            message = commit.message,
                            fullMessage = commit.fullMessage,
                            hash = commit.hash,
                        )
                    },
                tags =
                    currentBranch.tags.map { tag ->
                        SemverExtension.GitData.Tag(
                            name = tag.name,
                            refName = tag.refName,
                            commit =
                                SemverExtension.GitData.Commit(
                                    message = tag.commit.message,
                                    fullMessage = tag.commit.fullMessage,
                                    hash = tag.commit.hash,
                                ),
                        )
                    },
            ),
    )
}
