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
                val semverMapCurrentVersionDefinition: SemverCurrentVersionDefinition? =
                    definition.mapVersion.takeIf { hasAnyMapping(it) }
                        ?: definition.currentVersion.takeIf { hasAnyMapping(it) }
                if (overrideVersion != null || semverMapCurrentVersionDefinition != null) {
                    this.mapVersion(
                        transform =
                            createVersionMapper(
                                overrideVersion = definition.overrideVersion.orNull,
                                mapCurrentVersion = semverMapCurrentVersionDefinition,
                            )
                    )
                }
            }
        }
    }

    private fun hasAnyMapping(mapping: SemverCurrentVersionDefinition): Boolean =
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
        mapCurrentVersion: SemverCurrentVersionDefinition?,
    ): VersionMapper {
        val major: Int? = mapCurrentVersion?.major?.orNull
        val minor: Int? = mapCurrentVersion?.minor?.orNull
        val patch: Int? = mapCurrentVersion?.patch?.orNull
        val stageName: String? = mapCurrentVersion?.stageName?.orNull
        val stageNum: Int? = mapCurrentVersion?.stageNum?.orNull
        val commits: Int? = mapCurrentVersion?.commits?.orNull
        val hash: String? = mapCurrentVersion?.hash?.orNull
        val metadata: String? = mapCurrentVersion?.metadata?.orNull

        return VersionMapper { version ->
            val overrideGradleVersion: GradleVersion? =
                overrideVersion?.let(GradleVersion::safe)?.getOrNull()
            val gradleVersion: GradleVersion =
                overrideGradleVersion.takeIf { it != null } ?: version
            gradleVersion
                .copy(
                    major = major ?: gradleVersion.major,
                    minor = minor ?: gradleVersion.minor,
                    patch = patch ?: gradleVersion.patch,
                    stageName = stageName ?: gradleVersion.stage?.name,
                    stageNum = stageNum ?: gradleVersion.stage?.num,
                    commits = commits ?: gradleVersion.commits,
                    hash = hash ?: gradleVersion.hash,
                    metadata = metadata ?: gradleVersion.metadata,
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
