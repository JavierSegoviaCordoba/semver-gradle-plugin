package com.javiersc.semver.project.gradle.plugin.valuesources

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.project.gradle.plugin.internal.checkCleanProperty
import com.javiersc.semver.project.gradle.plugin.internal.checkVersionIsHigherOrSame
import com.javiersc.semver.project.gradle.plugin.internal.commitsMaxCount
import com.javiersc.semver.project.gradle.plugin.internal.forceProperty
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.project.gradle.plugin.internal.projectTagPrefix
import com.javiersc.semver.project.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.project.gradle.plugin.internal.stageProperty
import com.javiersc.semver.project.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.project.gradle.plugin.semverExtension
import com.javiersc.semver.project.gradle.plugin.tasks.CreateSemverTagTask
import com.javiersc.semver.project.gradle.plugin.tasks.PushSemverTagTask
import java.io.File
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

            fun cache(): GitCache =
                GitCache(
                    gitDir = parameters.gitDir.get(),
                    maxCount = parameters.commitsMaxCount,
                )

            val lastSemver: GradleVersion =
                cache().lastVersionInCurrentBranch(projectTagPrefix.get())

            val lastVersionInCurrentBranch: List<String> =
                cache().versionsInCurrentBranch(projectTagPrefix.get()).map(GradleVersion::toString)

            val lastVersionCommitInCurrentBranch: String? =
                cache().lastVersionCommitInCurrentBranch(projectTagPrefix.get())?.hash

            val version: String =
                calculatedVersion(
                    lastSemver = lastSemver,
                    stageProperty = stageProperty.orNull.takeIf { isSamePrefix },
                    scopeProperty = scopeProperty.orNull.takeIf { isSamePrefix },
                    isCreatingSemverTag = creatingSemverTag.get().takeIf { isSamePrefix } ?: false,
                    versionTagsInBranch = lastVersionInCurrentBranch,
                    clean = cache().isClean,
                    checkClean = checkClean.get(),
                    force = force.get(),
                    lastCommitInCurrentBranch = cache().lastCommitInCurrentBranch?.hash,
                    commitsInCurrentBranch =
                        cache().commitsInCurrentBranch.map(GitRef.Commit::hash),
                    headCommit = cache().headCommit.commit.hash,
                    lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch,
                )

            checkVersionIsHigherOrSame(version, lastSemver)

            version
        }

    internal interface Params : ValueSourceParameters {
        val gitDir: Property<File>
        val commitsMaxCount: Property<Int>
        val tagPrefixProperty: Property<String>
        val projectTagPrefix: Property<String>
        val stageProperty: Property<String?>
        val scopeProperty: Property<String?>
        val creatingSemverTag: Property<Boolean>
        val checkClean: Property<Boolean>
        val force: Property<Boolean>
    }

    companion object {

        fun register(project: Project): Provider<String> =
            project.providers.of(VersionValueSource::class) { valueSourceSpec ->
                val gitDir = project.provider { project.semverExtension.gitDir.get().asFile }
                valueSourceSpec.parameters.gitDir.set(gitDir)
                val commitsMaxCount: Int =
                    project.commitsMaxCount.orNull ?: project.semverExtension.commitsMaxCount.get()
                valueSourceSpec.parameters.commitsMaxCount.set(commitsMaxCount)
                valueSourceSpec.parameters.projectTagPrefix.set(project.projectTagPrefix.get())
                valueSourceSpec.parameters.tagPrefixProperty.set(project.tagPrefixProperty.get())
                valueSourceSpec.parameters.stageProperty.set(project.stageProperty.orNull)
                valueSourceSpec.parameters.scopeProperty.set(project.scopeProperty.orNull)
                valueSourceSpec.parameters.creatingSemverTag.set(project.isCreatingSemverTag)
                valueSourceSpec.parameters.checkClean.set(project.checkCleanProperty.get())
                valueSourceSpec.parameters.force.set(project.forceProperty.get())
            }
    }
}

private val Project.isCreatingSemverTag: Boolean
    get() =
        gradle.startParameter.taskNames.any { taskName: String ->
            taskName == CreateSemverTagTask.NAME || taskName == PushSemverTagTask.NAME
        }
