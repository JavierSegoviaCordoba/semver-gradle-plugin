package com.javiersc.gradle.version

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GradleVersionPublicPropertiesTest {

    @Test
    fun public_properties_and_constructors_with_correct_versions() {
        with(GradleVersion("1.2.3")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage.shouldBeNull()
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }

        with(GradleVersion("0.1.0.44+H4SH123")) {
            major.shouldBe(0)
            minor.shouldBe(1)
            patch.shouldBe(0)
            stage.shouldBeNull()
            commits.shouldBe(44)
            hash.shouldBe("H4SH123")
            metadata.shouldBeNull()
        }

        with(GradleVersion("0.1.0-rc.1.44+H4SH123")) {
            major.shouldBe(0)
            minor.shouldBe(1)
            patch.shouldBe(0)
            stage?.name.shouldBe("rc")
            stage?.num.shouldBe(1)
            commits.shouldBe(44)
            hash.shouldBe("H4SH123")
            metadata.shouldBeNull()
        }

        with(GradleVersion("0.1.0-rc.1.44+DIRTY")) {
            major.shouldBe(0)
            minor.shouldBe(1)
            patch.shouldBe(0)
            stage?.name.shouldBe("rc")
            stage?.num.shouldBe(1)
            commits.shouldBe(44)
            hash.shouldBeNull()
            metadata.shouldBe("DIRTY")
        }

        with(GradleVersion("0.5.0-beta.6.1+1.9.20-dev-5788")) {
            major.shouldBe(0)
            minor.shouldBe(5)
            patch.shouldBe(0)
            stage?.name.shouldBe("beta")
            stage?.num.shouldBe(6)
            commits.shouldBe(1)
            hash.shouldBeNull()
            metadata.shouldBe("1.9.20-dev-5788")
        }

        with(GradleVersion("1.2.3", "SNAPSHOT")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage?.name.shouldBe("SNAPSHOT")
            stage?.num.shouldBeNull()
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }

        with(GradleVersion("1.2.3", "final")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage.shouldBeNull()
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }

        with(GradleVersion("1.2.3-alpha.3")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage?.name.shouldBe("alpha")
            stage?.num.shouldBe(3)
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }

        with(GradleVersion("1.2.3-dev.3")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage?.name.shouldBe("dev")
            stage?.num.shouldBe(3)
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }

        with(GradleVersion("1.2.3", "alpha.3")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage?.name.shouldBe("alpha")
            stage?.num.shouldBe(3)
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }

        with(GradleVersion("1.2.3", "dev.3")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage.shouldNotBeNull().name.shouldBe("dev")
            stage.shouldNotBeNull().num.shouldBe(3)
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }

        with(GradleVersion("1.2.3", "SNAPSHOT")) {
            major.shouldBe(1)
            minor.shouldBe(2)
            patch.shouldBe(3)
            stage.shouldNotBeNull().name.shouldBe("SNAPSHOT")
            stage.shouldNotBeNull().num.shouldBeNull()
            commits.shouldBeNull()
            hash.shouldBeNull()
            metadata.shouldBeNull()
        }
    }
}
