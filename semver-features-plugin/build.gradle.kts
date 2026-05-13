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
                dependencies { //
                    implementation(projects.semverEcosystemFixturePlugin)
                    implementation(projects.semverEcosystemIntegrations.semverEcosystemFixtureIntegration)
                    implementation(projects.semverFeaturesPluginApi)
                }
            }
        }
    }

    gradle {
        plugin {
            gradlePlugin {
                plugins {
                    create("SemverEcosystemPlugin") {
                        id = "com.javiersc.semver.features"
                        displayName = "Semver Features"
                        description = "Semver Features plugins"
                        implementationClass = "com.javiersc.semver.features.plugin.SemverFeaturesPlugin"
                    }
                }
            }
        }
    }
}
