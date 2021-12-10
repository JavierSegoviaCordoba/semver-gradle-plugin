# Module semver-gradle-plugin

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

Additionally, you can set a tag prefix, for example `v1.0.0` by setting `semver.tagPrefix=v` in the
root `gradle.properties` or in each project you want to use with a different tag.

The default tag prefix is an empty string, for example: `1.0.0`.

#### All projects share the same version

Set the plugin in the root project:

```kotlin
// build.gradle.kts
plugins {
    id("com.javiersc.semver.gradle.plugin")
}
```

Set a `semver.tagPrefix` if the default one is not enough (default is empty):

```properties
# gradle.properties
semver.tagPrefix=v
```

#### Different version in a specific project

All projects use the version based on the tag prefix `v` except library, which is declaring the
plugin too with a different tag prefix, `w`.

```kotlin
// build.gradle.kts
plugins {
    id("com.javiersc.semver.gradle.plugin")
}
```

```properties
# gradle.properties
semver.tagPrefix=v
```

```kotlin
// library/build.gradle.kts
plugins {
    id("com.javiersc.semver.gradle.plugin")
}
```

```properties
# library/gradle.properties
semver.tagPrefix=w
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

There are reserved stages that can be used to create certain versions:

- `final`: It creates a version without a suffix stage, for example, `1.0.1`.
- `auto`: It calculates automatically the next stage based on the previous stage.
- `snapshot`: It generate the next snapshot version, for example, `1.0.1-SNAPSHOT`.

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
./gradlew "-Psemver.stage=auto" # v1.0.0-alpha.2

# Last tag = v1.0.0
./gradlew "-Psemver.stage=alpha" # v1.0.1-alpha.1
./gradlew "-Psemver.stage=beta" # v1.0.1-beta.1
./gradlew "-Psemver.stage=rc" # v1.0.1-rc.1
./gradlew "-Psemver.stage=snapshot" # v1.0.1-SNAPSHOT (still uses the same patch version)
./gradlew "-Psemver.stage=final" # v1.0.1
./gradlew "-Psemver.stage=auto" # v1.0.1
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

There are two tasks, `createSemverTag` and `pushSemverTag`. First one just create the tag, meanwhile
the last one creates and pushes the tag to the remote. You can combine them with `semver` Gradle
properties to ensure the correct tag version is created and/or pushed.

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

### Set versions without timestamp on dirty repositories

By default, if the repository status is not clean, the version shows the timestamp but that can be
avoided by setting the Gradle property `semver.checkClean`.

For example, if the last tag is `1.0.0`, there are 23 commits between that tag and the last commit
and the repo is not clean:

```shell
./gradlew "-Psemver.stage=final" "-Psemver.scope=patch"

semver: 1.0.0.23+2021-12-09T23-46-33-217289300Z
```

```shell
./gradlew "-Psemver.stage=final" "-Psemver.scope=patch" "-Psemver.checkClean=false"
semver: 1.0.1
```

```shell
./gradlew "-Psemver.checkClean=false"
semver: 1.0.0.23+1a2cd5b2 # 1a2cd5b2 is the last commit hash
```
