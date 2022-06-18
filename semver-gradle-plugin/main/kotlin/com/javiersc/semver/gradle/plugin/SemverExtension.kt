package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.DefaultTagPrefix
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property

public abstract class SemverExtension @Inject constructor(objects: ObjectFactory) {

    public val tagPrefix: Property<String> = objects.property<String>().convention(DefaultTagPrefix)

    public companion object {

        public const val extensionName: String = "semver"

        internal fun register(project: Project): SemverExtension =
            project.extensions.create(extensionName)
    }
}

internal val Project.semverExtension: SemverExtension
    get() = extensions.getByType()
