# One project example

## 0. Initial repo state

#### Modules:

- `library-one-a` uses prefix: a
- `library-two-b` uses prefix: b
- `library-three-b` uses prefix: b
- `library-four-b` uses prefix: b
- `library-five-b` uses prefix: b
- `library-six-c` uses prefix: c
- `library-seven-c` uses prefix: c
- `library-eight-c` uses prefix: c
- `library-nine` uses no prefix
- `library-ten` uses no prefix

#### Commits

- Initial commit: (hash: `14d9406b`)
- Add semver plugin to all projects (hash: `7f68e730`)

#### Last tags for all projects

All projects have the same version 1.0.0 so there is four tags with that

- a1.0.0
- b1.0.0
- c1.0.0
- 1.0.0

#### Repo state

- Repo is not clean

## 1. Run `./gradlew assemble`

- Timestamp (UTC)

```text
semver for library-one-a: a1.0.0.0+2021-11-11T14-22-03-207850300Z
                          -> tag prefix (a)
                          -----> base version (1.0.0)
                          ------> numbers of commits (0)
                          --------> timestamp (2021-11-11T14-22-03-207850300Z)

semver for library-two-b: b1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-three-b: b1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-four-b: b1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-five-b: b1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-six-c: c1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-seven-c: c1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-eight-c: c1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-nine: 1.0.0.0+2021-11-11T14-22-03-207850300Z
semver for library-ten: 1.0.0.0+2021-11-11T14-22-03-207850300Z
```

## 2. Create a new file in `library-one-a`, then run `./gradlew assemble`

Version should be similar to the previous one (only timestamp changes)

## 3. Add the new file to git and commit it, then run `./gradlew semverCreateTag "-Psemver.tagPrefix=a`

- Promotes the current state to be a new version for the projects which are using as tag prefix the
  value `a`.
- As we haven't passed as argument the property `semver.scope` with any valor, we are indeed
  doing `-Psemver.scope=auto`, which in this case will increase the `patch`.
- Since there is a previous tag (`a1.0.0`), `semver.scope=auto` resolves automatically the next
  version. The algorithm is simple:
    - If previous tag is final, the new tag increases the `patch`.
    - If previous tag is non-final, the new tag increases the `num` and it keeps the previous stage.

```text
Created new semver tag: a1.0.1
```

## 4. Run `./gradlew assemble`

- Because the latest commit in the branch has the tag a1.0.1, and the repo is clean, the project
  version for projects which uses the tag prefix `a` is `1.0.1`

```text
semver for library-one-a: a1.0.1
                          -> tag prefix (a)
                          -----> version (1.0.1)

semver for library-two-b: b1.0.0.1+26f0484
                          -> tag prefix (b)
                          -----> version (1.0.0)
                          ------> numbers of commits (1)
                          --------> hash (26f0484)

semver for library-three-b: b1.0.0.1+26f0484
semver for library-four-b: b1.0.0.1+26f0484
semver for library-five-b: b1.0.0.1+26f0484
semver for library-six-c: c1.0.0.1+26f0484
semver for library-seven-c: c1.0.0.1+26f0484
semver for library-eight-c: c1.0.0.1+26f0484
semver for library-nine: 1.0.0.1+26f0484
semver for library-ten: 1.0.0.1+26f0484
```

## 5. Create, add and commit a new file in `library-one-a`, then run `./gradlew assemble`

- Because the git status is clean, instead of using a timestamp, it uses the latest commit hash.

```text
semver for library-one-a: a1.0.1+76f0424
semver for library-two-b: b1.0.0.2+76f0424
semver for library-three-b: b1.0.0.2+76f0424
semver for library-four-b: b1.0.0.2+76f0424
semver for library-five-b: b1.0.0.2+76f0424
semver for library-six-c: c1.0.0.2+76f0424
semver for library-seven-c: c1.0.0.2+76f0424
semver for library-eight-c: c1.0.0.2+76f0424
semver for library-nine: 1.0.0.2+76f0424
semver for library-ten: 1.0.0.2+76f0424
```

## 6. Run `./gradlew semverCreateTag "-Psemver.tagPrefix=a"`

- Promote the current state to a new version.
- Since there is a previous tag (`v1.0.2`), `semverCreateTag` resolves automatically the next
  version. The algorithm is simple:
    - If previous tag is final, the new tag increases the `patch`.
    - If previous tag is non-final, the new tag increases the `num` and it keeps the previous stage.

```text
semver for library-one-a: a1.0.2
semver for library-two-b: b1.0.0.2+76f0424
semver for library-three-b: b1.0.0.2+76f0424
semver for library-four-b: b1.0.0.2+76f0424
semver for library-five-b: b1.0.0.2+76f0424
semver for library-six-c: c1.0.0.2+76f0424
semver for library-seven-c: c1.0.0.2+76f0424
semver for library-eight-c: c1.0.0.2+76f0424
semver for library-nine: 1.0.0.2+76f0424
semver for library-ten: 1.0.0.2+76f0424
```

## 7. Create, add to git and commit a new file in `library-one-a`, then run `./gradlew semverCreateTag "-Psemver.stage=alpha" "-Psemver.tagPrefix=a"`

- Promote the current state to be a new alpha version for the projects with tag prefix `a`.

```text
semver for library-one-a: a1.0.3-alpha.1
semver for library-two-b: b1.0.0.3+58u03s9
semver for library-three-b: b1.0.0.3+58u03s9
semver for library-four-b: b1.0.0.3+58u03s9
semver for library-five-b: b1.0.0.3+58u03s9
semver for library-six-c: c1.0.0.3+58u03s9
semver for library-seven-c: c1.0.0.3+58u03s9
semver for library-eight-c: c1.0.0.3+58u03s9
semver for library-nine: 1.0.0.3+58u03s9
semver for library-ten: 1.0.0.3+58u03s9
```

