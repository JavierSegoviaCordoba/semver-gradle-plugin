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

val testPluginClasspath: Configuration by configurations.creating {
    attributes {
        attribute(
            Usage.USAGE_ATTRIBUTE,
            project.objects.named(Usage.JAVA_RUNTIME)
        )
        attribute(
            Category.CATEGORY_ATTRIBUTE,
            project.objects.named(Category.LIBRARY)
        )
        attribute(
            GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE,
            project.objects.named("7.0")
        )
    }
}

dependencies {
    testPluginClasspath(libs.android.application.androidApplicationGradlePlugin)
    testPluginClasspath(libs.jetbrains.kotlin.kotlinGradlePlugin)
}

tasks.withType<PluginUnderTestMetadata>().configureEach {
    pluginClasspath.from(testPluginClasspath)
}
