semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("property-present") {
                metadata("property")
                rules {
                    rule("property-present") {
                        all {
                            gradlePropertyIsPresent("semver.rule.property")
                        }
                    }
                }
            }
        }
    }
}
