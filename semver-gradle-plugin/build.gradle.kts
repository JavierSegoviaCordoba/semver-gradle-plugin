plugins {
    `kotlin-jvm`
    `java-gradle-plugin`
    `javiersc-kotlin-config`
    `javiersc-publish`
}

pluginBundle {
    tags =
        listOf(
            "semver",
            "semantic versioning",
            "semantic version",
            "git tags",
            "git version",
        )
}

gradlePlugin {
    plugins {
        create("SemverPlugin") {
            id = "com.javiersc.semver.gradle.plugin"
            displayName = "Semver"
            description = "Manage project versions automatically with git tags"
            implementationClass = "com.javiersc.semver.gradle.plugin.SemverPlugin"
        }
    }
}

kotlin {
    explicitApi()
}

val testPluginClasspath: Configuration by configurations.creating

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(libs.eclipse.jgit.eclipseJgit)
    implementation(libs.javiersc.gradleExtensions.gradleExt)
    implementation(libs.javiersc.kotlin.kotlinStdlib)
    implementation(libs.javiersc.semver.semverCore)
    implementation(pluginLibs.jetbrains.gradlePluginIdeaExt.gradleIdeaExt)

    testImplementation(gradleTestKit())
    testImplementation(libs.javiersc.gradleExtensions.gradleTestkitExt)
    testImplementation(libs.jetbrains.kotlin.kotlinTest)
    testImplementation(libs.kotest.kotestAssertionsCore)

    testPluginClasspath(pluginLibs.android.application.androidApplicationGradlePlugin)
    testPluginClasspath(pluginLibs.jetbrains.kotlin.kotlinGradlePlugin)
}

tasks {
    pluginUnderTestMetadata {
        pluginClasspath.from(testPluginClasspath)
    }
}
