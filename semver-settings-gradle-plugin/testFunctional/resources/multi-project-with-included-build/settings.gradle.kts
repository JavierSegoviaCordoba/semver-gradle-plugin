rootProject.name = "multi-project"

plugins {
    id("com.javiersc.semver")
}

semver {
    tagPrefix.set("v")
}

include(
    ":library-one",
    ":library-two",
    ":library-three",
)
