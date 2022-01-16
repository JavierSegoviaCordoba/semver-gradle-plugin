# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Updated

- `com.javiersc.gradle-plugins:all-plugins -> 0.1.0-rc.40`
- `com.javiersc.kotlin:kotlin-stdlib -> 0.1.0-alpha.4`
- `gradle -> 7.3.3`
- `org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.6.0`
- `io.kotest:kotest-assertions-core -> 5.0.3`

## [0.1.0-alpha.10] - 2021-12-18

### Fixed

- crash when the project has no commits

### Updated

- `com.javiersc.semver:semver-core -> 0.1.0-beta.10`
- `gradle -> 7.3.2`
- `org.jetbrains.kotlin:kotlin-gradle-plugin -> 1.6.10`

## [0.1.0-alpha.9] - 2021-12-13

### Fixed

- pushing two tags at same time crashes

### Updated

- `com.javiersc.gradle-plugins:all-plugins -> 0.1.0-rc.24`

## [0.1.0-alpha.8] - 2021-12-10

### Fixed

- `semver.checkClean`

### Updated

- `io.kotest:kotest-assertions-core -> 5.0.2`

## [0.1.0-alpha.7] - 2021-12-10

### Added

- `semver.checkClean` Gradle property to allow versions without timestamp on dirty repositories
- `pushSemverTag` can set a specific remote via `semver.remote` Gradle property

### Fixed

- `pushSemverTag`
- project can't sync if it is not a git repository

### Updated

- `com.javiersc.gradle-plugins:all-plugins -> 0.1.0-rc.22`
- `gradle -> 7.3.1`

## [0.1.0-alpha.6] - 2021-11-30

### Updated

- `io.kotest:kotest-assertions-core -> 5.0.1`
- `com.javiersc.gradle-plugins:all-plugins -> 0.1.0-rc.19`
- `com.javiersc.semver:semver-core -> 0.1.0-beta.8`
- `com.javiersc.kotlin:kotlin-stdlib -> 0.1.0-alpha.3`
- `org.eclipse.jgit:org.eclipse.jgit -> 6.0.0.202111291000-r`

## [0.1.0-alpha.5] - 2021-11-29

### Added

- Java 8 support

### Updated

- `com.javiersc.gradle-plugins:all-plugins -> 0.1.0-rc.17`

## [0.1.0-alpha.4] - 2021-11-25

### Fixed

- getting all tags instead of only version tags
- calculated version

### Updated

- `io.kotest:kotest-assertions-core -> 5.0.0`
- `com.javiersc.gradle-plugins:all-plugins -> 0.1.0-rc.12`

## [0.1.0-alpha.3] - 2021-11-18

### Added

- `semver.tagPrefix` Gradle property

### Removed

- `SemVerExtension`

### Fixed

- not using the greatest version from tags

### Updated

- `org.jetbrains.kotlin:kotlin-gradle-plugin -> 1.6.0`

## [0.1.0-alpha.2] - 2021-11-13

- No changes

## [0.1.0-alpha.1] - 2021-11-12

- No changes

## [0.0.0] - 2021-10-15

- No changes
