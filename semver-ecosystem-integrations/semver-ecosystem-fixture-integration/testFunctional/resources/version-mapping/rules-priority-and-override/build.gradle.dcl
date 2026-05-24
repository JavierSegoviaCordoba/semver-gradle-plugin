semverEcosystemFixture {
    semver {
        mapVersion("7.8.9")
        mapVersions {
            mapVersion("low") {
                metadata("low")
                rules {
                    rule("low") {
                        priority = 1
                        any {
                            startsWith("1.0.0")
                        }
                    }
                }
            }
            mapVersion("high") {
                metadata("high")
                rules {
                    rule("high") {
                        priority = 2
                        any {
                            startsWith("1.0.0")
                        }
                    }
                }
            }
        }
    }
}
