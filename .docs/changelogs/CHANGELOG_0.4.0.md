# Changelog

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

[0.4.0-alpha.1]: https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/compare/0.3.0-alpha.5...0.4.0-alpha.1
