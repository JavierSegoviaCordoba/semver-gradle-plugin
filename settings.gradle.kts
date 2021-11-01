// TODO: Uncomment"{{ rootProject.name = providers.gradleProperty("allProjects.name").forUseAtConfigurationTime().get() }}"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("pluginLibs") { from(files("gradle/pluginLibs.versions.toml")) }
    }
}

include(":library")
