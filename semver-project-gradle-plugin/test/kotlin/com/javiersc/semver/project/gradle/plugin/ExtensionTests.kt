package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.isAlpha
import com.javiersc.gradle.version.isBeta
import com.javiersc.gradle.version.isDev
import com.javiersc.gradle.version.isNotAlpha
import com.javiersc.gradle.version.isNotBeta
import com.javiersc.gradle.version.isNotDev
import com.javiersc.gradle.version.isNotRC
import com.javiersc.gradle.version.isNotSnapshot
import com.javiersc.gradle.version.isRC
import com.javiersc.gradle.version.isSnapshot
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test

class ExtensionTests {

    @Test
    fun `is version`() {
        "1.0.0-alpha.1".isAlpha.shouldBeTrue()
        "1.0.0-alpha.1".isNotAlpha.shouldBeFalse()
        "1.0.0-alpha.1".isBeta.shouldBeFalse()
        "1.0.0-alpha.1".isNotBeta.shouldBeTrue()
        "1.0.0-alpha.1".isDev.shouldBeFalse()
        "1.0.0-alpha.1".isNotDev.shouldBeTrue()
        "1.0.0-alpha.1".isRC.shouldBeFalse()
        "1.0.0-alpha.1".isNotRC.shouldBeTrue()
        "1.0.0-alpha.1".isSnapshot.shouldBeFalse()
        "1.0.0-alpha.1".isNotSnapshot.shouldBeTrue()

        "1.0.0-beta.1".isAlpha.shouldBeFalse()
        "1.0.0-beta.1".isNotAlpha.shouldBeTrue()
        "1.0.0-beta.1".isBeta.shouldBeTrue()
        "1.0.0-beta.1".isNotBeta.shouldBeFalse()
        "1.0.0-beta.1".isDev.shouldBeFalse()
        "1.0.0-beta.1".isNotDev.shouldBeTrue()
        "1.0.0-beta.1".isRC.shouldBeFalse()
        "1.0.0-beta.1".isNotRC.shouldBeTrue()
        "1.0.0-beta.1".isSnapshot.shouldBeFalse()
        "1.0.0-beta.1".isNotSnapshot.shouldBeTrue()

        "1.0.0-dev.1".isAlpha.shouldBeFalse()
        "1.0.0-dev.1".isNotAlpha.shouldBeTrue()
        "1.0.0-dev.1".isBeta.shouldBeFalse()
        "1.0.0-dev.1".isNotBeta.shouldBeTrue()
        "1.0.0-dev.1".isDev.shouldBeTrue()
        "1.0.0-dev.1".isNotDev.shouldBeFalse()
        "1.0.0-dev.1".isRC.shouldBeFalse()
        "1.0.0-dev.1".isNotRC.shouldBeTrue()
        "1.0.0-dev.1".isSnapshot.shouldBeFalse()
        "1.0.0-dev.1".isNotSnapshot.shouldBeTrue()

        "1.0.0-rc.1".isAlpha.shouldBeFalse()
        "1.0.0-rc.1".isNotAlpha.shouldBeTrue()
        "1.0.0-rc.1".isBeta.shouldBeFalse()
        "1.0.0-rc.1".isNotBeta.shouldBeTrue()
        "1.0.0-rc.1".isDev.shouldBeFalse()
        "1.0.0-rc.1".isNotDev.shouldBeTrue()
        "1.0.0-rc.1".isRC.shouldBeTrue()
        "1.0.0-rc.1".isNotRC.shouldBeFalse()
        "1.0.0-rc.1".isSnapshot.shouldBeFalse()
        "1.0.0-rc.1".isNotSnapshot.shouldBeTrue()

        "1.0.0-SNAPSHOT".isAlpha.shouldBeFalse()
        "1.0.0-SNAPSHOT".isNotAlpha.shouldBeTrue()
        "1.0.0-SNAPSHOT".isBeta.shouldBeFalse()
        "1.0.0-SNAPSHOT".isNotBeta.shouldBeTrue()
        "1.0.0-SNAPSHOT".isDev.shouldBeFalse()
        "1.0.0-SNAPSHOT".isNotDev.shouldBeTrue()
        "1.0.0-SNAPSHOT".isRC.shouldBeFalse()
        "1.0.0-SNAPSHOT".isNotRC.shouldBeTrue()
        "1.0.0-SNAPSHOT".isSnapshot.shouldBeTrue()
        "1.0.0-SNAPSHOT".isNotSnapshot.shouldBeFalse()
    }
}
