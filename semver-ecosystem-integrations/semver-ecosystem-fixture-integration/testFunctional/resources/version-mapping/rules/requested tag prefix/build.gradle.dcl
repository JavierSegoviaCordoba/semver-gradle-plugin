semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("requested") {
                metadata("requested")
                rules {
                    rule("requested") {
                        any {
                            requestedTagPrefix()
                        }
                    }
                }
            }
        }
    }
}
