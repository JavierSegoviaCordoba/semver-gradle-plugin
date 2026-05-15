semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("starts") {
                metadata("starts")
                rules {
                    rule("starts") {
                        any {
                            startsWith("1.0")
                        }
                    }
                }
            }
        }
    }
}
