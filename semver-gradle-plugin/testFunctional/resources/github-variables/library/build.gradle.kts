plugins {
    id("com.javiersc.semver.gradle.plugin")
    kotlin("jvm")
}

semver {
    tagPrefix.set("w")
}
