semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("combined") {
                metadata("combined")
                rules {
                    rule("combined") {
                        all {
                            metadataIsPresent(false)
                            requestedTagPrefix()
                            startsWith("1.0")
                            contains("0.0")
                            endsWith(".0")
                            pattern("1\\.0\\.0")
                        }
                    }
                }
            }
        }
    }
}
