plugins {
    id("com.javiersc.semver.project")
}

semver {
    tagPrefix.set("v")
    mapVersion { gradleVersion ->
        val kotlinVersion = "2.0.10"
        val metadata =
            gradleVersion.metadata?.let { "$kotlinVersion-$it" } ?: kotlinVersion
        "${gradleVersion.copy(metadata = metadata)}"
    }
}
