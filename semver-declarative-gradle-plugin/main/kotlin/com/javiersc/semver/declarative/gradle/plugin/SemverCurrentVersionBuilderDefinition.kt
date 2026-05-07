package com.javiersc.semver.declarative.gradle.plugin

import org.gradle.declarative.dsl.model.annotations.Builder

public interface SemverCurrentVersionBuilderDefinition : SemverCurrentVersionDefinition {

    @Builder
    public fun major(value: Int): SemverCurrentVersionDefinition = apply { major.set(value) }

    @Builder
    public fun minor(value: Int): SemverCurrentVersionDefinition = apply { minor.set(value) }

    @Builder
    public fun patch(value: Int): SemverCurrentVersionDefinition = apply { patch.set(value) }

    @Builder
    public fun stageName(value: String): SemverCurrentVersionDefinition = apply {
        stageName.set(value)
    }

    @Builder
    public fun stageNum(value: Int): SemverCurrentVersionDefinition = apply { stageNum.set(value) }

    @Builder
    public fun commits(value: Int): SemverCurrentVersionDefinition = apply { commits.set(value) }

    @Builder
    public fun hash(value: String): SemverCurrentVersionDefinition = apply { hash.set(value) }

    @Builder
    public fun metadata(value: String): SemverCurrentVersionDefinition = apply {
        metadata.set(value)
    }
}
