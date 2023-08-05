# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

- the stage `SNAPSHOT` is not appended at the end of the version in all cases

### Updated

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

## [0.4.0-alpha.1] - 2023-01-04

### Added

- `Project.isAlpha: Provider<Boolean>` extension
- `Project.isNotAlpha: Provider<Boolean>` extension
- `Project.isBeta: Provider<Boolean>` extension
- `Project.isNotBeta: Provider<Boolean>` extension
- `Project.isDev: Provider<Boolean>` extension
- `Project.isNotDev: Provider<Boolean>` extension
- `Project.isRC: Provider<Boolean>` extension
- `Project.isNotRC: Provider<Boolean>` extension
- `Project.isSnapshot: Provider<Boolean>` extension
- `Project.isNotSnapshot: Provider<Boolean>` extension

### Changed

- `SNAPSHOT` is treated now as non-special stage (higher than `rc`)

### Updated

- `org.eclipse.jgit:org.eclipse.jgit -> 6.4.0.202211300538-r`
- `com.javiersc.hubdle:com.javiersc.hubdle.gradle.plugin -> 0.2.0-alpha.46`
- `gradle -> 7.6`

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

[Unreleased]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.4...HEAD

[0.5.0-rc.4]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.3...0.5.0-rc.4

[0.5.0-rc.3]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.2...0.5.0-rc.3

[0.5.0-rc.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-rc.1...0.5.0-rc.2

[0.5.0-rc.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-alpha.2...0.5.0-rc.1

[0.5.0-alpha.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.5.0-alpha.1...0.5.0-alpha.2

[0.5.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.4.0-alpha.1...0.5.0-alpha.1

[0.4.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.5...0.4.0-alpha.1

[0.3.0-alpha.5]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.4...0.3.0-alpha.5

[0.3.0-alpha.4]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.3...0.3.0-alpha.4

[0.3.0-alpha.3]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.2...0.3.0-alpha.3

[0.3.0-alpha.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.1...0.3.0-alpha.2

[0.3.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.2.0-alpha.2...0.3.0-alpha.1

[0.2.0-alpha.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.2.0-alpha.1...0.2.0-alpha.2

[0.2.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.10...0.2.0-alpha.1

[0.1.0-alpha.10]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.9...0.1.0-alpha.10

[0.1.0-alpha.9]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.8...0.1.0-alpha.9

[0.1.0-alpha.8]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.7...0.1.0-alpha.8

[0.1.0-alpha.7]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.6...0.1.0-alpha.7

[0.1.0-alpha.6]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.5...0.1.0-alpha.6

[0.1.0-alpha.5]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.4...0.1.0-alpha.5

[0.1.0-alpha.4]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.3...0.1.0-alpha.4

[0.1.0-alpha.3]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.2...0.1.0-alpha.3

[0.1.0-alpha.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.1...0.1.0-alpha.2

[0.1.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.0.0...0.1.0-alpha.1

[0.0.0]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/commits/0.0.0
