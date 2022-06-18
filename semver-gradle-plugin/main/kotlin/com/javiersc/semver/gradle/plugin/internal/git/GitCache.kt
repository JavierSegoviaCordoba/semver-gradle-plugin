package com.javiersc.semver.gradle.plugin.internal.git

import com.javiersc.semver.Version
import com.javiersc.semver.gradle.plugin.internal.InitialVersion
import com.javiersc.semver.gradle.plugin.internal.warningLastVersionIsNotHigherVersion
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

internal class GitCache(private val git: Git) {

    internal val isClean: Boolean
        get() = git.status().call().isClean

    internal val headRevCommit: RevCommit =
        RevWalk(git.repository).parseCommit(git.repository.resolve(Constants.HEAD))

    internal val headCommit: GitRef.Head =
        GitRef.Head(
            GitRef.Commit(
                message = headRevCommit.shortMessage,
                fullMessage = headRevCommit.fullMessage,
                hash = headRevCommit.toObjectId().name,
            )
        )

    internal val commitsInCurrentBranchRevCommit: List<RevCommit> = git.log().call().toList()

    internal val commitsInCurrentBranch: List<GitRef.Commit> =
        commitsInCurrentBranchRevCommit.map { revCommit ->
            revCommit.run {
                GitRef.Commit(
                    message = shortMessage,
                    fullMessage = fullMessage,
                    hash = toObjectId().name,
                )
            }
        }

    internal val lastCommitInCurrentBranch: GitRef.Commit? = commitsInCurrentBranch.firstOrNull()

    internal val commitsInCurrentBranchHash: List<String> =
        commitsInCurrentBranchRevCommit.map(RevCommit::getName)

    internal fun commitHash(ref: Ref): String = commitHash(ref.objectId)

    internal fun commitHash(objectId: ObjectId): String =
        git.repository.parseCommit(objectId).toObjectId().name

    internal fun lastVersionCommitInCurrentBranch(tagPrefix: String): GitRef.Commit? =
        lastVersionTagInCurrentBranch(tagPrefix)?.commit

    internal val Ref.tagName: String
        get() = name.substringAfter("refs/tags/")

    internal val tagsInRepoRef: List<Ref> = git.tagList().call()

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
                Version.safe(tag.name.removePrefix(tagPrefix)).isSuccess
        }

    internal fun versionTagsInCurrentBranch(tagPrefix: String): List<GitRef.Tag> =
        tagsInCurrentBranch.filter { tag ->
            tag.name.startsWith(tagPrefix) &&
                Version.safe(tag.name.removePrefix(tagPrefix)).isSuccess
        }

    internal fun versionsInCurrentBranch(tagPrefix: String): List<Version> =
        versionTagsInCurrentBranch(tagPrefix).mapNotNull { tag ->
            Version.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
        }

    internal fun versionTagsSortedBySemver(tagPrefix: String): List<GitRef.Tag> =
        versionTagsInCurrentBranch(tagPrefix).sortedBy { tag ->
            Version.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
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
    ): Version =
        versionTagsInCurrentCommit(headCommit.commit.hash, tagPrefix).lastResultVersion(tagPrefix)
            ?: lastVersionTagInCurrentBranch(tagPrefix)?.name?.removePrefix(tagPrefix).run {
                if (this != null) {
                    val lastVersion: Version? = Version.safe(this).getOrNull()
                    val higherVersion: Version? = versionsInCurrentBranch(tagPrefix).firstOrNull()

                    if (lastVersion != null && higherVersion != null && higherVersion > lastVersion
                    ) {
                        isWarningLastVersionIsNotHigherVersion(true)
                        warningLastVersionIsNotHigherVersion(lastVersion, higherVersion)
                    }

                    lastVersion
                } else null
            }
                ?: InitialVersion

    private fun List<GitRef.Tag>.lastResultVersion(tagPrefix: String): Version? =
        asSequence()
            .filter { tag -> tag.name.startsWith(tagPrefix) }
            .map { tag -> tag.name.substringAfter(tagPrefix) }
            .map(Version.Companion::safe)
            .mapNotNull(Result<Version>::getOrNull)
            .toList()
            .run { maxOrNull() }
}
