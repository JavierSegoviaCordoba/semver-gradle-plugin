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

            main {
                dependencies {
                    implementation(projects.semverDeclarativeGradlePlugin)
                    implementation(projects.semverEcosystemFixtureGradlePlugin)
                }
            }

            testFunctional {
                dependencies { //
                    implementation(testFixtures(projects.semverShared))
                }
            }
        }
    }

    gradle {
        plugin {
            gradlePlugin {
                plugins {
                    create("SemverEcosystemFixtureIntegrationPlugin") {
                        id = "com.javiersc.semver.ecosystem.fixture.integration"
                        displayName = "Semver Ecosystem Fixture Integration"
                        description =
                            "Semver integration for the fixture ecosystem plugin"
                        implementationClass =
                            "com.javiersc.semver.ecosystem.fixture.integration.gradle.plugin.SemverEcosystemFixtureIntegrationPlugin"
                    }
                }
            }
            pluginUnderTestDependencies(
                projects.semverDeclarativeGradlePlugin,
                projects.semverEcosystemFixtureGradlePlugin,
            )
        }
    }
}
