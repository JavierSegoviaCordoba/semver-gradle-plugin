@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.ecosystem.fixture.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.features.annotations.BindsProjectType
import org.gradle.features.binding.ProjectTypeBinding
import org.gradle.features.binding.ProjectTypeBindingBuilder
import org.gradle.features.dsl.bindProjectType

@BindsProjectType(SemverEcosystemFixtureProjectType::class)
public open class SemverEcosystemFixtureProjectType : Plugin<Project>, ProjectTypeBinding {

    override fun apply(target: Project): Unit = Unit

    override fun bind(builder: ProjectTypeBindingBuilder): Unit {
        builder
            .bindProjectType(
                SemverEcosystemFixtureApplyAction.NAME,
                SemverEcosystemFixtureApplyAction::class,
            )
            .withUnsafeDefinition()
            .withUnsafeApplyAction()
    }
}
