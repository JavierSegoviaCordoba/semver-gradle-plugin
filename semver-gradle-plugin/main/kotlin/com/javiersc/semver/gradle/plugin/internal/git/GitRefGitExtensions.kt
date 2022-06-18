package com.javiersc.semver.gradle.plugin.internal.git

import com.javiersc.semver.Version
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

internal val Git.headRef: Ref
    get() = repository.findRef(repository.fullBranch)

// branches
internal val Git.currentBranch: GitRef.Branch
    get() =
        repository.run {
            GitRef.Branch(branch, fullBranch, commitsInCurrentBranch, tagsInCurrentBranch)
        }

// commits
internal val Git.headCommit: GitRef.Head
    get() =
        GitRef.Head(
            GitRef.Commit(
                message = headRevCommit.shortMessage,
                fullMessage = headRevCommit.fullMessage,
                hash = headRevCommit.toObjectId().name,
            )
        )

internal val Git.headRevCommit: RevCommit
    get() = RevWalk(repository).parseCommit(repository.resolve(Constants.HEAD))

internal val Git.headRevCommitInBranch: RevCommit
    get() = RevWalk(repository).parseCommit(headRef.objectId)

internal val Git.lastCommitInCurrentBranch: GitRef.Commit?
    get() = commitsInCurrentBranch.firstOrNull()

internal val Git.commitsInCurrentBranchRevCommit: List<RevCommit>
    get() = log().call().toList()

internal val Git.commitsInCurrentBranch: List<GitRef.Commit>
    get() =
        commitsInCurrentBranchRevCommit.map { revCommit ->
            revCommit.run {
                GitRef.Commit(
                    message = shortMessage,
                    fullMessage = fullMessage,
                    hash = toObjectId().name,
                )
            }
        }

internal val Git.commitsInCurrentBranchHash: List<String>
    get() = commitsInCurrentBranchRevCommit.map(RevCommit::getName)

internal fun Git.commitHash(ref: Ref): String = commitHash(ref.objectId)

internal fun Git.commitHash(objectId: ObjectId): String =
    repository.parseCommit(objectId).toObjectId().name

internal fun commitsBetweenTwoCommitsIncludingLastExcludingFirst(
    fromCommit: String?,
    toCommit: String?,
    commitsInCurrentBranch: List<String>,
): List<String> {
    val to: Int = commitsInCurrentBranch.indexOf(toCommit)
    val from: Int = commitsInCurrentBranch.indexOf(fromCommit)

    return when {
        (to == -1 || from == -1) -> emptyList()
        to > from -> commitsInCurrentBranch.subList(from, to)
        else -> commitsInCurrentBranch.subList(to, from)
    }
}

internal val Git.commitsInCurrentBranchFullMessage: List<String>
    get() = commitsInCurrentBranchRevCommit.map(RevCommit::getFullMessage)

internal fun Git.lastVersionCommitInCurrentBranch(tagPrefix: String): GitRef.Commit? =
    lastVersionTagInCurrentBranch(tagPrefix)?.commit

// tags
internal val Ref.tagName: String
    get() = name.substringAfter("refs/tags/")

internal val Git.tagsInRepo: List<GitRef.Tag>
    get() =
        tagsInRepoRef.map { ref ->
            val commit = repository.parseCommit(ref.objectId)
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

internal val Git.tagsInRepoRef: List<Ref>
    get() = Git(repository).tagList().call()

internal val Git.tagsInRepoHash: List<String>
    get() = tagsInRepoRef.map(::commitHash)

internal val Git.tagsInRepoName: List<String>
    get() = tagsInRepoRef.map(Ref::getName)

internal val Git.tagsInCurrentBranch: List<GitRef.Tag>
    get() =
        tagsInCurrentBranchRef.map { ref ->
            val commit = repository.parseCommit(ref.objectId)
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

internal val Git.tagsInCurrentBranchRef: List<Ref>
    get() = tagsInRepoRef.filter { ref -> commitHash(ref) in commitsInCurrentBranchHash }

internal val Git.tagsInCurrentBranchHash: List<String>
    get() = tagsInCurrentBranchRef.map(::commitHash)

internal val Git.tagsInCurrentBranchName: List<String>
    get() = tagsInCurrentBranchRef.map(Ref::getName)

internal fun Git.tagsInCurrentCommit(hash: String): List<GitRef.Tag> =
    tagsInCurrentBranch.filter { it.commit.hash == hash }

internal fun Git.isThereVersionTag(tagPrefix: String): Boolean =
    versionTagsInCurrentBranch(tagPrefix).isNotEmpty()

internal fun Git.versionTagsInCurrentCommit(hash: String, tagPrefix: String): List<GitRef.Tag> =
    tagsInCurrentCommit(hash).filter { tag ->
        tag.name.startsWith(tagPrefix) && Version.safe(tag.name.removePrefix(tagPrefix)).isSuccess
    }

internal fun Git.versionTagsInCurrentBranch(tagPrefix: String): List<GitRef.Tag> =
    tagsInCurrentBranch.filter { tag ->
        tag.name.startsWith(tagPrefix) && Version.safe(tag.name.removePrefix(tagPrefix)).isSuccess
    }

internal fun Git.versionTagsSortedBySemver(tagPrefix: String): List<GitRef.Tag> =
    versionTagsInCurrentBranch(tagPrefix).sortedBy { tag ->
        Version.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
    }

internal fun Git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(
    tagPrefix: String
): List<GitRef.Tag> {
    val commitsByHash: Map<String, Int> =
        commitsInCurrentBranchHash.withIndex().associate { it.value to it.index }

    return versionTagsSortedBySemver(tagPrefix).sortedByDescending { tag ->
        commitsByHash[tag.commit.hash]
    }
}

internal fun Git.lastVersionTagInCurrentBranch(tagPrefix: String): GitRef.Tag? =
    versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(tagPrefix).lastOrNull()
