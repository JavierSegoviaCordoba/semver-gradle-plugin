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
            features { //
                jvmVersion(JavaVersion.VERSION_17)
                kotest()
            }

            main { dependencies { api(projects.semverShared) } }

            test {
                dependencies { //
                    implementation(testFixtures(projects.semverShared))
                }
            }

            testIntegration {
                dependencies {
                    implementation(testFixtures(projects.semverShared))
                    implementation(hubdle.eclipse.jgit)
                }
            }

            testFunctional {
                dependencies {
                    implementation(testFixtures(projects.semverShared))
                    implementation(hubdle.eclipse.jgit)
                }
            }

            testFixtures {
                dependencies { //
                    implementation(testFixtures(projects.semverShared))
                    implementation(hubdle.eclipse.jgit)
                }
            }
        }
    }

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
}
