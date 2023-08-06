package com.javiersc.gradle.version

import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test

internal class GradleVersionOrderTest {

    @Test
    fun order() {
        val actualList =
            listOf(
                GradleVersion("0.1.0-SNAPSHOT"),
                GradleVersion("0.1.0-rc.13"),
                GradleVersion("0.1.0-rc.12"),
                GradleVersion("0.1.0-rc.11"),
                GradleVersion("0.1.0-rc.10"),
                GradleVersion("0.1.0-rc.9"),
                GradleVersion("0.1.0-rc.8"),
                GradleVersion("0.1.0-rc.7"),
                GradleVersion("0.1.0-rc.6"),
                GradleVersion("0.1.0-rc.5"),
                GradleVersion("0.1.0-rc.4"),
                GradleVersion("0.1.0-rc.3"),
                GradleVersion("0.1.0-rc.2"),
                GradleVersion("0.1.0-rc.1"),
                GradleVersion("0.1.0-beta.5"),
                GradleVersion("0.1.0-beta.4"),
                GradleVersion("0.1.0-beta.3"),
                GradleVersion("0.1.0-beta.2"),
                GradleVersion("0.1.0-beta.1"),
                GradleVersion("0.1.0-alpha.23"),
                GradleVersion("0.1.0-alpha.22"),
                GradleVersion("0.1.0-alpha.21"),
                GradleVersion("0.1.0-alpha.20"),
                GradleVersion("0.1.0-alpha.19"),
                GradleVersion("0.1.0-alpha.18"),
                GradleVersion("0.1.0-alpha.17"),
                GradleVersion("0.1.0-alpha.16"),
                GradleVersion("0.1.0-alpha.15"),
                GradleVersion("0.1.0-alpha.14"),
                GradleVersion("0.1.0-alpha.13"),
                GradleVersion("0.1.0-alpha.12"),
                GradleVersion("0.1.0-alpha.11"),
                GradleVersion("0.1.0-alpha.10"),
                GradleVersion("0.1.0-alpha.9"),
                GradleVersion("0.1.0-alpha.8"),
                GradleVersion("0.1.0-alpha.7"),
                GradleVersion("0.1.0-alpha.6"),
                GradleVersion("0.1.0-alpha.5"),
                GradleVersion("0.1.0-alpha.4"),
                GradleVersion("0.1.0-alpha.3"),
                GradleVersion("0.1.0-alpha.2"),
                GradleVersion("0.1.0-alpha.1"),
            )

        actualList.shouldContainExactly(actualList.sortedDescending())
    }
}
