semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("property-absent") {
                metadata("property-absent")
                rules {
                    rule("property-absent") {
                        all {
                            gradlePropertyIsPresent("semver.rule.missing", false)
                        }
                    }
                }
            }
        }
    }
}
