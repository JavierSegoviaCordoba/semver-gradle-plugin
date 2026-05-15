semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("ignore") {
                metadata("ignore")
                rules {
                    rule("ignore") {
                        any {
                            contains("ALPHA", true)
                        }
                    }
                }
            }
        }
    }
}
