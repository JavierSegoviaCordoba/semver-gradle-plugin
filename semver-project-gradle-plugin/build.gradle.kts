hubdle {
    config {
        analysis()
        coverage()
        documentation { //
            api()
        }
        explicitApi()
        publishing()
    }
    kotlin {
        jvm {
            features {
                coroutines()
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

                        pluginUnderTestExternalDependencies(
                            hubdle.android.toolsBuild.gradle,
                            hubdle.jetbrains.kotlin.kotlinGradlePlugin,
                        )
                    }
                }
            }

            main {
                dependencies {
                    api(projects.gradleVersion)
                    implementation(hubdle.eclipse.jgit)
                }
            }
            test {
                dependencies { //
                    implementation(hubdle.kotest.kotestProperty)
                }
            }
            testIntegration {
                dependencies { //
                    implementation(hubdle.eclipse.jgit)
                }
            }

            testFunctional {
                dependencies { //
                    implementation(hubdle.eclipse.jgit)
                }
            }

            testFixtures {
                dependencies { //
                    implementation(hubdle.eclipse.jgit)
                }
            }
        }
    }
}
