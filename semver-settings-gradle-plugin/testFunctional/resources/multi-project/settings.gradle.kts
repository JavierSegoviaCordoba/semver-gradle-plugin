rootProject.name = "multi-project"

plugins {
    id("com.javiersc.semver")
}

include(
    ":library-one",
    ":library-two",
)
