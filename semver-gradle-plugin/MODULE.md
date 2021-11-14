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
