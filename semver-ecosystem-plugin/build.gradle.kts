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
                    implementation(projects.semverEcosystemPluginApi)
                }
            }
        }
    }

    gradle {
        plugin {
            gradlePlugin {
                plugins {
                    create("SemverEcosystemPlugin") {
                        id = "com.javiersc.semver.ecosystem"
                        displayName = "Semver Ecosystem"
                        description = "Ecosystem plugin for Semver"
                        implementationClass = "com.javiersc.semver.ecosystem.plugin.SemverEcosystemPlugin"
                    }
                }
            }
        }
    }
}
