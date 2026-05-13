@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.semver.ecosystem.fixture.gradle.plugin.SemverEcosystemFixtureDefinition
import com.javiersc.semver.features.plugin.api.SemverDefinition
import com.javiersc.semver.features.plugin.api.toVersionMapper
import com.javiersc.semver.shared.SemverConfig
import com.javiersc.semver.shared.configureSemver
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.features.binding.BuildModel
import org.gradle.features.binding.ProjectFeatureApplicationContext
import org.gradle.features.binding.ProjectFeatureApplyAction

public abstract class SemverEcosystemFixtureIntegrationApplyAction :
    ProjectFeatureApplyAction<SemverDefinition, BuildModel.None, SemverEcosystemFixtureDefinition> {

    @get:Inject public abstract val project: Project

    override fun apply(
        context: ProjectFeatureApplicationContext,
        definition: SemverDefinition,
        buildModel: BuildModel.None,
        parentDefinition: SemverEcosystemFixtureDefinition,
    ) {
        val enabled: Provider<Boolean> = definition.enabled.orElse(true)

        if (enabled.get()) {
            val defaultGitDir: Directory = project.layout.settingsDirectory.dir(".git")

            project.configureSemver(
                SemverConfig(
                    enabled = enabled,
                    gitDir = definition.gitDir.orElse(defaultGitDir),
                    commitsMaxCount = definition.commitsMaxCount.orElse(DefaultCommitsMaxCount),
                    tagPrefix = definition.tagPrefix.orElse(DefaultTagPrefix),
                    versionMapper = project.providers.provider { definition.toVersionMapper() },
                )
            )
        }
    }

    public companion object {
        public const val DefaultCommitsMaxCount: Int = -1
        public const val DefaultTagPrefix: String = ""
    }
}
