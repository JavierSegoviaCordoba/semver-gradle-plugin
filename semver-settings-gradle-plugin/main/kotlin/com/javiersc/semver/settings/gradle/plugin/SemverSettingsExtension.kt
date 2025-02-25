@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.javiersc.semver.settings.gradle.plugin

import com.javiersc.semver.project.gradle.plugin.VersionMapper
import com.javiersc.semver.project.gradle.plugin.internal.DefaultTagPrefix
import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.property

public abstract class SemverSettingsExtension @Inject constructor(objects: ObjectFactory) {

    public val isEnabled: Property<Boolean> = objects.property<Boolean>().convention(true)

    public abstract val gitDir: DirectoryProperty

    public val commitsMaxCount: Property<Int> = objects.property<Int>().convention(-1)

    public val tagPrefix: Property<String> = objects.property<String>().convention(DefaultTagPrefix)

    internal val versionMapper: Property<VersionMapper> =
        objects
            .property<VersionMapper>()
            .convention(VersionMapper { version -> version.toString() })

    public fun mapVersion(transform: VersionMapper) {
        versionMapper.set(transform)
    }

    public companion object {

        public const val ExtensionName: String = "semver"

        internal fun register(settings: Settings): SemverSettingsExtension {
            val semver = settings.extensions.create<SemverSettingsExtension>(ExtensionName)
            val gitDir = settings.providers.provider { settings.rootDir.resolve(".git") }
            semver.gitDir.fileProvider(gitDir)
            return semver
        }
    }
}
