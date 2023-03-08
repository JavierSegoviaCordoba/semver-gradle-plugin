package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.DefaultTagPrefix
import com.javiersc.semver.gradle.plugin.internal.git.GitCache
import java.io.File
import javax.inject.Inject
import org.gradle.api.Project
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
    internal val rootDir: File,
) {

    public val tagPrefix: Property<String> = objects.property<String>().convention(DefaultTagPrefix)

    public val commitsMaxCount: Property<Int> = objects.property<Int>().convention(-1)

    public val commits: Provider<List<Commit>> =
        providers.provider { GitCache(rootDir, commitsMaxCount).commitsInTheCurrentBranchPublicApi }

    public companion object {

        public const val ExtensionName: String = "semver"

        internal fun register(project: Project) {
            project.extensions.create<SemverExtension>(ExtensionName, project.rootDir)
        }
    }
}

internal val Project.semverExtension: SemverExtension
    get() = extensions.getByType()
