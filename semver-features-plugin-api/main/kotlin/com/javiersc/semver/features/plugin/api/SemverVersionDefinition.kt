@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.plugin.api

import java.io.Serializable
import javax.inject.Inject
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Nested
import org.gradle.declarative.dsl.model.annotations.Adding
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition

public interface SemverVersionDefinition : Serializable {

    @get:Nested public val major: Major
    @get:Nested public val minor: Minor
    @get:Nested public val patch: Patch
    @get:Nested public val stage: Stage
    @get:Nested public val commits: Commits
    @get:Nested public val hash: Hash
    @get:Nested public val metadata: Metadata

    @Adding public fun major(value: Int): Unit = major.value.set(value)

    @Adding public fun minor(value: Int): Unit = minor.value.set(value)

    @Adding public fun patch(value: Int): Unit = patch.value.set(value)

    @Adding public fun commits(value: Int): Unit = commits.value.set(value)

    @Adding public fun hash(value: String): Unit = hash.value.set(value)

    @Adding public fun metadata(value: String): Unit = metadata.value.set(value)

    public interface Major : Provisioner.IntProvisioner

    public interface Minor : Provisioner.IntProvisioner

    public interface Patch : Provisioner.IntProvisioner

    public interface Stage {
        @get:Nested public val name: Name
        @get:Nested public val number: Number

        @Adding public fun name(value: String): Unit = name.value.set(value)

        @Adding public fun number(value: Int): Unit = number.value.set(value)

        public interface Name : Provisioner.StringProvisioner

        public interface Number : Provisioner.IntProvisioner
    }

    public interface Commits : Provisioner.IntProvisioner

    public interface Hash : Provisioner.StringProvisioner

    public interface Metadata : Provisioner.StringProvisioner

    public interface Provisioner<T : Any> {

        @get:HiddenInDefinition @get:Inject public val providers: ProviderFactory

        public val value: Property<T>

        @Adding public fun gradleProperty(name: String)

        public interface IntProvisioner : Provisioner<Int> {

            @Adding
            override fun gradleProperty(name: String) {
                value.set(providers.gradleProperty(name).map(String::toInt))
            }
        }

        public interface StringProvisioner : Provisioner<String> {

            @Adding
            override fun gradleProperty(name: String) {
                value.set(providers.gradleProperty(name))
            }
        }
    }
}
