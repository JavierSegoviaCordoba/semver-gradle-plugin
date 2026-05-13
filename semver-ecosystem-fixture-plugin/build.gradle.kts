hubdle {
    config {
        analysis()
        coverage()
        documentation { //
            api()
        }
        explicitApi()
        publishing {
            maven { //
                isEnabled = false
            }
            gradlePortal { //
                isEnabled = false
            }
        }
    }
    kotlin {
        jvm {
            features { //
                jvmVersion(JavaVersion.VERSION_17)
                kotest()
            }
        }
    }

    gradle {
        plugin {
            gradlePlugin {
                plugins {
                    create("SemverEcosystemFixturePlugin") {
                        id = "semver.ecosystem.fixture"
                        displayName = "Semver Ecosystem Fixture"
                        description = "Ecosystem fixture plugin for Semver Declarative functional tests"
                        implementationClass =
                            "com.javiersc.semver.ecosystem.fixture.gradle.plugin.SemverEcosystemFixturePlugin"
                    }
                }
            }
        }
    }
}
