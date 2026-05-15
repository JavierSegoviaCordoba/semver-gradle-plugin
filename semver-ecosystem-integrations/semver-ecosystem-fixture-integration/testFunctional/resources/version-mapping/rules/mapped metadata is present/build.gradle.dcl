semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("mapped-metadata-present") {
                metadata("mapped")
                rules {
                    rule("mapped-metadata-present") {
                        all {
                            mappedMetadataIsPresent()
                        }
                    }
                }
            }
        }
    }
}
