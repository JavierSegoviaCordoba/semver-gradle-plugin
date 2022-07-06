import com.javiersc.gradle.tasks.extensions.namedLazily

plugins {
    alias(libs.plugins.javiersc.hubdle)
}

hubdle {
    config {
        explicitApi()
        publishing()
    }
    kotlin {
        gradle {
            plugin {
                tags("semver", "semantic versioning", "semantic version", "git tags", "git version")

                main {
                    dependencies {
                        implementation(libs.eclipse.jgit.eclipseJgit)
                        implementation(libs.javiersc.semver.semverCore)
                    }
                }

                rawConfig {
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
                }
            }
        }
    }
}

val testPluginClasspath: Configuration by configurations.creating

dependencies {
    testPluginClasspath(libs.android.application.androidApplicationGradlePlugin)
    testPluginClasspath(libs.jetbrains.kotlin.kotlinGradlePlugin)
}

tasks.namedLazily<PluginUnderTestMetadata>("pluginUnderTestMetadata").configureEach {
    pluginClasspath.from(testPluginClasspath)
}
