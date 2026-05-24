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
                    implementation(projects.semverEcosystemFixturePlugin)
                    implementation(projects.semverFeaturesPluginApi)
                    implementation(projects.semverShared)
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
                        id = "semver.ecosystem.fixture.integration"
                        displayName = "Semver Ecosystem Fixture Integration"
                        description = "Fixture integration for Semver Declarative functional tests"
                        implementationClass =
                            "com.javiersc.semver.features.fixture.integration.gradle.plugin.SemverEcosystemFixtureIntegrationPlugin"
                    }
                }
            }
            pluginUnderTestDependencies(
                projects.semverEcosystemFixturePlugin,
                projects.semverFeaturesPlugin,
            )
        }
    }
}
