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

                main {
                    dependencies {
                        implementation(libs.eclipse.jgit.eclipseJgit)
                        implementation(libs.javiersc.semver.semverCore)
                    }
                }

                pluginUnderTestDependencies(
                    libs.android.application.androidApplicationGradlePlugin,
                    libs.jetbrains.kotlin.kotlinGradlePlugin,
                )
            }
        }
    }
}
