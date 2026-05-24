package com.javiersc.semver.shared

import com.javiersc.gradle.version.GradleVersion
import java.io.Serializable

public fun interface VersionMapper : Serializable {

    public fun map(version: GradleVersion): String
}
