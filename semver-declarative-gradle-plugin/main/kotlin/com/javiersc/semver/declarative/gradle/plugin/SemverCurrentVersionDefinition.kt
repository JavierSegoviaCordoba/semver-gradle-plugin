@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import org.gradle.api.provider.Property

public interface SemverCurrentVersionDefinition {

    public val major: Property<Int>
    public val minor: Property<Int>
    public val patch: Property<Int>
    public val stageName: Property<String>
    public val stageNum: Property<Int>
    public val commits: Property<Int>
    public val hash: Property<String>
    public val metadata: Property<String>
}
