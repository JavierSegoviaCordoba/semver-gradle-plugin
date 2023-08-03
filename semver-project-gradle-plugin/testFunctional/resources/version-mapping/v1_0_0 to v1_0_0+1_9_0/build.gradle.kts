plugins {
    id("com.javiersc.semver.project")
}

semver {
    tagPrefix.set("v")
    mapVersion { gradleVersion ->
        "${gradleVersion.copy(metadata = "1.9.0")}"
    }
}
