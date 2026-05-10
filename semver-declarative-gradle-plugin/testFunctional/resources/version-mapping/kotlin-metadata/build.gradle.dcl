semver {
    tagPrefix = "v"
    mapVersion {
        major(3)
        minor(4)
        patch(5)
        stage {
            name("alpha")
            number(6)
        }
    }
    mapVersions {
        mapVersion("kotlin-dev") {
            stage {
                name("SNAPSHOT")
            }
            metadata {
                gradleProperty("kotlinVersion")
            }
            rules {
                rule("kotlin-dev") {
                    all {
                        metadataIsPresent()
                        requestedTagPrefix()
                    }
                    any {
                        contains("Dev", false)
                        contains("dev", true)
                        pattern("""(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)-dev-(0|[1-9]\d*)""")
                    }
                    none {
                        contains("rc", true)
                        contains("beta", true)
                    }
                }
                rule("kotlin-dev") {
                    all {
                        metadataIsPresent()
                        requestedTagPrefix()
                    }
                }
            }
        }
    }
}
