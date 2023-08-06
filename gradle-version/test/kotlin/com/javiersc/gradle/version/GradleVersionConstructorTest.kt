package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.Scope
import com.javiersc.gradle.version.GradleVersion.Stage
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionConstructorTest {

    @Test
    fun constructor_call() {
        GradleVersion(value = "6.8.3").toString().shouldBe("6.8.3")
        GradleVersion(scope = "6.8.3", stage = "alpha.1").toString().shouldBe("6.8.3-alpha.1")
        GradleVersion(scope = Scope(value = "6.8.3"), stage = null).toString().shouldBe("6.8.3")
        GradleVersion(scope = Scope(value = "6.8.3"), stage = Stage(value = "alpha.1"))
            .toString()
            .shouldBe("6.8.3-alpha.1")
        GradleVersion(scope = Scope(value = "6.8.3"), stage = Stage(name = "alpha", num = 1))
            .toString()
            .shouldBe("6.8.3-alpha.1")
    }
}
