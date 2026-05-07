@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.SemverExtension
import com.javiersc.semver.project.gradle.plugin.SemverProjectPlugin
import com.javiersc.semver.project.gradle.plugin.VersionMapper
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.ProjectFeatureApplicationContext
import org.gradle.features.binding.ProjectTypeApplyAction
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

public abstract class SemverApplyAction :
    ProjectTypeApplyAction<SemverDefinition, BuildModel.None> {

    @get:Inject public abstract val project: Project
    @get:Inject public abstract val pluginManager: PluginManager

    override fun apply(
        context: ProjectFeatureApplicationContext,
        definition: SemverDefinition,
        buildModel: BuildModel.None,
    ) {
        val enabled: Provider<Boolean> = definition.enabled.orElse(true)

        if (enabled.get()) {
            val defaultGitDir: Directory = project.layout.settingsDirectory.dir(".git")

            val gitDir: Provider<Directory> = definition.gitDir.orElse(defaultGitDir)
            val commitsMaxCount: Provider<Int> =
                definition.commitsMaxCount.orElse(DefaultCommitsMaxCount)
            val tagPrefix: Provider<String> = definition.tagPrefix.orElse(DefaultTagPrefix)

            pluginManager.apply(SemverProjectPlugin::class)

            project.configure<SemverExtension> {
                this.isEnabled.set(enabled)
                this.gitDir.set(gitDir)
                this.commitsMaxCount.set(commitsMaxCount)
                this.tagPrefix.set(tagPrefix)
                val overrideVersion: String? = definition.overrideVersion.orNull
                val versionMapping: SemverMapVersionDefinition = definition.mapVersion
                val hasMapping = hasAnyMapping(versionMapping)
                if (overrideVersion != null || hasMapping) {
                    this.mapVersion(createVersionMapper(overrideVersion, versionMapping))
                }
            }
        }
    }

    private fun hasAnyMapping(mapping: SemverMapVersionDefinition): Boolean =
        mapping.major.isPresent ||
            mapping.minor.isPresent ||
            mapping.patch.isPresent ||
            mapping.stageName.isPresent ||
            mapping.stageNum.isPresent ||
            mapping.commits.isPresent ||
            mapping.hash.isPresent ||
            mapping.metadata.isPresent

    private fun createVersionMapper(
        overrideVersion: String?,
        mapping: SemverMapVersionDefinition,
    ): VersionMapper {
        val major: Int? = mapping.major.orNull
        val minor: Int? = mapping.minor.orNull
        val patch: Int? = mapping.patch.orNull
        val stageName: String? = mapping.stageName.orNull
        val stageNum: Int? = mapping.stageNum.orNull
        val commits: Int? = mapping.commits.orNull
        val hash: String? = mapping.hash.orNull
        val metadata: String? = mapping.metadata.orNull

        return FullVersionMapper(
            overrideVersion,
            major,
            minor,
            patch,
            stageName,
            stageNum,
            commits,
            hash,
            metadata,
        )
    }

    private class FullVersionMapper(
        private val versionOverride: String?,
        private val major: Int?,
        private val minor: Int?,
        private val patch: Int?,
        private val stageName: String?,
        private val stageNum: Int?,
        private val commits: Int?,
        private val hash: String?,
        private val metadata: String?,
    ) : VersionMapper {
        override fun map(version: GradleVersion): String {
            val baseVersion: GradleVersion =
                if (versionOverride != null) {
                    GradleVersion(versionOverride)
                } else {
                    version
                }

            return baseVersion
                .copy(
                    major = major ?: baseVersion.major,
                    minor = minor ?: baseVersion.minor,
                    patch = patch ?: baseVersion.patch,
                    stageName = stageName ?: baseVersion.stage?.name,
                    stageNum = stageNum ?: baseVersion.stage?.num,
                    commits = commits ?: baseVersion.commits,
                    hash = hash ?: baseVersion.hash,
                    metadata = metadata ?: baseVersion.metadata,
                )
                .toString()
        }
    }

    public companion object {
        public const val NAME: String = "semver"

        internal const val DefaultCommitsMaxCount = -1
        internal const val DefaultTagPrefix = ""
    }
}
