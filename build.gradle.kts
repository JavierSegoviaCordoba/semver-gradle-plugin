plugins {
    `javiersc-versioning`
    `javiersc-all-projects`
    `javiersc-changelog`
    `javiersc-code-analysis`
//    `javiersc-code-coverage`
    `javiersc-code-formatter`
    `javiersc-docs`
    `javiersc-nexus`
    `javiersc-readme-badges-generator`
    `kotlinx-binary-compatibility-validator`
}

docs {
    navigation {
        reports {
            codeCoverage.set(false)
        }
    }
}

readmeBadges {
    coverage.set(false)
}
