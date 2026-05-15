semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("mapped-fields") {
                major(2)
                minor(3)
                patch(4)
                stage {
                    name("beta")
                    number(5)
                }
                commits(6)
                hash("abcdefg")
                metadata("fields")
                rules {
                    rule("mapped-fields") {
                        all {
                            mappedMajorIsPresent()
                            mappedMinorIsPresent()
                            mappedPatchIsPresent()
                            mappedStageIsPresent()
                            mappedStageNameIsPresent()
                            mappedStageNumberIsPresent()
                            mappedCommitsIsPresent()
                            mappedHashIsPresent()
                            mappedMetadataIsPresent()
                        }
                    }
                }
            }
        }
    }
}
