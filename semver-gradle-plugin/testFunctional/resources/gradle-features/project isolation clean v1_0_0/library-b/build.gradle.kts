plugins {
    id("com.javiersc.semver.gradle.plugin")
    java
}

semver {
    tagPrefix.set("v")
}
