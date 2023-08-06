package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion as Version
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionEdgeCasesTest {

    @Test
    fun edge_cases() {
        Version("1.0.0.0+M3t4D4ta-SNAPSHOT")
        Version("1.0.0.21+M3t4D4ta-SNAPSHOT")
        Version("1.0.0.0+1.9.2-dev-5787-SNAPSHOT")
        Version("1.0.0.12+1.9.2-dev-5787-SNAPSHOT")
        Version("1.0.0+1.9.2-dev-5787-SNAPSHOT")
        Version(
                major = 1,
                minor = 0,
                patch = 0,
                stageName = "SNAPSHOT",
                stageNum = null,
                commits = 23,
                hash = "h123456",
                metadata = "M3t4D4ta",
            )
            .toString()
            .shouldBe("1.0.0.23+h123456+M3t4D4ta-SNAPSHOT")
        Version(
                major = 20,
                minor = 12,
                patch = 50,
                stageName = "SNAPSHOT",
                stageNum = null,
                commits = null,
                hash = null,
                metadata = "1.9.20-dev-5788",
            )
            .toString()
            .shouldBe("20.12.50+1.9.20-dev-5788-SNAPSHOT")
    }
}
