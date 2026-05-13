plugins {
    id("com.javiersc.semver.features")
    id("semver.ecosystem.fixture")
}

rootProject.name = "sandbox-project"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("library-a")
include("library-b")
