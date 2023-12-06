plugins {
    id("com.javiersc.semver.project")
}

semver {
    tagPrefix.set("v")
    mapVersion { gradleVersion, git ->
        "${gradleVersion.copy(metadata = "2.0.0-${git.branch.name}")}"
    }
}
