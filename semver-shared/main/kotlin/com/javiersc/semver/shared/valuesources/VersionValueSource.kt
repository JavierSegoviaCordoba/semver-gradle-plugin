package com.javiersc.semver.shared.valuesources

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.shared.VersionMapper
import com.javiersc.semver.shared.internal.calculatedVersion
import com.javiersc.semver.shared.internal.checkCleanProperty
import com.javiersc.semver.shared.internal.checkVersionIsHigherOrSame
import com.javiersc.semver.shared.internal.commitsMaxCount
import com.javiersc.semver.shared.internal.git.GitCache
import com.javiersc.semver.shared.internal.git.GitRef
import com.javiersc.semver.shared.internal.projectTagPrefix
import com.javiersc.semver.shared.internal.scopeProperty
import com.javiersc.semver.shared.internal.semverWarningMessage
import com.javiersc.semver.shared.internal.stageProperty
import com.javiersc.semver.shared.internal.tagPrefixProperty
import com.javiersc.semver.shared.tasks.CreateSemverTagTask
import com.javiersc.semver.shared.tasks.PushSemverTagTask
import java.io.File
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.kotlin.dsl.of

public abstract class VersionValueSource : ValueSource<String, VersionValueSource.Params> {

    override fun obtain(): String = parameters.obtainSemverVersion { version: GradleVersion ->
        parameters.versionMapper.get().map(version)
    }

    public interface Params : SemverVersionValueSourceParams {
        public val versionMapper: Property<VersionMapper>
    }

    public companion object {

        public fun register(
            project: Project,
            versionMapper: Provider<VersionMapper>,
            gitDir: Provider<out Directory>,
            commitsMaxCount: Provider<Int>,
            tagPrefix: Provider<String>,
        ): Provider<String> =
            project.providers.of(VersionValueSource::class) { valueSourceSpec ->
                val parameters: Params = valueSourceSpec.parameters

                parameters.versionMapper.set(versionMapper)
                project.configureSemverVersionValueSourceParams(
                    parameters = parameters,
                    gitDir = gitDir,
                    commitsMaxCount = commitsMaxCount,
                    tagPrefix = tagPrefix,
                )
            }
    }
}

public interface SemverVersionValueSourceParams : ValueSourceParameters {
    public val gitDir: DirectoryProperty
    public val commitsMaxCount: Property<Int>
    public val tagPrefixProperty: Property<String>
    public val projectTagPrefix: Property<String>
    public val stageProperty: Property<String>
    public val scopeProperty: Property<String>
    public val creatingSemverTag: Property<Boolean>
    public val checkClean: Property<Boolean>
}

public fun SemverVersionValueSourceParams.obtainSemverVersion(
    mapVersion: (GradleVersion) -> String
): String {
    val isSamePrefix: Boolean = tagPrefixProperty.get() == projectTagPrefix.get()

    val gitDir: File? = gitDir.orNull?.asFile?.takeIf { it.exists() }
    if (gitDir == null) {
        semverWarningMessage("There is no git directory")
        return "[undefined]"
    }

    var cache: GitCache? = null
    fun cache(): GitCache {
        if (cache == null) {
            cache = GitCache(gitDir = gitDir, maxCount = commitsMaxCount)
        }
        return cache
    }

    val lastSemver: GradleVersion = cache().lastVersionInCurrentBranch(projectTagPrefix.get())
    val lastVersionInCurrentBranch: List<String> =
        cache().versionsInCurrentBranch(projectTagPrefix.get()).map(GradleVersion::toString)

    val lastVersionCommitInCurrentBranch: String? =
        cache().lastVersionCommitInCurrentBranch(projectTagPrefix.get())?.hash

    val version: String =
        calculatedVersion(
            stageProperty = stageProperty.orNull.takeIf { isSamePrefix },
            scopeProperty = scopeProperty.orNull.takeIf { isSamePrefix },
            isCreatingSemverTag = creatingSemverTag.get().takeIf { isSamePrefix } ?: false,
            lastSemverMajorInCurrentBranch = lastSemver.major,
            lastSemverMinorInCurrentBranch = lastSemver.minor,
            lastSemverPatchInCurrentBranch = lastSemver.patch,
            lastSemverStageInCurrentBranch = lastSemver.stage?.name,
            lastSemverNumInCurrentBranch = lastSemver.stage?.num,
            versionTagsInCurrentBranch = lastVersionInCurrentBranch,
            clean = cache().isClean,
            checkClean = checkClean.get(),
            lastCommitInCurrentBranch = cache().lastCommitInCurrentBranch?.hash,
            commitsInCurrentBranch = cache().commitsInCurrentBranch.map(GitRef.Commit::hash),
            headCommit = cache().headCommit.commit.hash,
            lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch,
        )

    val gradleVersion = GradleVersion(version)
    val mappedVersion: String = mapVersion(gradleVersion)

    checkVersionIsHigherOrSame(
        version = mappedVersion,
        lastVersionInCurrentBranch = lastSemver,
    )

    return mappedVersion
}

public fun Project.configureSemverVersionValueSourceParams(
    parameters: SemverVersionValueSourceParams,
    gitDir: Provider<out Directory>,
    commitsMaxCount: Provider<Int>,
    tagPrefix: Provider<String>,
) {
    parameters.gitDir.set(gitDir)
    val maxCount: Provider<Int> = project.commitsMaxCount.orElse(commitsMaxCount)
    parameters.commitsMaxCount.set(maxCount)
    parameters.projectTagPrefix.set(project.projectTagPrefix(tagPrefix).get())
    parameters.tagPrefixProperty.set(project.tagPrefixProperty.get())
    parameters.stageProperty.set(project.stageProperty.orNull)
    parameters.scopeProperty.set(project.scopeProperty.orNull)
    parameters.creatingSemverTag.set(project.isCreatingSemverTag)
    parameters.checkClean.set(project.checkCleanProperty.get())
}

private val Project.isCreatingSemverTag: Boolean
    get() =
        gradle.startParameter.taskNames.any { taskName: String ->
            taskName == CreateSemverTagTask.NAME || taskName == PushSemverTagTask.NAME
        }
