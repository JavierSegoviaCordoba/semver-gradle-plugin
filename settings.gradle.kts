pluginManagement {
    val hubdleVersion: String =
        file("$rootDir/gradle/libs.versions.toml")
            .readLines()
            .first { it.contains("hubdle") }
            .split("\"")[1]

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://central.sonatype.com/repository/maven-snapshots/") {
            mavenContent { snapshotsOnly() }
            content { includeGroupByRegex("com\\.javiersc\\.hubdle.*") }
        }
        google()
    }

    plugins {
        id("com.javiersc.hubdle") version hubdleVersion
    }
}

plugins {
    id("com.javiersc.hubdle")
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://central.sonatype.com/repository/maven-snapshots/") {
            mavenContent { snapshotsOnly() }
            content { includeGroupByRegex("com\\.javiersc\\.hubdle.*") }
        }
    }
}
