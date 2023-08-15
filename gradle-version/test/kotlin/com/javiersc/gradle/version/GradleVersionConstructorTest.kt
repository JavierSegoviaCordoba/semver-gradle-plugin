package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.Scope
import com.javiersc.gradle.version.GradleVersion.Stage
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionConstructorTest {

    @Test
    fun constructor_value_call() {
        GradleVersion(value = "6.8.3").toString().shouldBe("6.8.3")
        GradleVersion(value = "1.0.0.0+M3t4D4ta-SNAPSHOT")
            .toString()
            .shouldBe("1.0.0.0+M3t4D4ta-SNAPSHOT")
        GradleVersion(value = "1.0.0.21+M3t4D4ta-SNAPSHOT")
            .toString()
            .shouldBe("1.0.0.21+M3t4D4ta-SNAPSHOT")
        GradleVersion(value = "1.0.0.0+1.9.2-dev-5787-SNAPSHOT")
            .toString()
            .shouldBe("1.0.0.0+1.9.2-dev-5787-SNAPSHOT")
        GradleVersion(value = "1.0.0.12+1.9.2-dev-5787-SNAPSHOT")
            .toString()
            .shouldBe("1.0.0.12+1.9.2-dev-5787-SNAPSHOT")
        GradleVersion(value = "1.0.0+1.9.2-dev-5787-SNAPSHOT")
            .toString()
            .shouldBe("1.0.0+1.9.2-dev-5787-SNAPSHOT")
    }

    @Test
    fun constructor_scope_stage_primitives_call() {
        GradleVersion(scope = "6.8.3", stage = "alpha.1").toString().shouldBe("6.8.3-alpha.1")
    }

    @Test
    fun constructor_scope_stage_call() {
        GradleVersion(scope = Scope(value = "6.8.3"), stage = null).toString().shouldBe("6.8.3")
        GradleVersion(scope = Scope(value = "6.8.3"), stage = Stage(value = "alpha.1"))
            .toString()
            .shouldBe("6.8.3-alpha.1")
        GradleVersion(scope = Scope(value = "6.8.3"), stage = Stage(name = "alpha", num = 1))
            .toString()
            .shouldBe("6.8.3-alpha.1")
    }

    @Test
    fun constructor_major_minor_patch_stageName_stageNum_commits_hash_metadata_call() {
        GradleVersion(
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
        GradleVersion(
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
