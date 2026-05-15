semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("environment-variable-absent") {
                metadata("env-absent")
                rules {
                    rule("environment-variable-absent") {
                        all {
                            environmentVariableIsPresent("SEMVER_TEST_MISSING_ENV", false)
                        }
                    }
                }
            }
        }
    }
}
