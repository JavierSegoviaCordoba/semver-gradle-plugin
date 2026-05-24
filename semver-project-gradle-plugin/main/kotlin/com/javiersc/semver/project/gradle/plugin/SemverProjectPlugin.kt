package com.javiersc.semver.project.gradle.plugin

import com.javiersc.semver.shared.SemverConfig
import com.javiersc.semver.shared.configureSemver
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SemverProjectPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        SemverExtension.register(target)
        target.configureSemver(
            SemverConfig(
                enabled = target.semverExtension.isEnabled,
                gitDir = target.semverExtension.gitDir,
                commitsMaxCount = target.semverExtension.commitsMaxCount,
                tagPrefix = target.semverExtension.tagPrefix,
                versionMapper = target.semverExtension.versionMapper,
            )
        )
    }
}
