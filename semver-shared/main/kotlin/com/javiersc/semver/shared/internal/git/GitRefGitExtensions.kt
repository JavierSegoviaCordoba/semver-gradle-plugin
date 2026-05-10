package com.javiersc.semver.shared.internal.git

import com.javiersc.gradle.version.GradleVersion
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

public val Git.headRef: Ref
    get() = repository.findRef(repository.fullBranch)

// branches
public val Git.currentBranch: GitRef.Branch
    get() = repository.run {
        GitRef.Branch(branch, fullBranch, commitsInCurrentBranch, tagsInCurrentBranch)
    }

// commits
public val Git.headCommit: GitRef.Head
    get() =
        GitRef.Head(
            GitRef.Commit(
                message = headRevCommit.shortMessage,
                fullMessage = headRevCommit.fullMessage,
                hash = headRevCommit.toObjectId().name,
            )
        )

public val Git.headRevCommit: RevCommit
    get() = RevWalk(repository).parseCommit(repository.resolve(Constants.HEAD))

public val Git.headRevCommitInBranch: RevCommit
    get() = RevWalk(repository).parseCommit(headRef.objectId)

public val Git.lastCommitInCurrentBranch: GitRef.Commit?
    get() = commitsInCurrentBranch.firstOrNull()

public val Git.commitsInCurrentBranchRevCommit: List<RevCommit>
    get() = log().call().toList()

public val Git.commitsInCurrentBranch: List<GitRef.Commit>
    get() = commitsInCurrentBranchRevCommit.map { revCommit ->
        revCommit.run {
            GitRef.Commit(
                message = shortMessage,
                fullMessage = fullMessage,
                hash = toObjectId().name,
            )
        }
    }

public val Git.commitsInCurrentBranchHash: List<String>
    get() = commitsInCurrentBranchRevCommit.map(RevCommit::getName)

public fun Git.commitHash(ref: Ref): String = commitHash(ref.objectId)

public fun Git.commitHash(objectId: ObjectId): String =
    repository.parseCommit(objectId).toObjectId().name

public fun commitsBetweenTwoCommitsIncludingLastExcludingFirst(
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

public val Git.commitsInCurrentBranchFullMessage: List<String>
    get() = commitsInCurrentBranchRevCommit.map(RevCommit::getFullMessage)

public fun Git.lastVersionCommitInCurrentBranch(tagPrefix: String): GitRef.Commit? =
    lastVersionTagInCurrentBranch(tagPrefix)?.commit

// tags
public val Ref.tagName: String
    get() = name.substringAfter("refs/tags/")

public val Git.tagsInRepo: List<GitRef.Tag>
    get() = tagsInRepoRef.map { ref ->
        val commit = repository.parseCommit(ref.objectId)
        GitRef.Tag(
            name = ref.tagName,
            refName = ref.name,
            commit =
                GitRef.Commit(
                    message = commit.shortMessage,
                    fullMessage = commit.fullMessage,
                    hash = commit.toObjectId().name,
                ),
        )
    }

public val Git.tagsInRepoRef: List<Ref>
    get() = tagList().call()

public val Git.tagsInRepoHash: List<String>
    get() = tagsInRepoRef.map(::commitHash)

public val Git.tagsInRepoName: List<String>
    get() = tagsInRepoRef.map(Ref::getName)

public val Git.tagsInCurrentBranch: List<GitRef.Tag>
    get() = tagsInCurrentBranchRef.map { ref ->
        val commit = repository.parseCommit(ref.objectId)
        GitRef.Tag(
            name = ref.tagName,
            refName = ref.name,
            commit =
                GitRef.Commit(
                    message = commit.shortMessage,
                    fullMessage = commit.fullMessage,
                    hash = commit.toObjectId().name,
                ),
        )
    }

public val Git.tagsInCurrentBranchRef: List<Ref>
    get() = tagsInRepoRef.filter { ref -> commitHash(ref) in commitsInCurrentBranchHash }

public val Git.tagsInCurrentBranchHash: List<String>
    get() = tagsInCurrentBranchRef.map(::commitHash)

public val Git.tagsInCurrentBranchName: List<String>
    get() = tagsInCurrentBranchRef.map(Ref::getName)

public fun Git.tagsInCurrentCommit(hash: String): List<GitRef.Tag> = tagsInCurrentBranch.filter {
    it.commit.hash == hash
}

public fun Git.isThereVersionTag(tagPrefix: String): Boolean =
    versionTagsInCurrentBranch(tagPrefix).isNotEmpty()

public fun Git.versionTagsInCurrentCommit(hash: String, tagPrefix: String): List<GitRef.Tag> =
    tagsInCurrentCommit(hash).filter { tag ->
        tag.name.startsWith(tagPrefix) &&
            GradleVersion.safe(tag.name.removePrefix(tagPrefix)).isSuccess
    }

public fun Git.versionTagsInCurrentBranch(tagPrefix: String): List<GitRef.Tag> =
    tagsInCurrentBranch.filter { tag ->
        tag.name.startsWith(tagPrefix) &&
            GradleVersion.safe(tag.name.removePrefix(tagPrefix)).isSuccess
    }

public fun Git.versionTagsSortedBySemver(tagPrefix: String): List<GitRef.Tag> =
    versionTagsInCurrentBranch(tagPrefix).sortedBy { tag ->
        GradleVersion.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
    }

public fun Git.versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(
    tagPrefix: String
): List<GitRef.Tag> {
    val commitsByHash: Map<String, Int> =
        commitsInCurrentBranchHash.withIndex().associate { it.value to it.index }

    return versionTagsSortedBySemver(tagPrefix).sortedByDescending { tag ->
        commitsByHash[tag.commit.hash]
    }
}

public fun Git.lastVersionTagInCurrentBranch(tagPrefix: String): GitRef.Tag? =
    versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(tagPrefix).lastOrNull()
