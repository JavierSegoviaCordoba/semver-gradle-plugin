package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.SpecialStage

public val GradleVersion.isAlpha: Boolean
    get() = stage?.name?.equals("alpha", ignoreCase = true) ?: false

public val String.isAlpha: Boolean
    get() = contains("alpha", ignoreCase = true)

public val GradleVersion.isNotAlpha: Boolean
    get() = !isAlpha

public val String.isNotAlpha: Boolean
    get() = !isAlpha

public val GradleVersion.isBeta: Boolean
    get() = stage?.name?.equals("beta", ignoreCase = true) ?: false

public val String.isBeta: Boolean
    get() = contains("beta", ignoreCase = true)

public val GradleVersion.isNotBeta: Boolean
    get() = !isBeta

public val String.isNotBeta: Boolean
    get() = !isBeta

public val GradleVersion.isDev: Boolean
    get() = stage?.name?.equals(SpecialStage.dev, ignoreCase = true) ?: false

public val String.isDev: Boolean
    get() = contains(SpecialStage.dev, ignoreCase = true)

public val GradleVersion.isNotDev: Boolean
    get() = !isDev

public val String.isNotDev: Boolean
    get() = !isDev

public val GradleVersion.isFinal: Boolean
    get() =
        (stage?.name?.equals(SpecialStage.final, ignoreCase = true) ?: false ||
            stage?.name.isNullOrBlank())

public val String.isFinal: Boolean
    get() = contains(SpecialStage.final, ignoreCase = true) || isBlank()

public val GradleVersion.isNotFinal: Boolean
    get() = !isFinal

public val String.isNotFinal: Boolean
    get() = !isFinal

public val GradleVersion.isRC: Boolean
    get() = stage?.name?.equals(SpecialStage.rc, ignoreCase = true) ?: false

public val String.isRC: Boolean
    get() = contains(SpecialStage.rc, ignoreCase = true)

public val GradleVersion.isNotRC: Boolean
    get() = !isRC

public val String.isNotRC: Boolean
    get() = !isRC

public val GradleVersion.isSnapshot: Boolean
    get() = stage?.name?.equals(SpecialStage.snapshot, ignoreCase = true) ?: false

public val String.isSnapshot: Boolean
    get() = contains(SpecialStage.snapshot, ignoreCase = true)

public val GradleVersion.isNotSnapshot: Boolean
    get() = !isSnapshot

public val String.isNotSnapshot: Boolean
    get() = !isSnapshot
