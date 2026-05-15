semverEcosystemFixture {
    semver {
        mapVersions {
            mapVersion("contains") {
                metadata("contains")
                rules {
                    rule("contains") {
                        any {
                            contains("0.0")
                        }
                    }
                }
            }
        }
    }
}
