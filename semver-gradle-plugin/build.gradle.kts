plugins {
    alias(libs.plugins.javiersc.hubdle)
}

hubdle {
    config {
        explicitApi()
        publishing()
    }
    kotlin {
        jvm {
            features {
                gradle {
                    plugin {
                        tags("semver", "semantic versioning", "semantic version", "git tags", "git version")

                        gradlePlugin {
                            plugins {
                                create("SemverPlugin") {
                                    id = "com.javiersc.semver.gradle.plugin"
                                    displayName = "Semver"
                                    description = "Manage project versions automatically with git tags"
                                    implementationClass =
                                        "com.javiersc.semver.gradle.plugin.SemverPlugin"
                                }
                            }
                        }

                        pluginUnderTestDependencies(
                            androidToolsBuildGradle(),
                            jetbrainsKotlinGradlePlugin(),
                        )
                    }
                }
            }

            main {
                dependencies {
                    api(libs.javiersc.semver.semverCore)
                    implementation(libs.eclipse.jgit.eclipseJgit)
                }
            }

            testIntegration {
                dependencies {
                    implementation(libs.eclipse.jgit.eclipseJgit)
                }
            }

            testFunctional {
                dependencies {
                    implementation(libs.eclipse.jgit.eclipseJgit)
                }
            }

            testFixtures {
                dependencies {
                    implementation(libs.eclipse.jgit.eclipseJgit)
                }
            }
        }
    }
}
