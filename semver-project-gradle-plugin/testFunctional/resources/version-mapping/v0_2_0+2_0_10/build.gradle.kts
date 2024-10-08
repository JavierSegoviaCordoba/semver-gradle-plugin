plugins {
    id("com.javiersc.semver")
}

semver {
    mapVersion { gradleVersion ->
        "${gradleVersion.copy(metadata = "2.0.10")}"
    }
}
