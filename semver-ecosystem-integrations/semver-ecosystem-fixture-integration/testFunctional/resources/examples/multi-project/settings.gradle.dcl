plugins {
    id("com.javiersc.semver.features")
    id("semver.ecosystem.fixture")
    id("semver.ecosystem.fixture.integration")
}

rootProject.name = "sandbox-project"

include(":library-one-a")
include(":library-two-b")
include(":library-three-b")
include(":library-four-b")
include(":library-five-b")
include(":library-six-c")
include(":library-seven-c")
include(":library-eight-c")
include(":library-nine")
include(":library-ten")
