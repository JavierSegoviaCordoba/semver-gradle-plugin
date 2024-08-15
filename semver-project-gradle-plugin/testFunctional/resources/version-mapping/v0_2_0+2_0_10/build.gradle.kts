plugins {
    id("com.javiersc.semver.project")
}

semver {
    mapVersion { gradleVersion ->
        "${gradleVersion.copy(metadata = "2.0.10")}"
    }
}
