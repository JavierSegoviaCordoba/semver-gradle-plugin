import com.javiersc.semver.settings.gradle.plugin.SemverSettingsExtension

rootProject.name = "build-logic"

plugins {
    id("com.javiersc.semver")
}

configure<SemverSettingsExtension> {
    gitDir.set(rootDir.parentFile.resolve(".git"))
}

include(
    "module-a",
)
