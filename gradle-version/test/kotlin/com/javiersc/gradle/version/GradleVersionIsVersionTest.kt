package com.javiersc.gradle.version

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test

internal class GradleVersionIsVersionTest {

    @Test
    fun is_version_lowercase() {
        GradleVersion("1.0.0")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
        GradleVersion("1.0.0-random.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-alpha.1")
            .apply {
                isAlpha.shouldBeTrue()
                isNotAlpha.shouldBeFalse()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeTrue()
                isNotAlpha.shouldBeFalse()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-beta.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeTrue()
                isNotBeta.shouldBeFalse()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeTrue()
                isNotBeta.shouldBeFalse()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-dev.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeTrue()
                isNotDev.shouldBeFalse()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeTrue()
                isNotDev.shouldBeFalse()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-rc.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeTrue()
                isNotRC.shouldBeFalse()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeTrue()
                isNotRC.shouldBeFalse()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-snapshot")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeTrue()
                isNotSnapshot.shouldBeFalse()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeTrue()
                isNotSnapshot.shouldBeFalse()
            }
    }

    @Test
    fun is_version_uppercase() {
        GradleVersion("1.0.0")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
        GradleVersion("1.0.0-RANDOM.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-ALPHA.1")
            .apply {
                isAlpha.shouldBeTrue()
                isNotAlpha.shouldBeFalse()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeTrue()
                isNotAlpha.shouldBeFalse()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-BETA.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeTrue()
                isNotBeta.shouldBeFalse()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeTrue()
                isNotBeta.shouldBeFalse()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-DEV.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeTrue()
                isNotDev.shouldBeFalse()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeTrue()
                isNotDev.shouldBeFalse()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-RC.1")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeTrue()
                isNotRC.shouldBeFalse()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeTrue()
                isNotRC.shouldBeFalse()
                isSnapshot.shouldBeFalse()
                isNotSnapshot.shouldBeTrue()
            }

        GradleVersion("1.0.0-SNAPSHOT")
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeTrue()
                isNotSnapshot.shouldBeFalse()
            }
            .toString()
            .apply {
                isAlpha.shouldBeFalse()
                isNotAlpha.shouldBeTrue()
                isBeta.shouldBeFalse()
                isNotBeta.shouldBeTrue()
                isDev.shouldBeFalse()
                isNotDev.shouldBeTrue()
                isRC.shouldBeFalse()
                isNotRC.shouldBeTrue()
                isSnapshot.shouldBeTrue()
                isNotSnapshot.shouldBeFalse()
            }
    }
}