## 8. Run `./gradlew semverCreateTag "-Psemver.stage=beta" "-Psemver.tagPrefix=a"`

- Without changing the state, promote the current state to be a new beta version for the projects
  with tag prefix `a`.

```text
semver for library-one-a: a1.0.3-beta.1
semver for library-two-b: b1.0.0.3+58u03s9
semver for library-three-b: b1.0.0.3+58u03s9
semver for library-four-b: b1.0.0.3+58u03s9
semver for library-five-b: b1.0.0.3+58u03s9
semver for library-six-c: c1.0.0.3+58u03s9
semver for library-seven-c: c1.0.0.3+58u03s9
semver for library-eight-c: c1.0.0.3+58u03s9
semver for library-nine: 1.0.0.3+58u03s9
semver for library-ten: 1.0.0.3+58u03s9
```

## 9. Create, add to git and commit a new file in `library-one-a`, then run `./gradlew semverCreateTag "-Psemver.stage=final" "-Psemver.tagPrefix=a"`

- Without changing the state, promote the current state to be a final version for the projects with
  tag prefix `a`.

```text
semver for library-one-a: a1.0.3
semver for library-two-b: b1.0.0.4+1as03k5
semver for library-three-b: b1.0.0.4+1as03k5
semver for library-four-b: b1.0.0.4+1as03k5
semver for library-five-b: b1.0.0.4+1as03k5
semver for library-six-c: c1.0.0.4+1as03k5
semver for library-seven-c: c1.0.0.4+1as03k5
semver for library-eight-c: c1.0.0.4+1as03k5
semver for library-nine: 1.0.0.4+1as03k5
semver for library-ten: 1.0.0.4+1as03k5
```

## 10. Run `./gradlew semverCreateTag "-Psemver.stage=final" "-Psemver.scope=major "-Psemver.tagPrefix=a"`

- Without changing the state, promote the current state to be the `2.0.0` version for the projects
  with tag prefix `a`.

```text
semver for library-one-a: a2.0.0
semver for library-two-b: b1.0.0.4+1as03k5
semver for library-three-b: b1.0.0.4+1as03k5
semver for library-four-b: b1.0.0.4+1as03k5
semver for library-five-b: b1.0.0.4+1as03k5
semver for library-six-c: c1.0.0.4+1as03k5
semver for library-seven-c: c1.0.0.4+1as03k5
semver for library-eight-c: c1.0.0.4+1as03k5
semver for library-nine: 1.0.0.4+1as03k5
semver for library-ten: 1.0.0.4+1as03k5
```

## 11. Run `./gradlew publishToMavenLocal "-Psemver.stage=snapshot" "-Psemver.tagPrefix=a"`

- A common approach is publishing the artifacts to the snapshot repository with each commit merged
  to some branch.
- Without changing the state and without creating a new tag, the project uses the next patched
  version by just submitting the snapshot scope.

```text
semver for library-one-a: a2.0.1-SNAPSHOT
semver for library-two-b: b1.0.0.4+1as03k5
semver for library-three-b: b1.0.0.4+1as03k5
semver for library-four-b: b1.0.0.4+1as03k5
semver for library-five-b: b1.0.0.4+1as03k5
semver for library-six-c: c1.0.0.4+1as03k5
semver for library-seven-c: c1.0.0.4+1as03k5
semver for library-eight-c: c1.0.0.4+1as03k5
semver for library-nine: 1.0.0.4+1as03k5
semver for library-ten: 1.0.0.4+1as03k5
```

## 12. Run `./gradlew semverCreateTag "-Psemver.scope=minor" "-Psemver.tagPrefix=b"`

```text
semver for library-one-a: a2.0.0
semver for library-two-b: b1.1.0
semver for library-three-b: b1.1.0
semver for library-four-b: b1.1.0
semver for library-five-b: b1.1.0
semver for library-six-c: c1.0.0.4+1as03k5
semver for library-seven-c: c1.0.0.4+1as03k5
semver for library-eight-c: c1.0.0.4+1as03k5
semver for library-nine: 1.0.0.4+1as03k5
semver for library-ten: 1.0.0.4+1as03k5
```

## 12. Run `./gradlew semverCreateTag "-Psemver.stage=rc" "-Psemver.tagPrefix=c"`

```text
semver for library-one-a: a2.0.0
semver for library-two-b: b1.1.0
semver for library-three-b: b1.1.0
semver for library-four-b: b1.1.0
semver for library-five-b: b1.1.0
semver for library-six-c: c1.0.1-rc.1
semver for library-seven-c: c1.0.1-rc.1
semver for library-eight-c: c1.0.1-rc.1
semver for library-nine: 1.0.0.4+1as03k5
semver for library-ten: 1.0.0.4+1as03k5
```

## 12. Run `./gradlew semverCreateTag "-Psemver.stage=dev"`

```text
semver for library-one-a: a2.0.0
semver for library-two-b: b1.1.0
semver for library-three-b: b1.1.0
semver for library-four-b: b1.1.0
semver for library-five-b: b1.1.0
semver for library-six-c: c1.0.1-rc.1
semver for library-seven-c: c1.0.1-rc.1
semver for library-eight-c: c1.0.1-rc.1
semver for library-nine: 1.0.1-dev.1
semver for library-ten: 1.0.1-dev.1
```
