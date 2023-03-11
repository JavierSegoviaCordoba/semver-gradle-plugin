plugins {
    id("com.javiersc.semver.project")
}

semver {
    tagPrefix.set("v")
}

println("SEMVER: $version")
