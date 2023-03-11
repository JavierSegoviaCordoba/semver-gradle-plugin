plugins {
    id("com.javiersc.semver.project")
    kotlin("jvm")
}

semver {
    tagPrefix.set("w")
}
