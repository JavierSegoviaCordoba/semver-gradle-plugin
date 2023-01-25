plugins {
    id("com.javiersc.semver.gradle.plugin")
}

semver {
    tagPrefix.set("v")
}

println("SEMVER: $version")
