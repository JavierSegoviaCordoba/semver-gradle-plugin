semverEcosystemFixture {
    semver {
        mapVersion {
            metadata("base")
        }
        mapVersions {
            mapVersion("metadata") {
                metadata("metadata")
                rules {
                    rule("metadata") {
                        any {
                            metadataIsPresent(false)
                        }
                    }
                }
            }
        }
    }
}
