semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("mapped-values") {
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
                    rule("mapped-values") {
                        all {
                            mappedMajor(2)
                            mappedMinor(3)
                            mappedPatch(4)
                            mappedStageName("BETA", true)
                            mappedStageNumber(5)
                            mappedCommits(6)
                            mappedHash("ABCDEFG", true)
                            mappedMetadata("FIELDS", true)
                            mappedStartsWith("2.3")
                            mappedContains("beta.5")
                            mappedEndsWith("+fields")
                            mappedPattern("2\\.3\\.4-beta\\.5\\.6\\+abcdefg\\+fields")
                        }
                    }
                }
            }
        }
    }
}
