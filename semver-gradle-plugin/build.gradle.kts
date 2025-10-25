hubdle {
    config {
        analysis()
        coverage()
        documentation { api() }
        explicitApi()
        publishing()
    }
    kotlin {
        jvm {
            features { //
                jvmVersion(JavaVersion.VERSION_17)
                kotest()
            }

            main {
                dependencies {
                    api(projects.semverProjectGradlePlugin)
                    api(projects.semverSettingsGradlePlugin)
                }
            }
        }
    }

    gradle {
        plugin {
            gradlePlugin {
                plugins {
                    create("SemverPlugin") {
                        id = "com.javiersc.semver"
                        displayName = "Semver"
                        description = "Manage project versions automatically with git tags"
                        implementationClass = "com.javiersc.semver.gradle.plugin.SemverPlugin"
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
                hubdle.android.tools.build.gradle,
                hubdle.jetbrains.kotlin.gradle.plugin,
            )
        }
    }
}
