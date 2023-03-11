# Single project

## 0. Initial repo state

#### Modules:

- Root project

#### Commits

- Initial commit: (hash: `14d9406b`)
- Add config (hash: `cd48c99a`)
- Add plugin (hash: `7f68e730`)

#### Repo state

- Repo is not clean

## 1. Run `./gradlew assemble`

- Base version when there is no tag is `0.1.0`

```text
semver for the root project: 0.1.0.0+7f68e730-DIRTY
                             -----> base version (0.1.0)
                             ------> numbers of commits (0)
                             ----------------> DIRTY
```

## 2. Create a new file and run `./gradlew assemble`

Version should be the same than the previous one

## 3. Add the new file to git, commit it, and run `./gradlew createSemverTag`

- Promotes the current state to be a new version. A new tag is created with the value `v0.1.0`

```text
Created new semver tag: v0.1.0
```

## 4. Run `./gradlew assemble`

- Because the latest commit in the branch has the tag v.1.0, the project version is `0.1.0`

```text
semver: 0.1.0
```

## 5. Create, add, commit a new file and run `./gradlew assemble`

- Because the git status is clean, instead of using a `DIRTY` suffix, it uses the latest commit
  hash.

```text
semver: 0.1.0.1+26f0484
        -----> base version (0.1.0)
        ------> numbers of commits (1)
        --------> hash (26f0484)
```

## 6. Run `./gradlew createSemverTag`

- Promote the current state to a new version.
- Since there is a previous tag (`v0.1.0`), `createSemverTag` resolves automatically the next
  version. The algorithm is simple:
    - If previous tag is final, the new tag increases the `patch`.
    - If previous tag is non-final, the new tag increases the `num` and it keeps the previous stage.

```text
Created new semver tag: v0.1.1
```

## 7. Create and add to git a new file, then run `./gradlew createSemverTag "-Psemver.stage=alpha"`

- Promote the current state to be a new alpha version.

```text
Created new semver tag: v0.1.2-alpha.1
```

## 8. Run `./gradlew createSemverTag "-Psemver.stage=beta"`

- Without changing the state, promote the current state to be a new beta version.

```text
Created new semver tag: v0.1.2-beta.1
```

## 9. Create and add to git a new file, then run `./gradlew createSemverTag "-Psemver.stage=final"`

- Without changing the state, promote the current state to be a final version.

```text
Created new semver tag: v0.1.2
```

## 10. Run `./gradlew createSemverTag "-Psemver.stage=final" "-Psemver.scope=major`

- Without changing the state, promote the current state to be the `1.0.0` version.

## 11. Run `./gradlew publishToMavenLocal "-Psemver.stage=snapshot"`

- A common approach is publishing the artifacts to the snapshot repository with each commit merged
  to some branch.
- Without changing the state and without creating a new tag, the project uses the next patched
  version by just submitting the snapshot scope.

```text
semver: 1.0.1-SNAPSHOT
```
