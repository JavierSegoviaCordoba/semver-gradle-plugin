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
        create("SemVerPlugin") {
            id = "com.javiersc.semver.gradle.plugin"
            displayName = "SemVer"
            description = "Manage project versions automatically with git tags"
            implementationClass = "com.javiersc.semver.gradle.plugin.SemVerPlugin"
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
    implementation(libs.javiersc.kotlin.kotlinStdlib)
    implementation(libs.javiersc.semver.semverCore)

    testImplementation(gradleTestKit())
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
