semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("all") {
                metadata("all")
                rules {
                    rule("all") {
                        all {
                            startsWith("1.0")
                            contains("0.0")
                            endsWith(".0")
                        }
                    }
                }
            }
        }
    }
}
