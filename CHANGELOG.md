# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Updated

## [0.5.0-rc.6] - 2023-12-14

### Added

- `mapVersion` which expose `GitData` to `semver` extension

### Fixed

- GitHub output and environment variables in the `printSemver` task

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.5.0-beta.15`
- `gradle -> 8.5`

## [0.5.0-rc.5] - 2023-08-05

### Fixed

- the stage `SNAPSHOT` is not appended at the end of the version in all cases

## [0.5.0-rc.4] - 2023-08-04

### Fixed

- `metadata` doesn't allow `.`, `-`, or `_` characters

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.5.0-beta.6`

## [0.5.0-rc.3] - 2023-08-04

### Fixed

- multiple regexes invalidating valid versions

## [0.5.0-rc.2] - 2023-08-03

### Added

- `mapVersion` to `semver` extension
- `version` to `semver` extension

### Removed

- `LazyVersion`

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.5.0-beta.4`
- `gradle -> 8.2.1`

## [0.5.0-rc.1] - 2023-06-06

### Added

- `map` function to `LazyVersion`

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.5.0-alpha.26`
- `gradle -> 8.1.1`

## [0.5.0-alpha.2] - 2023-03-20

### Added

- follow Gradle version ordering

### Changed

- `Version` to `GradleVersion`

## [0.5.0-alpha.1] - 2023-03-12

### Added

- settings plugin to apply semver plugin to all projects
- `gitDir` property to `SemverExtension`
- `printSemver` task depends on `prepareKotlinIdeaImport` task
- `commits: Provider<Commit>` to `SemverExtension`
- `commitsMaxCount: Provider<Int>` to `SemverExtension`
- `semver.commitsMaxCount` property

### Changed

- plugin id from `com.javiersc.semver.gradle.plugin` to `com.javiersc.semver`
- `com.javiersc.semver:semver-core` dependency from `implementation` to `api`

### Fixed

- default logger uses `LIFECYCLE` instead of `QUIET`

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.5.0-alpha.6`
- `org.eclipse.jgit:org.eclipse.jgit -> 6.5.0.202303070854-r`
- `gradle -> 8.0.2`
- `com.javiersc.semver:semver-core -> 0.1.0-beta.13`

[Unreleased]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.6...HEAD

[0.5.0-rc.6]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.5...0.5.0-rc.6

[0.5.0-rc.5]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.4...0.5.0-rc.5

[0.5.0-rc.4]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.3...0.5.0-rc.4

[0.5.0-rc.3]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.2...0.5.0-rc.3

[0.5.0-rc.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.1...0.5.0-rc.2

[0.5.0-rc.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-alpha.2...0.5.0-rc.1

[0.5.0-alpha.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-alpha.1...0.5.0-alpha.2

[0.5.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/commits/0.5.0-alpha.1
