@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.plugin.api

import org.gradle.api.Named
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.declarative.dsl.model.annotations.Adding
import org.gradle.declarative.dsl.model.annotations.ElementFactoryName
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition

@ElementFactoryName("rule")
public interface SemverMapVersionsRuleDefinition : Named {

    public val priority: Property<Int>

    @get:Nested public val all: SemverMapVersionsMatches
    @get:Nested public val any: SemverMapVersionsMatches
    @get:Nested public val none: SemverMapVersionsMatches

    @Suppress("TooManyFunctions")
    public interface SemverMapVersionsMatches {

        @get:Nested @get:HiddenInDefinition public val conditions: Conditions
        @get:HiddenInDefinition public val contains: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val endsWith: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val environmentVariables: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val mappedContains: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val mappedEndsWith: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val mappedMetadata: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val mappedPatterns: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val mappedStartsWith: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val patterns: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val gradleProperties: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val startsWith: MapProperty<String, Boolean>

        @Adding
        public fun contains(element: String, ignoreCase: Boolean = false) {
            contains.put(element, ignoreCase)
        }

        @Adding
        public fun endsWith(element: String, ignoreCase: Boolean = false) {
            endsWith.put(element, ignoreCase)
        }

        @Adding
        public fun environmentVariableIsPresent(name: String, enabled: Boolean = true) {
            environmentVariables.put(name, enabled)
        }

        @Adding
        public fun metadataIsPresent(enabled: Boolean = true) {
            conditions.metadataIsPresent.set(enabled)
        }

        @Adding
        public fun mappedCommits(value: Int) {
            conditions.mappedCommits.set(value)
        }

        @Adding
        public fun mappedCommitsIsPresent(enabled: Boolean = true) {
            conditions.mappedCommitsIsPresent.set(enabled)
        }

        @Adding
        public fun mappedContains(element: String, ignoreCase: Boolean = false) {
            mappedContains.put(element, ignoreCase)
        }

        @Adding
        public fun mappedEndsWith(element: String, ignoreCase: Boolean = false) {
            mappedEndsWith.put(element, ignoreCase)
        }

        @Adding
        public fun mappedHash(value: String, ignoreCase: Boolean = false) {
            conditions.mappedHash.set(value)
            conditions.mappedHashIgnoreCase.set(ignoreCase)
        }

        @Adding
        public fun mappedHashIsPresent(enabled: Boolean = true) {
            conditions.mappedHashIsPresent.set(enabled)
        }

        @Adding
        public fun mappedMajor(value: Int) {
            conditions.mappedMajor.set(value)
        }

        @Adding
        public fun mappedMajorIsPresent(enabled: Boolean = true) {
            conditions.mappedMajorIsPresent.set(enabled)
        }

        @Adding
        public fun mappedMetadata(value: String, ignoreCase: Boolean = false) {
            mappedMetadata.put(value, ignoreCase)
        }

        @Adding
        public fun mappedMetadataIsPresent(enabled: Boolean = true) {
            conditions.mappedMetadataIsPresent.set(enabled)
        }

        @Adding
        public fun mappedMinor(value: Int) {
            conditions.mappedMinor.set(value)
        }

        @Adding
        public fun mappedMinorIsPresent(enabled: Boolean = true) {
            conditions.mappedMinorIsPresent.set(enabled)
        }

        @Adding
        public fun mappedPatch(value: Int) {
            conditions.mappedPatch.set(value)
        }

        @Adding
        public fun mappedPatchIsPresent(enabled: Boolean = true) {
            conditions.mappedPatchIsPresent.set(enabled)
        }

        @Adding
        public fun mappedPattern(pattern: String, ignoreCase: Boolean = false) {
            mappedPatterns.put(pattern, ignoreCase)
        }

        @Adding
        public fun mappedStageName(value: String, ignoreCase: Boolean = false) {
            conditions.mappedStageName.set(value)
            conditions.mappedStageNameIgnoreCase.set(ignoreCase)
        }

        @Adding
        public fun mappedStageIsPresent(enabled: Boolean = true) {
            conditions.mappedStageIsPresent.set(enabled)
        }

        @Adding
        public fun mappedStageNameIsPresent(enabled: Boolean = true) {
            conditions.mappedStageNameIsPresent.set(enabled)
        }

        @Adding
        public fun mappedStageNumber(value: Int) {
            conditions.mappedStageNumber.set(value)
        }

        @Adding
        public fun mappedStageNumberIsPresent(enabled: Boolean = true) {
            conditions.mappedStageNumberIsPresent.set(enabled)
        }

        @Adding
        public fun mappedStartsWith(element: String, ignoreCase: Boolean = false) {
            mappedStartsWith.put(element, ignoreCase)
        }

        @Adding
        public fun pattern(pattern: String, ignoreCase: Boolean = false) {
            patterns.put(pattern, ignoreCase)
        }

        @Adding
        public fun gradlePropertyIsPresent(name: String, enabled: Boolean = true) {
            gradleProperties.put(name, enabled)
        }

        @Adding
        public fun requestedTagPrefix(enabled: Boolean = true) {
            conditions.requestedTagPrefix.set(enabled)
        }

        @Adding
        public fun startsWith(element: String, ignoreCase: Boolean = false) {
            startsWith.put(element, ignoreCase)
        }

        public interface Conditions {
            public val mappedCommits: Property<Int>
            public val mappedCommitsIsPresent: Property<Boolean>
            public val mappedHash: Property<String>
            public val mappedHashIgnoreCase: Property<Boolean>
            public val mappedHashIsPresent: Property<Boolean>
            public val mappedMajor: Property<Int>
            public val mappedMajorIsPresent: Property<Boolean>
            public val mappedMetadataIsPresent: Property<Boolean>
            public val mappedMinor: Property<Int>
            public val mappedMinorIsPresent: Property<Boolean>
            public val mappedPatch: Property<Int>
            public val mappedPatchIsPresent: Property<Boolean>
            public val mappedStageIsPresent: Property<Boolean>
            public val mappedStageName: Property<String>
            public val mappedStageNameIgnoreCase: Property<Boolean>
            public val mappedStageNameIsPresent: Property<Boolean>
            public val mappedStageNumber: Property<Int>
            public val mappedStageNumberIsPresent: Property<Boolean>
            public val metadataIsPresent: Property<Boolean>
            public val requestedTagPrefix: Property<Boolean>
        }
    }
}
