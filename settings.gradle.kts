plugins {
    `gradle-enterprise`
}

rootProject.name = providers.gradleProperty("project.name").forUseAtConfigurationTime().get()

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("pluginLibs") { from(files("gradle/pluginLibs.versions.toml")) }
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

include(":semver-gradle-plugin")
