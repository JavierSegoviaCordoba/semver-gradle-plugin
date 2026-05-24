plugins {
    id("com.javiersc.semver.features")
    id("semver.ecosystem.fixture")
    id("semver.ecosystem.fixture.integration")
}

rootProject.name = "sandbox-project"

include(":library")
