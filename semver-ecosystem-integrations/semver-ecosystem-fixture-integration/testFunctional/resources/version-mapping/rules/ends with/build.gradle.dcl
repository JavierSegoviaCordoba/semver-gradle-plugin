semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("ends") {
                metadata("ends")
                rules {
                    rule("ends") {
                        any {
                            endsWith(".0")
                        }
                    }
                }
            }
        }
    }
}
