# Changelog

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

[0.2.0-alpha.2]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.2.0-alpha.1...0.2.0-alpha.2

[0.2.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.1.0-alpha.10...0.2.0-alpha.1
