@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.hubdle.integration.gradle.plugin

import com.javiersc.semver.features.plugin.api.SemverDefinition
import com.javiersc.semver.shared.SemverConfig
import com.javiersc.semver.shared.VersionMapper
import com.javiersc.semver.shared.configureSemver
import hubdle.ecosystem.feature.versioning.HubdleVersioningBuildModel
import hubdle.ecosystem.feature.versioning.HubdleVersioningDefinition
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.ProjectFeatureApplicationContext
import org.gradle.features.binding.ProjectFeatureApplyAction

public abstract class SemverEcosystemHubdleIntegrationApplyAction :
    ProjectFeatureApplyAction<SemverDefinition, BuildModel.None, HubdleVersioningDefinition> {

    @get:Inject public abstract val project: Project

    override fun apply(
        context: ProjectFeatureApplicationContext,
        definition: SemverDefinition,
        buildModel: BuildModel.None,
        parentDefinition: HubdleVersioningDefinition,
    ) {
        val parentBuildModel: HubdleVersioningBuildModel = context.getBuildModel(parentDefinition)
        val declaredEnabled: Provider<Boolean> = definition.enabled.orElse(true)
        val enabled: Provider<Boolean> =
            parentBuildModel.effectiveEnabled.zip(declaredEnabled) {
                parentEffectiveEnabled,
                semverEnabled ->
                parentEffectiveEnabled && semverEnabled
            }

        if (enabled.get()) {
            val defaultGitDir: Directory = project.layout.settingsDirectory.dir(".git")
            val gitDir: Provider<out Directory> = definition.gitDir.orElse(defaultGitDir)
            val commitsMaxCount: Provider<Int> =
                definition.commitsMaxCount.orElse(DefaultCommitsMaxCount)
            val tagPrefix: Provider<String> = definition.tagPrefix.orElse(DefaultTagPrefix)
            val versionMapper: Provider<VersionMapper> = project.provider {
                VersionMapper { version -> version.toString() }
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

    public companion object {
        public const val DefaultCommitsMaxCount: Int = -1
        public const val DefaultTagPrefix: String = ""
    }
}
