plugins {
    id("com.javiersc.semver")
    kotlin("jvm")
}

semver {
    tagPrefix.set("v")
}
