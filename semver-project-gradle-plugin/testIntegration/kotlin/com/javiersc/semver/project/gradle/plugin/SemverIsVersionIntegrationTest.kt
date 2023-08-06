package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.project.test.extensions.GradleProjectTest
import com.javiersc.semver.project.gradle.plugin.extensions.isAlpha
import com.javiersc.semver.project.gradle.plugin.extensions.isBeta
import com.javiersc.semver.project.gradle.plugin.extensions.isDev
import com.javiersc.semver.project.gradle.plugin.extensions.isNotAlpha
import com.javiersc.semver.project.gradle.plugin.extensions.isNotBeta
import com.javiersc.semver.project.gradle.plugin.extensions.isNotDev
import com.javiersc.semver.project.gradle.plugin.extensions.isNotRC
import com.javiersc.semver.project.gradle.plugin.extensions.isNotSnapshot
import com.javiersc.semver.project.gradle.plugin.extensions.isRC
import com.javiersc.semver.project.gradle.plugin.extensions.isSnapshot
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test

internal class SemverIsVersionIntegrationTest : GradleProjectTest() {

    @Test
    fun `given a project which has some versions, when isProperty is called, then the boolean is correct`() {
        gradleProjectTest {
            version = "1.0.0"
            isAlpha.get().shouldBeFalse()
            isNotAlpha.get().shouldBeTrue()
            isBeta.get().shouldBeFalse()
            isNotBeta.get().shouldBeTrue()
            isDev.get().shouldBeFalse()
            isNotDev.get().shouldBeTrue()
            isRC.get().shouldBeFalse()
            isNotRC.get().shouldBeTrue()
            isSnapshot.get().shouldBeFalse()
            isNotSnapshot.get().shouldBeTrue()

            version = "1.0.0-alpha.1"
            isAlpha.get().shouldBeTrue()

            version = "1.0.0-beta.1"
            isBeta.get().shouldBeTrue()

            version = "1.0.0-dev.1"
            isDev.get().shouldBeTrue()

            version = "1.0.0-rc.1"
            isRC.get().shouldBeTrue()

            version = "1.0.0-SNAPSHOT"
            isSnapshot.get().shouldBeTrue()
        }
    }
}
