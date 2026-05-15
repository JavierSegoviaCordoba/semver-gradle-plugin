semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("pattern") {
                metadata("pattern")
                rules {
                    rule("pattern") {
                        any {
                            pattern("1\\.0\\.0")
                        }
                    }
                }
            }
        }
    }
}
