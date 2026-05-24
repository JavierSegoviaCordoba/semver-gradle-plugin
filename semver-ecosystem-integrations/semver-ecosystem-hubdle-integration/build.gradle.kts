import com.javiersc.gradle.extensions.version.catalogs.artifact
import com.javiersc.gradle.properties.extensions.getBooleanProperty

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
                isEnabled.set(provider { project.version.toString().endsWith("-SNAPSHOT") })
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
                    implementation(libs.javiersc.hubdle.ecosystem.feature.versioning)
                    implementation(libs.javiersc.hubdle.ecosystem.api)
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
