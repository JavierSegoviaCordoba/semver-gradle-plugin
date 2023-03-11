rootProject.name = "multi-project"

plugins {
    id("com.javiersc.semver.settings")
}

include(
    ":library-one",
    ":library-two",
)
