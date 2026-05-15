semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("mapped-metadata-absent") {
                patch(1)
                rules {
                    rule("mapped-metadata-absent") {
                        all {
                            mappedMetadataIsPresent(false)
                        }
                    }
                }
            }
        }
    }
}
