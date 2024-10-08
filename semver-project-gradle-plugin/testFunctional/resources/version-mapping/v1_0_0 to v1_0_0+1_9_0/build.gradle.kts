plugins {
    id("com.javiersc.semver")
}

semver {
    tagPrefix.set("v")
    mapVersion { gradleVersion ->
        "${gradleVersion.copy(metadata = "1.9.0")}"
    }
}
