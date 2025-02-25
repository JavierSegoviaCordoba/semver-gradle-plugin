rootProject.name = "multi-project"

plugins {
    id("com.javiersc.semver")
}

includeBuild(
    "build-logic",
)

include(
    ":library-one",
)
