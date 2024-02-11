# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Updated

## [0.5.0] - 2024-02-10

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.6.2`
- `gradle -> 8.6`
- `org.eclipse.jgit:org.eclipse.jgit -> 6.5.0.202303070854-r`
- `com.javiersc.semver:semver-core -> 0.1.0-beta.13`

### Added

- `mapVersion` which expose `GitData` to `semver` extension
- `mapVersion` to `semver` extension
- `version` to `semver` extension
- `map` function to `LazyVersion`
- follow Gradle version ordering
- settings plugin to apply semver plugin to all projects
- `gitDir` property to `SemverExtension`
- `printSemver` task depends on `prepareKotlinIdeaImport` task
- `commits: Provider<Commit>` to `SemverExtension`
- `commitsMaxCount: Provider<Int>` to `SemverExtension`
- `semver.commitsMaxCount` property

### Fixed

- GitHub output and environment variables in the `printSemver` task
- the stage `SNAPSHOT` is not appended at the end of the version in all cases
- `metadata` doesn't allow `.`, `-`, or `_` characters
- multiple regexes invalidating valid versions
- default logger uses `LIFECYCLE` instead of `QUIET`

### Removed

- `LazyVersion`

### Changed

- `Version` to `GradleVersion`
- plugin id from `com.javiersc.semver.gradle.plugin` to `com.javiersc.semver`
- `com.javiersc.semver:semver-core` dependency from `implementation` to `api`

[Unreleased]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0...HEAD

[0.5.0]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.6...0.5.0
