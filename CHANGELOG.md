# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Updated

- `gradle -> 7.5.1`
- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.2.0-alpha.29`

## [0.3.0-alpha.4] - 2022-07-10

### Fixed

- `githubEnv*` properties are not `SNAKE_CASE` in `PrintSemverTask`

## [0.3.0-alpha.3] - 2022-07-10

### Fixed

- each `githubEnv*` property in `PrintSemverTask`

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.2.0-alpha.19`

## [0.3.0-alpha.2] - 2022-07-09

### Added

- `githubEnv` boolean option to `PrintSemverTask`
- `githubOutput` boolean option to `PrintSemverTask`

### Changed

- `printSemver` can't be cacheable

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.2.0-alpha.18`
- `org.jetbrains.kotlin:kotlin-gradle-plugin -> 1.7.10`
- `com.android.application:com.android.application.gradle.plugin -> 7.2.1`

## [0.3.0-alpha.1] - 2022-07-03

### Changed

- `printSemver` name to `printSemver`.
- `semverCreateTag` name to `createSemverTag`.
- `semverPushTag` name to `pushSemverTag`.

### Updated

- `org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.6.3`

## [0.2.0-alpha.2] - 2022-06-18

### Fixed

- high configuration time
- `semverCreateTag` does not belong to the `semver` group
- `semverPushTag` does not create the tag

## [0.2.0-alpha.1] - 2022-06-14

### Added

- `semverPrint` task which prints the version and generates the `build/semver/version.txt`.
- `semver` plugin extension which has `tagPrefix` to indicate the project tag prefix.
- configuration cache support
- project isolation support

### Changed

- the plugin must be applied individually to each project instead of only in the root project.
- `project.version` is now a `LazyVersion`, to get the string use `toString` method.
- `createSemverTag` name to `semverCreateTag`.
- `pushSemverTag` name to `semverPushTag`.
- `semver.tagPrefix` is no longer used to indicate a project version. Now it is used to filter which
  projects are going to bump the version based on it and the value in the property `tagPrefix` in
  the semver plugin extension.

### Removed

- applying the plugin to the root project only configures the root project and not all projects.
- `build/semver/version.txt` is no longer generated in configuration phase.
- the old way to change the version in multi-project builds (`-PprojectName:semver.scope=patch"`).

### Updated

- `org.jetbrains.kotlinx:binary-compatibility-validator -> 0.10.1`
- `org.eclipse.jgit:org.eclipse.jgit -> 6.2.0.202206071550-r`
- `com.javiersc.kotlin:kotlin-stdlib -> 0.1.0-alpha.5`
- `org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.6.2`
- `com.javiersc.gradle-plugins:all-plugins -> 0.1.0-rc.43`
- `io.kotest:kotest-assertions-core -> 5.3.0`
- `org.jetbrains.kotlin:kotlin-gradle-plugin -> 1.6.21`
- `gradle -> 7.4.2`

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
