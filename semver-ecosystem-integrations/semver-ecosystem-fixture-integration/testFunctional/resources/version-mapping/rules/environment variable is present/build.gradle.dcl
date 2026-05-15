semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("environment-variable-present") {
                metadata("env")
                rules {
                    rule("environment-variable-present") {
                        all {
                            environmentVariableIsPresent("SEMVER_TEST_MAPPED_ENV")
                        }
                    }
                }
            }
        }
    }
}
