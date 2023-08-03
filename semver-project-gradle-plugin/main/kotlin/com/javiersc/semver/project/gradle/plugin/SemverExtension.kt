package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.internal.DefaultTagPrefix
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import javax.inject.Inject
import org.gradle.api.Action
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
constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
) {

    public val isEnabled: Property<Boolean> = objects.property<Boolean>().convention(true)

    public abstract val gitDir: RegularFileProperty

    public val commits: Provider<List<Commit>> =
        providers.provider {
            GitCache(
                    gitDir = gitDir.get().asFile,
                    maxCount = commitsMaxCount,
                )
                .commitsInTheCurrentBranchPublicApi
        }

    public val commitsMaxCount: Property<Int> = objects.property<Int>().convention(-1)

    public val tagPrefix: Property<String> = objects.property<String>().convention(DefaultTagPrefix)

    internal abstract val calculatedVersion: Property<GradleVersion>

    public val version: Property<String> =
        objects
            .property<String>()
            .convention(providers.provider { "${calculatedVersion.orNull ?: "unspecified"}" })

    private var onMapVersion: Action<Unit>? = null

    public fun mapVersion(transform: (GradleVersion) -> String) {
        val mappedVersion: Provider<String> = calculatedVersion.map(transform)
        version.set(mappedVersion)
        onMapVersion?.execute(Unit)
    }

    internal fun onMapVersion(action: Action<Unit>) {
        onMapVersion = action
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
