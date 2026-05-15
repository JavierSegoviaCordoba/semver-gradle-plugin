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

    public interface SemverMapVersionsMatches {

        @get:Nested @get:HiddenInDefinition public val conditions: Conditions
        @get:HiddenInDefinition public val contains: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val endsWith: MapProperty<String, Boolean>
        @get:HiddenInDefinition public val environmentVariables: MapProperty<String, Boolean>
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
        public fun mappedCommitsIsPresent(enabled: Boolean = true) {
            conditions.mappedCommitsIsPresent.set(enabled)
        }

        @Adding
        public fun mappedHashIsPresent(enabled: Boolean = true) {
            conditions.mappedHashIsPresent.set(enabled)
        }

        @Adding
        public fun mappedMajorIsPresent(enabled: Boolean = true) {
            conditions.mappedMajorIsPresent.set(enabled)
        }

        @Adding
        public fun mappedMetadataIsPresent(enabled: Boolean = true) {
            conditions.mappedMetadataIsPresent.set(enabled)
        }

        @Adding
        public fun mappedMinorIsPresent(enabled: Boolean = true) {
            conditions.mappedMinorIsPresent.set(enabled)
        }

        @Adding
        public fun mappedPatchIsPresent(enabled: Boolean = true) {
            conditions.mappedPatchIsPresent.set(enabled)
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
        public fun mappedStageNumberIsPresent(enabled: Boolean = true) {
            conditions.mappedStageNumberIsPresent.set(enabled)
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
            public val mappedCommitsIsPresent: Property<Boolean>
            public val mappedHashIsPresent: Property<Boolean>
            public val mappedMajorIsPresent: Property<Boolean>
            public val mappedMetadataIsPresent: Property<Boolean>
            public val mappedMinorIsPresent: Property<Boolean>
            public val mappedPatchIsPresent: Property<Boolean>
            public val mappedStageIsPresent: Property<Boolean>
            public val mappedStageNameIsPresent: Property<Boolean>
            public val mappedStageNumberIsPresent: Property<Boolean>
            public val metadataIsPresent: Property<Boolean>
            public val requestedTagPrefix: Property<Boolean>
        }
    }
}
