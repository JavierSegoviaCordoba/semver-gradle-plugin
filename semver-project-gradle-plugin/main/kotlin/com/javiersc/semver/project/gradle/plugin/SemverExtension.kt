package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.SemverExtension.VersionAndGitMapper
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

    internal val versionMapper: Property<VersionMapper> =
        objects
            .property<VersionMapper>()
            .convention(VersionMapper { version -> version.toString() })

    internal val versionAndGitMapper: Property<VersionAndGitMapper> =
        objects
            .property<VersionAndGitMapper>()
            .convention(VersionAndGitMapper { version, _ -> version.toString() })

    public fun mapVersion(transform: VersionMapper) {
        versionMapper.set(transform)
    }

    public fun mapVersion(transform: VersionAndGitMapper) {
        versionAndGitMapper.set(transform)
    }

    public fun interface VersionMapper : Serializable {

        public fun map(version: GradleVersion): String
    }

    public fun interface VersionAndGitMapper : Serializable {

        public fun map(version: GradleVersion, gitData: GitData): String
    }

    /**
     * @property commit The commit information.
     * @property tag The tag information, if available.
     * @property branch The branch information.
     */
    public data class GitData(val commit: Commit, val tag: Tag?, val branch: Branch) {

        /**
         * @property message The commit message.
         * @property fullMessage The full commit message, including any additional details.
         * @property hash The unique hash that identifies the commit.
         */
        public data class Commit(val message: String, val fullMessage: String, val hash: String)

        /**
         * @property name The name of the tag.
         * @property refName The reference name of the tag.
         * @property commit The commit associated with the tag.
         */
        public data class Tag(val name: String, val refName: String, val commit: Commit)

        /**
         * @property name The name of the branch. Example: `main`.
         * @property refName The reference name of the branch. Example: `refs/heads/main`.
         * @property commits The list of commits in the branch.
         * @property tags The list of tags in the branch.
         */
        public data class Branch(
            val name: String,
            val refName: String,
            val commits: List<Commit>,
            val tags: List<Tag>,
        )
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
