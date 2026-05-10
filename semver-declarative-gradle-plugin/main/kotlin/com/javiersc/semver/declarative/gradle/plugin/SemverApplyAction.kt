@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.shared.SemverConfig
import com.javiersc.semver.shared.VersionMapper
import com.javiersc.semver.shared.configureSemver
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.ProjectFeatureApplicationContext
import org.gradle.features.binding.ProjectTypeApplyAction

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
            val versionMapper: Provider<VersionMapper> =
                project.providers.provider {
                    configureVersionMapper(definition)
                        ?: VersionMapper { version -> version.toString() }
                }

            project.configureSemver(
                SemverConfig(
                    enabled = enabled,
                    gitDir = gitDir,
                    commitsMaxCount = commitsMaxCount,
                    tagPrefix = tagPrefix,
                    versionMapper = versionMapper,
                )
            )
        }
    }

    private fun configureVersionMapper(definition: SemverDefinition): VersionMapper? {
        val overrideVersion: String? = definition.overrideVersion.orNull
        val semverMapVersion: SemverVersionDefinition? =
            definition.mapVersion.takeIf(::hasAnyMapping)

        if (overrideVersion == null && semverMapVersion == null) return null

        return VersionMapper(overrideVersion = overrideVersion, definition = semverMapVersion)
    }

    private fun hasAnyMapping(mapping: SemverVersionDefinition): Boolean =
        mapping.major.value.isPresent ||
            mapping.minor.value.isPresent ||
            mapping.patch.value.isPresent ||
            mapping.stage.name.value.isPresent ||
            mapping.stage.number.value.isPresent ||
            mapping.commits.value.isPresent ||
            mapping.hash.value.isPresent ||
            mapping.metadata.value.isPresent

    private fun VersionMapper(
        overrideVersion: String?,
        definition: SemverVersionDefinition?,
    ): VersionMapper {
        val major: Int? by lazy { definition?.major?.value?.orNull }
        val minor: Int? by lazy { definition?.minor?.value?.orNull }
        val patch: Int? by lazy { definition?.patch?.value?.orNull }
        val stageName: String? by lazy { definition?.stage?.name?.value?.orNull }
        val stageNum: Int? by lazy { definition?.stage?.number?.value?.orNull }
        val commits: Int? by lazy { definition?.commits?.value?.orNull }
        val hash: String? by lazy { definition?.hash?.value?.orNull }
        val metadata: String? by lazy { definition?.metadata?.value?.orNull }

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
