package com.javiersc.gradle.version

import io.kotest.assertions.throwables.shouldThrow
import kotlin.test.Test

internal class GradleVersionWrongVersionsTest {

    @Test
    fun incorrect_versions() {
        shouldThrow<GradleVersionException> { GradleVersion("1.0") }
        shouldThrow<GradleVersionException> { GradleVersion("3.53") }
        shouldThrow<GradleVersionException> { GradleVersion("222.22") }
        shouldThrow<GradleVersionException> { GradleVersion("4223.4343") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0-snapshot.1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0-SNAPSHOT.1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT.1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT.1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0", "SNAPSHOT.1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0", "SNAPSHOT.1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0", "snapshot.1") }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, "SNAPSHOT", 1) }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, "snapshot", 1) }
        shouldThrow<GradleVersionException> { GradleVersion("1a.2") }
        shouldThrow<GradleVersionException> { GradleVersion("1a.2a") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2a") }
        shouldThrow<GradleVersionException> { GradleVersion("1a.2.3") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2a.3a") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2.3a") }
        shouldThrow<GradleVersionException> { GradleVersion("1a.2a.3") }
        shouldThrow<GradleVersionException> { GradleVersion("1a.2.3a") }
        shouldThrow<GradleVersionException> { GradleVersion("1a.2a.3a") }
        shouldThrow<GradleVersionException> { GradleVersion("a1.2.3") }
        shouldThrow<GradleVersionException> { GradleVersion("1.a2.3") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2.a3") }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1.alpha.1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.1.1-alpha-1") }
        shouldThrow<GradleVersionException> { GradleVersion("1-1.1.alpha-1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.1-1.alpha-1") }
        shouldThrow<GradleVersionException> { GradleVersion("1-1-1.alpha-1") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2", "alpha.") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2", "alpha") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2", "11alpha") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2", "11alpha.2220s") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2", "alpha.2220s") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2.3", "alpha.2220s") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2.3", "alpha") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2.3", "alpha.") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2.3", "223alpha") }
        shouldThrow<GradleVersionException> { GradleVersion("1.2.3", "223alpha.") }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, "-alpha", 1) }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, "alpha-", 1) }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, ".alpha", 1) }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, "alpha.", 1) }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, "alpha3232", 1) }
        shouldThrow<GradleVersionException> { GradleVersion(1, 2, 3, "232alpha", 1) }
        shouldThrow<GradleVersionException> { GradleVersion("1.0").copy(stageName = "alpha") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT-11") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT-jjj11") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT-jaaj") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT-jjj") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT+jjj") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT+1.9.2") }
        shouldThrow<GradleVersionException> { GradleVersion("1.0.0-SNAPSHOT+1.9.2-dev-5677") }
        // TODO: shouldThrow<GradleVersionException> { Version("0.5.0-beta.6.1+.1.9.20-dev-5788") }
    }
}
