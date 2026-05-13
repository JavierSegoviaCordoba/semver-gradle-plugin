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

            main {
                dependencies {
                    implementation(projects.gradleVersion)
                    implementation(projects.semverShared)
                    compileOnly(gradleKotlinDsl())
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
                    implementation(testFixtures(projects.semverShared))
                }
            }

            testFixtures {
                dependencies { //
                    implementation(hubdle.eclipse.jgit)
                }
            }
        }
    }

    gradle {
        plugin {
            gradlePlugin {
                plugins {
                    create("SemverDeclarativePlugin") {
                        id = "com.javiersc.semver.declarative"
                        displayName = "Semver Declarative"
                        description = "Gradle Declarative support for Semver"
                        implementationClass =
                            "com.javiersc.semver.declarative.gradle.plugin.SemverDeclarativePlugin"
                    }
                }
            }
            pluginUnderTestDependencies(
                hubdle.android.tools.build.gradle,
                hubdle.jetbrains.kotlin.gradle.plugin,
                projects.semverEcosystemFixturePlugin,
                projects.semverGradlePlugin,
                projects.semverProjectGradlePlugin,
                projects.semverSettingsGradlePlugin,
            )
        }
    }
}
