---
name: semver-ecosystem-integration
description: Create and maintain Semver integrations with external ecosystem plugins. Use when adding or changing modules under semver-ecosystem-integrations, wiring semver-features-plugin to another ecosystem plugin, adapting DCL feature bridges, plugin-under-test dependencies, snapshot publication order, or circular dependency boundaries.
---

# Semver Ecosystem Integration

## Goal

Semver owns Semver behavior. Ecosystem plugins own their DSL surface and may apply
`com.javiersc.semver.features`, but the bridge that attaches Semver to that ecosystem lives in
Semver.

Use this split to avoid cycles:

- ecosystem plugin: applies Semver features plugin by ID when it wants Semver available
- Semver integration module: reads ecosystem API/model types and wires Semver feature behavior
- tests: may apply both plugins and add plugin markers to plugin-under-test classpaths

## Module Pattern

Create one module per ecosystem bridge:

```text
semver-ecosystem-integrations/semver-ecosystem-<ecosystem>-integration
```

Module contents:

- integration `ApplyAction` or registration code that attaches Semver to external ecosystem model
- optional internal settings/project plugin only when tests need a private fixture plugin
- functional fixture proving Semver can be configured through the ecosystem DSL

Then add the integration module to `semver-features-plugin` main dependencies.

## Dependency Rules

- Depend on ecosystem API/model artifacts, not the ecosystem Gradle plugin marker.
- Prefer concrete published API modules over full plugin implementation artifacts.
- Use version catalog library aliases for ecosystem API artifacts.
- Do not use plugin aliases or `.artifact` in `main` dependencies unless the artifact is explicitly
  a public API jar and not a Gradle plugin marker.
- Plugin marker artifacts may appear in `pluginUnderTestDependencies(...)` for tests only.
- Avoid cycles: if ecosystem plugin depends on Semver features, Semver runtime must not depend on
  that ecosystem plugin runtime/marker.

## Integration Steps

1. Identify ecosystem API/model types needed by Semver.
2. Add catalog library aliases for those API artifacts.
3. Create `semver-ecosystem-<ecosystem>-integration`.
4. Implement bridge logic using ecosystem definitions/build models and Semver feature API.
5. Add integration module as `implementation(...)` in `semver-features-plugin`.
6. Add functional test fixture applying both:
   - `com.javiersc.semver.features`
   - ecosystem plugin ID
7. Validate resolution and builds.

## Snapshot Workflow

When Semver depends on a newly published ecosystem API snapshot:

1. Ecosystem repo publishes its API/model artifacts first.
2. Validate Semver compile classpath:

```shell
./gradlew :semver-ecosystem-integrations:semver-ecosystem-<ecosystem>-integration:dependencies --configuration compileClasspath --refresh-dependencies
```

3. Run:

```shell
./gradlew fixChecks
./gradlew :semver-features-plugin:build
./gradlew :semver-ecosystem-integrations:semver-ecosystem-<ecosystem>-integration:build
```

4. Publish Semver snapshot.
5. Return to ecosystem repo and validate its plugin-under-test classpath/build.

Use `--refresh-dependencies` only after a snapshot was actually published or when diagnosing stale
snapshot timestamps.

## Hubdle Case

Hubdle integration uses:

- `com.javiersc.hubdle:hubdle-ecosystem-api`
- `com.javiersc.hubdle:hubdle-ecosystem-feature-versioning`

Hubdle must not implement Semver DCL internals. Hubdle ecosystem applies
`com.javiersc.semver.features`; Semver owns `hubdle { versioning { semver { ... } } }` bridge.

Do not depend on:

```text
com.javiersc.hubdle.ecosystem:com.javiersc.hubdle.ecosystem.gradle.plugin
```

from Semver runtime/main variants.

## Known Failure Modes

- Ecosystem build fails resolving its own plugin marker through Semver: Semver published a runtime
  dependency on ecosystem plugin marker. Replace with API/model artifact deps and publish new Semver
  snapshot.
- Ecosystem build fails resolving API/model artifacts from Semver transitive deps: ecosystem repo
  repository filters do not allow that artifact group in snapshots repo.
- Semver integration compile fails resolving ecosystem API artifacts: ecosystem snapshot was not
  published yet, catalog alias points to old artifact name, or repository content filter excludes
  the group.

## Validation

Always run `fixChecks` before module builds. Build affected modules unless the change crosses
published plugin boundaries.
