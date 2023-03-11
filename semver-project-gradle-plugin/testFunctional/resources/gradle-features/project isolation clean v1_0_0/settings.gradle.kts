rootProject.name = "sandbox-project"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(
    "library-a",
    "library-b",
)
