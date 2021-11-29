import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    `kotlin-jvm`
    `java-gradle-plugin`
    `javiersc-kotlin-library`
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(libs.eclipse.jgit.eclipseJgit)
    implementation(libs.javiersc.kotlin.kotlinStdlib)
    implementation(libs.javiersc.semanticVersioning.semanticVersioningCore)

    testImplementation(gradleTestKit())
    testImplementation(libs.jetbrains.kotlin.kotlinTest)
    testImplementation(libs.kotest.kotestAssertionsCore)
}
