package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.SemverExtension.VersionMapper
import com.javiersc.semver.project.gradle.plugin.internal.DefaultTagPrefix
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import java.io.Serializable
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property

public abstract class SemverExtension
@Inject
constructor(objects: ObjectFactory, providers: ProviderFactory) {

    public val isEnabled: Property<Boolean> = objects.property<Boolean>().convention(true)

    public abstract val gitDir: RegularFileProperty

    public val commits: Provider<List<Commit>> =
        providers.provider {
            GitCache(gitDir = gitDir.get().asFile, maxCount = commitsMaxCount)
                .commitsInTheCurrentBranchPublicApi
        }

    public val commitsMaxCount: Property<Int> = objects.property<Int>().convention(-1)

    public val tagPrefix: Property<String> = objects.property<String>().convention(DefaultTagPrefix)

    internal val versionMapper: Property<VersionMapper> =
        objects
            .property<VersionMapper>()
            .convention(VersionMapper { version -> version.toString() })

    public fun mapVersion(transform: VersionMapper) {
        versionMapper.set(transform)
    }

    public fun interface VersionMapper : Serializable {

        public fun map(version: GradleVersion): String
    }

    public companion object {

        public const val ExtensionName: String = "semver"

        internal fun register(project: Project) {
            project.extensions.create<SemverExtension>(ExtensionName)
            val gitDir = project.provider { project.rootDir.resolve(".git") }
            project.semverExtension.gitDir.fileProvider(gitDir)
        }
    }
}

internal val Project.semverExtension: SemverExtension
    get() = extensions.getByType()
