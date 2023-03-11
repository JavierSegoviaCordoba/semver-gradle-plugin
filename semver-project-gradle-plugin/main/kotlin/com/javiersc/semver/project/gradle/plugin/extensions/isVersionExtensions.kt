package com.javiersc.semver.project.gradle.plugin.extensions

import com.javiersc.semver.isAlpha
import com.javiersc.semver.isBeta
import com.javiersc.semver.isDev
import com.javiersc.semver.isNotAlpha
import com.javiersc.semver.isNotBeta
import com.javiersc.semver.isNotDev
import com.javiersc.semver.isNotRC
import com.javiersc.semver.isNotSnapshot
import com.javiersc.semver.isRC
import com.javiersc.semver.isSnapshot
import org.gradle.api.Project
import org.gradle.api.provider.Provider

public val Project.isAlpha: Provider<Boolean>
    get() = provider { version.toString().isAlpha }

public val Project.isNotAlpha: Provider<Boolean>
    get() = provider { version.toString().isNotAlpha }

public val Project.isBeta: Provider<Boolean>
    get() = provider { version.toString().isBeta }

public val Project.isNotBeta: Provider<Boolean>
    get() = provider { version.toString().isNotBeta }

public val Project.isDev: Provider<Boolean>
    get() = provider { version.toString().isDev }

public val Project.isNotDev: Provider<Boolean>
    get() = provider { version.toString().isNotDev }

public val Project.isRC: Provider<Boolean>
    get() = provider { version.toString().isRC }

public val Project.isNotRC: Provider<Boolean>
    get() = provider { version.toString().isNotRC }

public val Project.isSnapshot: Provider<Boolean>
    get() = provider { version.toString().isSnapshot }

public val Project.isNotSnapshot: Provider<Boolean>
    get() = provider { version.toString().isNotSnapshot }
