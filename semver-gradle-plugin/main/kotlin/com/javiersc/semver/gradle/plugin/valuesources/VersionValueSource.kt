package com.javiersc.semver.gradle.plugin.valuesources

import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.checkCleanProperty
import com.javiersc.semver.gradle.plugin.internal.checkVersionIsHigherOrSame
import com.javiersc.semver.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.gradle.plugin.internal.projectTagPrefix
import com.javiersc.semver.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.gradle.plugin.internal.stageProperty
import com.javiersc.semver.gradle.plugin.internal.tagPrefixProperty
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
            project.providers
                .of(VersionValueSource::class) { valueSourceSpec ->
                    val cache = gitTagBuildService.map(GitBuildService::gitCache).get()

                    valueSourceSpec.parameters.gitDir.set(
                        project.objects
                            .directoryProperty()
                            .fileProvider(
                                project.provider {
                                    project.file("${project.rootProject.projectDir}/.git")
                                }
                            )
                    )
                    valueSourceSpec.parameters.projectTagPrefix.set(project.projectTagPrefix)
                    valueSourceSpec.parameters.tagPrefixProperty.set(project.tagPrefixProperty)
                    valueSourceSpec.parameters.stageProperty.set(project.stageProperty)
                    valueSourceSpec.parameters.scopeProperty.set(project.scopeProperty)
                    valueSourceSpec.parameters.creatingSemverTag.set(project.isCreatingSemverTag)
                    valueSourceSpec.parameters.checkClean.set(project.checkCleanProperty)
                    valueSourceSpec.parameters.commitsInCurrentBranch.set(
                        cache.commitsInCurrentBranch.map(GitRef.Commit::hash)
                    )
                    valueSourceSpec.parameters.headCommit.set(cache.headCommit.commit.hash)
                    valueSourceSpec.parameters.lastVersionCommitInCurrentBranch.set(
                        cache.lastVersionCommitInCurrentBranch(project.projectTagPrefix)?.hash
                    )
                }
                .forUseAtConfigurationTime()
    }
}

private val Project.isCreatingSemverTag: Boolean
    get() =
        gradle.startParameter.taskNames.any { taskName: String ->
            taskName == CreateSemverTagTask.taskName || taskName == PushSemverTagTask.taskName
        }
