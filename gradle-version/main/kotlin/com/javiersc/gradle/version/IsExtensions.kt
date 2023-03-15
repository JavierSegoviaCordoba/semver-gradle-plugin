package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.SpecialStage

public val GradleVersion.isAlpha: Boolean
    get() = stage?.name?.lowercase() == "alpha"

public val String.isAlpha: Boolean
    get() = lowercase() == "alpha"

public val GradleVersion.isNotAlpha: Boolean
    get() = !isAlpha

public val String.isNotAlpha: Boolean
    get() = !isAlpha

public val GradleVersion.isBeta: Boolean
    get() = stage?.name?.lowercase() == "beta"

public val String.isBeta: Boolean
    get() = lowercase() == "beta"

public val GradleVersion.isNotBeta: Boolean
    get() = !isBeta

public val String.isNotBeta: Boolean
    get() = !isBeta

public val GradleVersion.isDev: Boolean
    get() = stage?.name?.lowercase() == SpecialStage.dev

public val String.isDev: Boolean
    get() = lowercase() == SpecialStage.dev

public val GradleVersion.isNotDev: Boolean
    get() = !isDev

public val String.isNotDev: Boolean
    get() = !isDev

public val GradleVersion.isRC: Boolean
    get() = stage?.name?.lowercase() == SpecialStage.rc

public val String.isRC: Boolean
    get() = lowercase() == SpecialStage.rc

public val GradleVersion.isNotRC: Boolean
    get() = !isRC

public val String.isNotRC: Boolean
    get() = !isRC

public val GradleVersion.isSnapshot: Boolean
    get() = stage?.name?.lowercase() == SpecialStage.snapshot

public val String.isSnapshot: Boolean
    get() = lowercase() == SpecialStage.snapshot

public val GradleVersion.isNotSnapshot: Boolean
    get() = !isSnapshot

public val String.isNotSnapshot: Boolean
    get() = !isSnapshot
