plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    // TODO: remove when the next issue is fixed:
    //  https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(files(pluginLibs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(pluginLibs.android.toolsBuild.gradle)
    implementation(pluginLibs.javiersc.gradlePlugins.allPlugins)
    implementation(pluginLibs.jetbrains.kotlin.kotlinGradlePlugin)
    implementation(pluginLibs.jetbrains.kotlinx.binaryCompatibilityValidator)
}
