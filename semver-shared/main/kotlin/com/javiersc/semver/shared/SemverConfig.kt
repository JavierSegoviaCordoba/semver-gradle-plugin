package com.javiersc.semver.shared

import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

public data class SemverConfig(
    public val enabled: Provider<Boolean>,
    public val gitDir: Provider<out Directory>,
    public val commitsMaxCount: Provider<Int>,
    public val tagPrefix: Provider<String>,
    public val versionMapper: Provider<VersionMapper>,
)
