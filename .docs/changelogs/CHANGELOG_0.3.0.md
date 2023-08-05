# Changelog

## [0.3.0-alpha.5] - 2022-11-04

### Added

- `semver.project.tagPrefix` Gradle Project property to set tag prefix instead of using the property
  from the extension (`SemverExtension::tagPrefix`). If both are set, the first one has preference.

### Updated

- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.2.0-alpha.43`
- `org.eclipse.jgit:org.eclipse.jgit -> 6.3.0.202209071007-r`
- `gradle -> 7.5.1`

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

[0.3.0-alpha.5]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.4...0.3.0-alpha.5

[0.3.0-alpha.4]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.3...0.3.0-alpha.4

[0.3.0-alpha.3]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.2...0.3.0-alpha.3

[0.3.0-alpha.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.1...0.3.0-alpha.2

[0.3.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.2.0-alpha.2...0.3.0-alpha.1
