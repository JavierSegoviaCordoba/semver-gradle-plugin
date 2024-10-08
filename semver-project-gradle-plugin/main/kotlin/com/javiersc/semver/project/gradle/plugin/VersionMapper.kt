package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import java.io.Serializable

public fun interface VersionMapper : Serializable {

    public fun map(version: GradleVersion): String
}
