semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("kotlin-dev") {
                major(1)
                minor(0)
                patch(1)
                stage {
                    name("SNAPSHOT")
                }
                metadata {
                    gradleProperty("kotlinVersion")
                }
                rules {
                    rule("kotlin-dev") {
                        priority = 10
                        all {
                            gradlePropertyIsPresent("kotlinVersion")
                            mappedStageName("SNAPSHOT")
                            mappedMetadata("2.2.0-dev-123")
                            mappedPattern(".*dev.*")
                        }
                    }
                }
            }
            mapVersion("kotlin-release") {
                metadata {
                    gradleProperty("kotlinVersion")
                }
                rules {
                    rule("kotlin-release") {
                        priority = 0
                        all {
                            gradlePropertyIsPresent("kotlinVersion")
                        }
                    }
                }
            }
        }
    }
}
