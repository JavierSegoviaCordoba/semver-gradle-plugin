hubdle {
    config {
        analysis()
        coverage()
        documentation { //
            api()
        }
        explicitApi()
        publishing {
            gradlePortal { //
                isEnabled = false
            }
        }
    }
    kotlin {
        jvm {
            features {
                gradle {
                    plugin {
                        pluginUnderTestDependencies(
                            hubdle.android.tools.build.gradle,
                            hubdle.jetbrains.kotlin.gradle.plugin,
                            projects.semverGradlePlugin,
                            projects.semverProjectGradlePlugin,
                            projects.semverSettingsGradlePlugin,
                        )
                    }
                }
                kotest()
            }

            main {
                dependencies {
                    api(projects.gradleVersion)
                    api(projects.semverProjectGradlePlugin)
                    implementation(hubdle.eclipse.jgit)
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
