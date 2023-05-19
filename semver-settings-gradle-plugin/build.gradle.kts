hubdle {
    config {
        analysis()
        coverage()
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
                                create("SemverSettingsPlugin") {
                                    id = "com.javiersc.semver.settings"
                                    displayName = "Semver Settings Plugin"
                                    description = "Apply Semver plugin to all projects"
                                    implementationClass =
                                        "com.javiersc.semver.settings.gradle.plugin.SemverSettingsPlugin"
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
                            hubdle.android.toolsBuild.gradle,
                            hubdle.jetbrains.kotlin.kotlinGradlePlugin,
                        )
                    }
                }
            }

            main {
                dependencies {
                    api(projects.gradleVersion)
                    api(projects.semverProjectGradlePlugin)
                    implementation(hubdle.eclipse.jgit)
                }
            }

            testIntegration {
                dependencies {
                    implementation(hubdle.eclipse.jgit)
                }
            }

            testFunctional {
                dependencies {
                    implementation(hubdle.eclipse.jgit)
                }
            }

            testFixtures {
                dependencies {
                    implementation(hubdle.eclipse.jgit)
                }
            }
        }
    }
}
