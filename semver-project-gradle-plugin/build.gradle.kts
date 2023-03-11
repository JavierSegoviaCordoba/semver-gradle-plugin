plugins {
    alias(libs.plugins.javiersc.hubdle)
}

hubdle {
    config {
        analysis()
        documentation {
            api()
        }
        explicitApi()
        publishing()
    }
    kotlin {
        jvm {
            features {
                gradle {
                    plugin {
                        gradlePlugin {
                            plugins {
                                create("SemverProjectPlugin") {
                                    id = "com.javiersc.semver.project"
                                    displayName = "Semver Project plugin"
                                    description =
                                        "Manage project versions automatically with git tags"
                                    implementationClass =
                                        "com.javiersc.semver.project.gradle.plugin.SemverProjectPlugin"
                                    tags.set(
                                        listOf(
                                            "semver",
                                            "semantic versioning",
                                            "semantic version",
                                            "git tags",
                                            "git version",
                                        )
                                    )
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
