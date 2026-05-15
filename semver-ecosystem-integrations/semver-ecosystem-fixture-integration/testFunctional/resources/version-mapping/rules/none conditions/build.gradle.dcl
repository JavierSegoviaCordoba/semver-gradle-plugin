semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("none") {
                metadata("none")
                rules {
                    rule("none") {
                        none {
                            contains("missing")
                            endsWith("missing")
                        }
                    }
                }
            }
        }
    }
}
