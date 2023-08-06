package com.javiersc.gradle.version

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionToStringTest {

    @Test
    fun value_is_correct() {
        "${GradleVersion("12.23.34-SNAPSHOT")}".shouldBe("12.23.34-SNAPSHOT")
        "${GradleVersion("12.23.34", "alpha.5")}".shouldBe("12.23.34-alpha.5")
        "${GradleVersion("12.23.34", "SNAPSHOT")}".shouldBe("12.23.34-SNAPSHOT")
        "${GradleVersion(1, 2, 3, "alpha", 1)}".shouldBe("1.2.3-alpha.1")
        "${GradleVersion(1, 2, 3, "SNAPSHOT", null)}".shouldBe("1.2.3-SNAPSHOT")
    }
}
