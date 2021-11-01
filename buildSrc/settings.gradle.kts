enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") { from(files("../gradle/libs.versions.toml")) }
        create("pluginLibs") { from(files("../gradle/pluginLibs.versions.toml")) }
    }
}
