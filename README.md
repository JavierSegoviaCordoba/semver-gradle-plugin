![Kotlin version](https://img.shields.io/badge/kotlin-1.9.21-blueviolet?logo=kotlin&logoColor=white)
[![MavenCentral](https://img.shields.io/maven-central/v/com.javiersc.semver/semver-gradle-plugin?label=MavenCentral)](https://repo1.maven.org/maven2/com/javiersc/semver/semver-gradle-plugin/)
[![Snapshot](https://img.shields.io/nexus/s/com.javiersc.semver/semver-gradle-plugin?server=https%3A%2F%2Foss.sonatype.org%2F&label=Snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/javiersc/semver/semver-gradle-plugin/)

[![Build](https://img.shields.io/github/actions/workflow/status/JavierSegoviaCordoba/semver-gradle-plugin/build-kotlin.yaml?label=Build&logo=GitHub)](https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/tree/main)
[![Coverage](https://img.shields.io/sonar/coverage/com.javiersc.gradle:semver-gradle-plugin?label=Coverage&logo=SonarCloud&logoColor=white&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=com.javiersc.gradle:semver-gradle-plugin)
[![Quality](https://img.shields.io/sonar/quality_gate/com.javiersc.gradle:semver-gradle-plugin?label=Quality&logo=SonarCloud&logoColor=white&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=com.javiersc.gradle:semver-gradle-plugin)
[![Tech debt](https://img.shields.io/sonar/tech_debt/com.javiersc.gradle:semver-gradle-plugin?label=Tech%20debt&logo=SonarCloud&logoColor=white&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=com.javiersc.gradle:semver-gradle-plugin)

# Semver Gradle Plugin

Set projects versions based on git tags and following semantic versioning.

Inspired on [Reckon](https://github.com/ajoberstar/reckon) but centered on supporting multi-project
versions and combine normal stages with `snapshot` stage.

### Apply the plugin

In order to support configuration cache or project isolation the plugin must be applied to each
project or using the settings plugin. Avoid using `allprojects` or `subprojects`.

```kotlin
// build.gradle.kts
plugins {
    id("com.javiersc.semver") version "$version"
}
```

It is possible to apply the plugin to all projects if the plugin is applied in the `settings.gradle`
or `settings.gradle.kts` file:

```kotlin
// settings.gradle.kts
plugins {
    id("com.javiersc.semver") version "$version"
}
```

### Examples

Check [documented examples](.docs/docs/examples)
or [test examples](semver-project-gradle-plugin/testFunctional/resources/examples) to understand
easily how it works.

### Usage

There are three project properties which the plugin uses to detect automatically the current version
based on the last tag in the current branch: `semver.stage`, `semver.scope` and `semver.tagPrefix`.

They can be set via CLI, for example:

```shell
./gradlew "-Psemver.stage=final" "-Psemver.scope=major" "-Psemver.tagPrefix=v"
```

- `semver.stage` indicates the stage to be changed, for example, `alpha`
- `semver.scope` indicates the scope to be changed, for example, `patch`
- `semver.tagPrefix` is used to know which version is going to be changed based on the tag prefix,
  for example `v`. If the projects have different tag prefix, it is necessary to disambiguate which
  version is going to be bumped.

Default values:

|           | **default value** | **Optional** |
|-----------|-------------------|--------------|
| stage     | `auto`            | Yes*         |
| scope     | `auto`            | Yes*         |
| tagPrefix | `auto`            | Yes*         |

> Depends on the use case*

#### Plugin extension

```kotlin
semver {
    isEnabled.set(true)
    tagPrefix.set("")
    commitsMaxCount.set(-1)
    gitDir.set(rootDir.resolve(".git"))
}

tasks.register("printLastCommitHash") {
    doLast {
        println(semver.commits.get().last().hash)
    }
}
```

- Default values:

|                     | **default value**                                             |
|---------------------|---------------------------------------------------------------|
| **isEnabled**       | `true`                                                        |
| **tagPrefix**       | ` `, empty string                                             |
| **commitsMaxCount** | `-1`                                                          |
| **gitDir**          | `rootDir.resolve(".git")`                                     |
| **version**         | calculated version based on the last git tag and other inputs |

`tagPrefix` is used to asociate a project version with a tag prefix, and it allows having different
versions in multi-project builds.

An example can be setting the extension prefix to `v` in a specific project A and the last tags in
the last commit are: `v1.0.0` and `w3.0.1`. The project A version is `v1.0.0`. If a project B sets
the prefix to `w`, the project B version is `w3.0.1`.

In order to improve the performance on large repositories with a lot of commits, it is possible to
limit the number of commits to be checked via `commitsMaxCount`. By default, it is `-1` which means
that all commits are checked.

`semver` contains `commits: Provider<List<Commit>>` to get the commits in the current branch and the
associated tags to each one which can be useful in some use cases.

#### `semver.tagPrefix` project property via CLI or `gradle.properties` file

```shell
"-Psemver.tagPrefix=v"
```

```properties
semver.tagPrefix=v
```

If it is necessary to bump the project version, for example to `v3.0.2` from `v3.0.1`, but at same
time there are more project with different prefixes, the plugin needs to know which tag prefix is
going to be bumped, so `semver.tagPrefix` property is the solution to that problem.

To get it working:

```shell
./gradlew "-Psemver.scope=patch" "-Psemver.tagPrefix=v"
```

It is possible to set the project tag prefix via Gradle property if some third-party plugin requires
the version in configuration phase. This property is:

```properties
semver.project.tagPrefix=v
```

As it can be useful to change the number of commits to be checked, it is possible to set it via
properties too:

```properties
semver.commitsMaxCount=100
```

Or in the CLI:

```shell
./gradlew "-Psemver.commitsMaxCount=100"
```

##### Map the version

The `semver` extension has a `mapVersion` function which allows to map the version easily:

```kotlin
// if last tag is v3.0.1, and the Kotlin version is 1.9.0,
// the version will be `v3.0.1+1.9.0`
semver {
    tagPrefix.set("v")
    mapVersion { gradleVersion: GradleVersion ->
        val kotlinVersion: String = getKotlinPluginVersion()
        "${gradleVersion.copy(metadata = kotlinVersion)}"
    }
}
```

##### Override the version

If it possible to force an override of the version:

```kotlin
semver {
    version.set("1.0.0")
}
```

##### Additional notes

###### Empty tag prefix for all projects

> If all projects are not using a tag prefix, or in other words, the tag prefix is empty, both the
> property in the extension and the project property via CLI or `gradle.properties` file are
> irrelevant.

###### Same tag prefix for all projects

> If all projects share a tag prefix, it is easier to set it in the root project `gradle.properties`
> file instead of passing it constantly via CLI.

### Version types

The whole format can be:

```text
<major>.<minor>.<patch>[-<stage>.<num>][.<commits number>+<hash>][+<metadata>]
```

#### Final

- Format: `<major>.<minor>.<patch>`
- Example: `1.0.0`

#### Significant

- Format: `<major>.<minor>.<patch>[-<stage>.<num>]`
- Example: `1.0.0-alpha.1`

#### Insignificant

- Format:
    - Clean repository: `<major>.<minor>.<patch>[-<stage>.<num>][.<commits number>+<hash>]`
    - Dirty repository: `<major>.<minor>.<patch>[-<stage>.<num>][.<commits number>+<DIRTY>]`

- Examples:
    - `1.0.0.4+26f0484`
    - `1.0.0.4+DIRTY`

> It is used the `DIRTY` suffix instead of a timestamp in order to avoid issues with any Gradle
> cache.

#### Snapshot

- Format: `<major>.<minor>.<patch>-SNAPSHOT`
- Example: `1.0.0-SNAPSHOT`

### Stages

To change between stages, use the Gradle project property `-Psemver.stage=<stage>`

There are reserved stages that can be used to create certain versions:

- `final`: It creates a version without a suffix stage, for example, `1.0.1`.
- `auto`: It calculates automatically the next stage based on the previous stage.
- `snapshot`: It generate the next snapshot version, for example, `1.0.1-SNAPSHOT`.

```properties
# gradle.properties
semver.tagPrefix=v
```

```shell
# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.stage=alpha" # v1.0.0-alpha.2
./gradlew "-Psemver.stage=beta" # v1.0.0-beta.1
./gradlew "-Psemver.stage=rc" # v1.0.0-rc.1
./gradlew "-Psemver.stage=snapshot" # v1.0.1-SNAPSHOT (uses the next patch version)
./gradlew "-Psemver.stage=final" # v1.0.0
./gradlew "-Psemver.stage=auto" # v1.0.0-alpha.2

# Last tag = v1.0.0
./gradlew "-Psemver.stage=alpha" # v1.0.1-alpha.1
./gradlew "-Psemver.stage=beta" # v1.0.1-beta.1
./gradlew "-Psemver.stage=rc" # v1.0.1-rc.1
./gradlew "-Psemver.stage=snapshot" # v1.0.1-SNAPSHOT (still uses the same patch version)
./gradlew "-Psemver.stage=final" # v1.0.1
./gradlew "-Psemver.stage=auto" # v1.0.1
```

The stage order is based on the Gradle official rules, some samples are:

- If both are non-numeric, the parts are compared alphabetically, in a case-sensitive manner:
  `1.0.0-ALPHA.1` < `1.0.0-BETA.1` < `1.0.0-alpha.1` < `1.0.0-beta.1`.
- `dev` is considered lower than any non-numeric
  part: `1.0.0-dev.1` < `1.0.0-ALPHA.1` < `1.0.0-alpha.1` < `1.0.0-rc.1`.
- The strings `rc`, `snapshot`, `final`, `ga`, `release` and `sp` are considered higher than any
  other string part (sorted in this order): `1.0.0-zeta.1` < `1.0.0-rc.1` < `1.0.0-snapshot` <
  `1.0.0-ga.1` < `1.0.0-release.1` < `1.0.0-sp.1` < `1.0.0`.
- These particular values are NOT case-sensitive, as opposed to regular string parts and do not
  depend on the separator used around them: `1.0.0-RC.1` == `1.0.0-rc.1`.

Gradle's docs can be
found [here](https://docs.gradle.org/current/userguide/single_versions.html#version_ordering)

### Scopes

To change between scopes, use the Gradle property `-Psemver.scope=<scope>`

The scope has to be one of `major`, `minor`, `patch` or `auto`.

```properties
# gradle.properties
semver.tagPrefix=v
```

```shell
# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.scope=major" # v2.0.0
./gradlew "-Psemver.scope=minor" # v1.1.0
./gradlew "-Psemver.scope=patch" # v1.0.1
./gradlew "-Psemver.scope=auto" # v1.0.0-alpha.2 (uses the next num version)

# Last tag = v1.0.0
./gradlew "-Psemver.scope=major" # v2.0.0
./gradlew "-Psemver.scope=minor" # v1.1.0
./gradlew "-Psemver.scope=patch" # v1.0.1
./gradlew "-Psemver.scope=auto" # v1.0.1 (uses the next patch version)
```

### Combine stages and scopes

```properties
# gradle.properties
semver.tagPrefix=v
```

```shell
# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=major" # v2.0.0-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=major" # v2.0.0-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=major" # v2.0.0-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=major" # v2.0.0
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=major" # v2.0.0-SNAPSHOT
./gradlew "-Psemver.stage=auto" "-Psemver.scope=auto" # v1.0.0-alpha.2

# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=minor" # v1.1.0-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=minor" # v1.1.0-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=minor" # v1.1.0-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=minor" # v1.1.0
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=minor" # v1.1.0-SNAPSHOT

# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=patch" # v1.0.1-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=patch" # v1.0.1-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=patch" # v1.0.1-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=patch" # v1.0.1
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=patch" # v1.0.1-SNAPSHOT

# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=auto" # v1.0.0-alpha.2
./gradlew "-Psemver.stage=beta" "-Psemver.scope=auto" # v1.0.1-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=auto" # v1.0.1-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=auto" # v1.0.1
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=auto" # v1.0.1-SNAPSHOT

# Last tag = v1.0.0
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=major" # v2.0.0-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=major" # v2.0.0-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=major" # v2.0.0-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=major" # v2.0.0
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=major" # v2.0.0-SNAPSHOT
./gradlew "-Psemver.stage=auto" "-Psemver.scope=auto" # v1.0.1

# Last tag = v1.0.0
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=minor" # v1.1.0-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=minor" # v1.1.0-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=minor" # v1.1.0-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=minor" # v1.1.0
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=minor" # v1.1.0-SNAPSHOT

# Last tag = v1.0.0
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=patch" # v1.0.1-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=patch" # v1.0.1-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=patch" # v1.0.1-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=patch" # v1.0.1
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=patch" # v1.0.1-SNAPSHOT

# Last tag = v1.0.0
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=auto" # v1.0.1-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=auto" # v1.0.1-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=auto" # v1.0.1-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=auto" # v1.0.1
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=auto" # v1.0.1-SNAPSHOT
```

### Tasks

There are three tasks:

- `printSemver`: Prints the tag in CLI and create a file in `build/semver/version.txt` which has two
  lines; the version without the tag and the version including the tag.
- `createSemverTag`. Creates a git tag.
- `pushSemverTag`. Creates and pushes a git tag to the remote.

You can combine them with any `semver` project properties to ensure the correct tag version is
printed, created or pushed.

`pushSemverTag` can use a specific remote if the Gradle property `semver.remote` is set. If it is
not set, `origin` is used if it exists, if not, the first remote by name is used. If there is no
remote, the task fails.

Samples:

```shell
./gradlew createSemverTag

./gradlew createSemverTag "-Psemver.stage=alpha"

./gradlew pushSemverTag

./gradlew pushSemverTag "-Psemver.stage=alpha"
```

### Set versions without `DIRTY` suffix on dirty repositories

By default, if the repository status is not clean, the version shows the suffix `DIRTY` but that can
be avoided by setting the Gradle property `semver.checkClean`.

For example, if the last tag is `1.0.0`, there are 23 commits between that tag and the last commit
and the repo is not clean:

```shell
./gradlew "-Psemver.stage=final" "-Psemver.scope=patch"

semver: 1.0.0.23+DIRTY
```

```shell
./gradlew "-Psemver.stage=final" "-Psemver.scope=patch" "-Psemver.checkClean=false"
semver: 1.0.1
```

```shell
./gradlew "-Psemver.checkClean=false"
semver: 1.0.0.23+1a2cd5b2 # 1a2cd5b2 is the last commit hash
```

## License

```
Copyright 2022 Javier Segovia Córdoba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
