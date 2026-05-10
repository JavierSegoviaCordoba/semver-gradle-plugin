plugins {
    id("com.javiersc.semver.declarative")
}

rootProject.name = "sandbox-project"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("library-a")
include("library-b")
