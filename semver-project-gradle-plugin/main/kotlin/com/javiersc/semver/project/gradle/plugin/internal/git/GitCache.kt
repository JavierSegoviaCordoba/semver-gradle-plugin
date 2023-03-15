package com.javiersc.semver.project.gradle.plugin.internal.git

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.internal.InitialVersion
import com.javiersc.semver.project.gradle.plugin.internal.semverWarningMessage
import com.javiersc.semver.project.gradle.plugin.internal.warningLastVersionIsNotHigherVersion
import com.javiersc.semver.project.gradle.plugin.services.hasNotCommits
import java.io.File
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.provider.Provider

private val checkedNotNullCache: GitCache
    get() =
        checkNotNull(gitCache) {
            "`GitCache` must not be null at this point, report this as a bug, please"
        }

private var gitCache: GitCache? = null

internal class GitCache
private constructor(
    private val gitDir: File,
    maxCount: Provider<Int>? = null,
) {

    internal val git: Git by lazy {
        Git(FileRepositoryBuilder().setGitDir(gitDir).readEnvironment().findGitDir().build()).also {
            if (it.hasNotCommits()) {
                semverWarningMessage("semver plugin can't work if there are no commits")
            }
        }
    }

    internal val gitFiles: List<File> = git.repository.directory.walkTopDown().toList()

    internal val isClean: Boolean
        get() = git.status().call().isClean

    internal val headRevCommit: RevCommit by lazy {
        RevWalk(git.repository).parseCommit(git.repository.resolve(Constants.HEAD))
    }

    internal val headCommit: GitRef.Head by lazy {
        GitRef.Head(
            GitRef.Commit(
                message = headRevCommit.shortMessage,
                fullMessage = headRevCommit.fullMessage,
                hash = headRevCommit.toObjectId().name,
            )
        )
    }

    internal val commitsInCurrentBranchRevCommit: List<RevCommit> by lazy {
        git.log().setMaxCount(maxCount?.get() ?: -1).call().toList()
    }
    internal val tagsInRepoRef: List<Ref> by lazy { git.tagList().call() }

    internal val commitsInCurrentBranchHash: List<String> by lazy {
        commitsInCurrentBranchRevCommit.map(RevCommit::getName)
    }

    internal val commitsInTheCurrentBranchPublicApi:
        List<com.javiersc.semver.project.gradle.plugin.Commit> by lazy {
        commitsInCurrentBranchRevCommit.map { revCommit ->
            val hash: String = revCommit.toObjectId().name
            val tags: List<com.javiersc.semver.project.gradle.plugin.Tag> =
                tagsInCurrentBranchRef
                    .filter { ref -> commitHash(ref) == hash }
                    .map { ref ->
                        com.javiersc.semver.project.gradle.plugin.Tag(
                            name = ref.tagName,
                            refName = ref.name,
                        )
                    }
            com.javiersc.semver.project.gradle.plugin.Commit(
                message = revCommit.shortMessage,
                fullMessage = revCommit.fullMessage,
                hash = hash,
                timestampEpochSecond = revCommit.authorIdent.whenAsInstant.epochSecond,
                tags = tags,
            )
        }
    }

    internal val commitsInCurrentBranch: List<GitRef.Commit> by lazy {
        commitsInCurrentBranchRevCommit.map { revCommit ->
            revCommit.run {
                GitRef.Commit(
                    message = shortMessage,
                    fullMessage = fullMessage,
                    hash = toObjectId().name,
                )
            }
        }
    }

    internal val lastCommitInCurrentBranch: GitRef.Commit? by lazy {
        commitsInCurrentBranch.firstOrNull()
    }

    internal fun commitHash(ref: Ref): String = commitHash(ref.objectId)

    internal fun commitHash(objectId: ObjectId): String =
        git.repository.parseCommit(objectId).toObjectId().name

    internal fun lastVersionCommitInCurrentBranch(tagPrefix: String): GitRef.Commit? =
        lastVersionTagInCurrentBranch(tagPrefix)?.commit

    internal val Ref.tagName: String
        get() = name.substringAfter("refs/tags/")

    internal val tagsInCurrentBranchRef: List<Ref>
        get() = tagsInRepoRef.filter { ref -> commitHash(ref) in commitsInCurrentBranchHash }

    internal val tagsInCurrentBranch: List<GitRef.Tag>
        get() =
            tagsInCurrentBranchRef.map { ref ->
                val commit = git.repository.parseCommit(ref.objectId)
                GitRef.Tag(
                    name = ref.tagName,
                    refName = ref.name,
                    commit =
                        GitRef.Commit(
                            message = commit.shortMessage,
                            fullMessage = commit.fullMessage,
                            hash = commit.toObjectId().name,
                        )
                )
            }

    internal fun tagsInCurrentCommit(hash: String): List<GitRef.Tag> =
        tagsInCurrentBranch.filter { it.commit.hash == hash }

    internal fun versionTagsInCurrentCommit(hash: String, tagPrefix: String): List<GitRef.Tag> =
        tagsInCurrentCommit(hash).filter { tag ->
            tag.name.startsWith(tagPrefix) &&
                GradleVersion.safe(tag.name.removePrefix(tagPrefix)).isSuccess
        }

    internal fun versionTagsInCurrentBranch(tagPrefix: String): List<GitRef.Tag> =
        tagsInCurrentBranch.filter { tag ->
            tag.name.startsWith(tagPrefix) &&
                GradleVersion.safe(tag.name.removePrefix(tagPrefix)).isSuccess
        }

    internal fun versionsInCurrentBranch(tagPrefix: String): List<GradleVersion> =
        versionTagsInCurrentBranch(tagPrefix).mapNotNull { tag ->
            GradleVersion.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
        }

    internal fun versionTagsSortedBySemver(tagPrefix: String): List<GitRef.Tag> =
        versionTagsInCurrentBranch(tagPrefix).sortedBy { tag ->
            GradleVersion.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
        }

    internal fun versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(
        tagPrefix: String
    ): List<GitRef.Tag> {
        val commitsByHash: Map<String, Int> =
            commitsInCurrentBranchHash.withIndex().associate { it.value to it.index }

        return versionTagsSortedBySemver(tagPrefix).sortedByDescending { tag ->
            commitsByHash[tag.commit.hash]
        }
    }

    internal fun lastVersionTagInCurrentBranch(tagPrefix: String): GitRef.Tag? =
        versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(tagPrefix).lastOrNull()

    // versions
    internal fun lastVersionInCurrentBranch(
        tagPrefix: String,
        isWarningLastVersionIsNotHigherVersion: (Boolean) -> Unit = {},
    ): GradleVersion =
        versionTagsInCurrentCommit(headCommit.commit.hash, tagPrefix).lastResultVersion(tagPrefix)
            ?: lastVersionTagInCurrentBranch(tagPrefix)?.name?.removePrefix(tagPrefix).run {
                if (this != null) {
                    val lastVersion: GradleVersion? = GradleVersion.safe(this).getOrNull()
                    val higherVersion: GradleVersion? =
                        versionsInCurrentBranch(tagPrefix).firstOrNull()

                    if (
                        lastVersion != null && higherVersion != null && higherVersion > lastVersion
                    ) {
                        isWarningLastVersionIsNotHigherVersion(true)
                        warningLastVersionIsNotHigherVersion(lastVersion, higherVersion)
                    }

                    lastVersion
                } else null
            }
                ?: InitialVersion

    internal fun shouldRefresh(): Boolean =
        git.repository.directory.walkTopDown().toList() != gitFiles

    private fun List<GitRef.Tag>.lastResultVersion(tagPrefix: String): GradleVersion? =
        asSequence()
            .filter { tag -> tag.name.startsWith(tagPrefix) }
            .map { tag -> tag.name.substringAfter(tagPrefix) }
            .map(GradleVersion.Companion::safe)
            .mapNotNull(Result<GradleVersion>::getOrNull)
            .toList()
            .run { maxOrNull() }

    companion object {

        internal operator fun invoke(gitDir: File, maxCount: Provider<Int>? = null): GitCache {
            // val cache: GitCache? = gitCache
            // TODO: improve in-memory cache as `cache.shouldRefresh()` is flaky
            // if (cache == null || cache.shouldRefresh() || true) {
            gitCache = GitCache(gitDir, maxCount)
            // }
            return checkedNotNullCache
        }
    }
}
