import com.javiersc.gradle.extensions.version.catalogs.artifact

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
                    implementation(libs.plugins.javiersc.hubdle.ecosystem.artifact)
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
            pluginUnderTestDependencies(
                libs.plugins.javiersc.hubdle.ecosystem.artifact,
                projects.semverFeaturesPlugin,
            )
        }
    }
}
