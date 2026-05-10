package com.javiersc.semver.declarative.gradle.plugin

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
        @get:HiddenInDefinition public val patterns: MapProperty<String, Boolean>
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
        public fun metadataIsPresent(enabled: Boolean = true) {
            conditions.metadataIsPresent.set(enabled)
        }

        @Adding
        public fun pattern(pattern: String, ignoreCase: Boolean = false) {
            patterns.put(pattern, ignoreCase)
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
            public val metadataIsPresent: Property<Boolean>
            public val requestedTagPrefix: Property<Boolean>
        }
    }
}
