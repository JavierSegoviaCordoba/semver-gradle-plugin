@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.features.annotations.BindsProjectType
import org.gradle.features.binding.ProjectTypeBinding
import org.gradle.features.binding.ProjectTypeBindingBuilder
import org.gradle.features.dsl.bindProjectType

@BindsProjectType(SemverProjectType::class)
public open class SemverProjectType : Plugin<Project>, ProjectTypeBinding {

    override fun apply(target: Project) {
        // NO-OP
    }

    override fun bind(builder: ProjectTypeBindingBuilder) {
        builder
            .bindProjectType(SemverApplyAction.NAME, SemverApplyAction::class)
            .withUnsafeDefinition()
            .withUnsafeApplyAction()
    }
}
