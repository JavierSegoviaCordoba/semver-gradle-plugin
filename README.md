![Kotlin version](https://img.shields.io/badge/kotlin-1.5.31-blueviolet?logo=kotlin&logoColor=white)
[![MavenCentral](https://img.shields.io/maven-central/v/com.javiersc.semver/semver-gradle-plugin?label=MavenCentral)](https://repo1.maven.org/maven2/com/javiersc/semver/semver-gradle-plugin/)
[![Snapshot](https://img.shields.io/nexus/s/com.javiersc.semver/semver-gradle-plugin?server=https%3A%2F%2Foss.sonatype.org%2F&label=Snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/javiersc/semver/semver-gradle-plugin/)

[![Build](https://img.shields.io/github/workflow/status/JavierSegoviaCordoba/semver-gradle-plugin/build-kotlin?label=Build&logo=GitHub)](https://github.com/JavierSegoviaCordoba/semver-gradle-plugin/tree/main)
[![Quality](https://img.shields.io/sonar/quality_gate/JavierSegoviaCordoba_semver-gradle-plugin?label=Quality&logo=SonarCloud&logoColor=white&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=JavierSegoviaCordoba_semver-gradle-plugin)
[![Tech debt](https://img.shields.io/sonar/tech_debt/JavierSegoviaCordoba_semver-gradle-plugin?label=Tech%20debt&logo=SonarCloud&logoColor=white&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=JavierSegoviaCordoba_semver-gradle-plugin)

# SemVer Gradle Plugin

Set projects versions based on git tags and following semantic versioning.

Inspired on [Reckon](https://github.com/ajoberstar/reckon) but centered on supporting multi-project
versions and combine normal stages with `snapshot` stage.

### Download from MavenCentral

```kotlin
// buildSrc/build.gradle.kts

dependencies {
    implementation("com.javiersc.semver:semver-gradle-plugin:$version")
}
```

### Usage

There are two Gradle properties which the plugin uses to detect automatically the current version
based on the last tag in the current branch: `semver.stage` and `semver.scope`.

They can be set via CLI, for example:

```shell
./gradlew "-Psemver.stage=final" "-Psemver.scope=major"
```

Check [examples](examples) to understand easily how it works.

#### All projects share the same version

Set the plugin in the root project:

```kotlin
// build.gradle.kts
plugins {
    id("com.javiersc.semver.gradle.plugin")
}

semver {
    tagPrefix.set("v") // default "v"
}
```

#### Different version in a specific project

All projects use the version based on the tag prefix `v` except library, which is declaring the
plugin too with a different tag prefix, `w`.

```kotlin
// build.gradle.kts
plugins {
    id("com.javiersc.semver.gradle.plugin")
}

semver {
    tagPrefix.set("v")
}
```

```kotlin
// library/build.gradle.kts
plugins {
    id("com.javiersc.semver.gradle.plugin")
}

semver {
    tagPrefix.set("w")
}
```

#### Different version in all projects

Just apply the plugin in every project and set different `tagPrefix` for each one.

### Version types

#### Final

- Format: `<major>.<minor>.<patch>`
- Example: `1.0.0`

#### Significant

- Format: `<major>.<minor>.<patch>-<stage>.<num>`
- Example: `1.0.0-alpha.1`

#### Insignificant

- Format: `<major>.<minor>.<patch>-<stage>.<num>.<commits>+<hash or timestamp>`
- Examples:
    - `1.0.0.4+2021-11-11T14-22-03-207850300Z`
    - `1.0.0.4+26f0484`

#### Snapshot

- Format: `<major>.<minor>.<patch>-SNAPSHOT`
- Example: `1.0.0-SNAPSHOT`

### Stages

To change between stages, use the Gradle property `-Psemver.stage=<stage>`

The stage can be whatever word, except two reserved words: `auto`, `final` and `snapshot`.

For multi-project + multi-version configuration, it is possible to override the version of a
specific project which is applying the plugin via CLI, for example if the subproject is `library`:

```shell
./gradlew "-Plibrary:semver.stage=alpha"
````

```shell
# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.stage=alpha" # v1.0.0-alpha.2
./gradlew "-Psemver.stage=beta" # v1.0.0-beta.1
./gradlew "-Psemver.stage=rc" # v1.0.0-rc.1
./gradlew "-Psemver.stage=snapshot" # v1.0.1-SNAPSHOT (uses the next patch version)
./gradlew "-Psemver.stage=final" # v1.0.0

# Last tag = v1.0.0
./gradlew "-Psemver.stage=alpha" # v1.0.1-alpha.1
./gradlew "-Psemver.stage=beta" # v1.0.1-beta.1
./gradlew "-Psemver.stage=rc" # v1.0.1-rc.1
./gradlew "-Psemver.stage=snapshot" # v1.0.1-SNAPSHOT (still uses the same patch version)
./gradlew "-Psemver.stage=final" # v1.0.1
```

### Scopes

To change between scopes, use the Gradle property `-Psemver.scope=<scope>`

The scope has to be one of `major`, `minor`, `patch` or `auto`.

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

```shell
# Last tag = v1.0.0-alpha.1
./gradlew "-Psemver.stage=alpha" "-Psemver.scope=major" # v2.0.0-alpha.1
./gradlew "-Psemver.stage=beta" "-Psemver.scope=major" # v2.0.0-beta.1
./gradlew "-Psemver.stage=rc" "-Psemver.scope=major" # v2.0.0-rc.1
./gradlew "-Psemver.stage=final" "-Psemver.scope=major" # v2.0.0
./gradlew "-Psemver.stage=snapshot" "-Psemver.scope=major" # v2.0.0-SNAPSHOT

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
