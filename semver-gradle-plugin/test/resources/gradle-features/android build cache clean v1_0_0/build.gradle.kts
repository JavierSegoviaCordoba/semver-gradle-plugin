plugins {
    id("com.javiersc.semver.gradle.plugin")
    id("com.android.application")
}

semver {
    tagPrefix.set("v")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.example.myapp"
        minSdk = 21
        versionCode = "$version".first().toInt()
        versionName = "$version"
    }
}
