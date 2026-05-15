semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("any") {
                metadata("any")
                rules {
                    rule("any") {
                        any {
                            contains("missing")
                            startsWith("1.0")
                        }
                    }
                }
            }
        }
    }
}
