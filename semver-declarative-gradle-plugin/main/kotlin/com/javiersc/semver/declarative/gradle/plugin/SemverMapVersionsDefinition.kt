@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.declarative.gradle.plugin

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.declarative.dsl.model.annotations.ElementFactoryName

@ElementFactoryName("mapVersion")
public interface SemverMapVersionsDefinition : Named, SemverMapVersionDefinition {

    public val rules: NamedDomainObjectContainer<SemverMapVersionsRuleDefinition>
}
