package com.javiersc.semver.gradle.plugin.internal

import java.util.Date
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project

internal sealed interface GitRef {

    data class Head(val commit: Commit) : GitRef

    data class Commit(
        val message: String,
        val fullMessage: String,
        val hash: String,
    ) : GitRef

    data class Tag(val name: String, val refName: String, val commit: Commit) : GitRef

    data class Branch(
        val name: String,
        val fullName: String,
        val commits: List<Commit>,
        val tags: List<Tag>,
    ) : GitRef
}

internal val Project.git: Git
    get() =
        Git(
            FileRepositoryBuilder()
                .setGitDir(file("${rootProject.rootDir}/.git"))
                .readEnvironment()
                .findGitDir()
                .build()
        )

internal val Git.headCommit: GitRef.Head
    get() =
        GitRef.Head(
            GitRef.Commit(
                message = headRevCommit.shortMessage,
                fullMessage = headRevCommit.fullMessage,
                hash = headRevCommit.toObjectId().name,
            )
        )

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

internal fun Git.tagsInCurrentCommit(commit: GitRef): List<GitRef.Tag> =
    tagsInCurrentBranch.filter { it.commit == commit }

internal fun Git.tagsInCurrentCommit(hash: String): List<GitRef.Tag> =
    tagsInCurrentBranch.filter { it.commit.hash == hash }

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

internal val Git.currentBranch: GitRef.Branch
    get() =
        repository.run {
            GitRef.Branch(branch, fullBranch, commitsInCurrentBranch, tagsInCurrentBranch)
        }

internal fun List<GitRef.Commit>?.additionalDataIfTagIsNotInHead(
    git: Git,
    mockDate: Date?
): String =
    when {
        this == null -> ".0+${timestamp(mockDate)}"
        git.status().call().isClean.not() -> ".$size+${timestamp(mockDate)}"
        isEmpty() && git.status().call().isClean -> ""
        else -> ".$size+${first().hash.take(DEFAULT_SHORT_HASH_LENGTH)}"
    }

internal fun Git.commitsBetweenTwoCommits(
    fromCommit: GitRef.Commit?,
    toCommit: GitRef.Commit?,
): List<GitRef.Commit> {
    val to = commitsInCurrentBranch.indexOf(toCommit)
    val from = commitsInCurrentBranch.indexOf(fromCommit)

    return if (to == -1 || from == -1) emptyList() else commitsInCurrentBranch.subList(from, to)
}

internal val Git.headRevCommit: RevCommit
    get() = RevWalk(repository).parseCommit(repository.resolve(Constants.HEAD))

internal val Git.headRevCommitInBranch: RevCommit
    get() = RevWalk(repository).parseCommit(headRef.objectId)

internal val Git.headRef: Ref
    get() = repository.findRef(repository.fullBranch)

internal val Git.commitsInCurrentBranchRevCommit: List<RevCommit>
    get() = log().call().toList()

internal val Git.commitsInCurrentBranchHash: List<String>
    get() = commitsInCurrentBranchRevCommit.map(RevCommit::getName)

internal val Git.commitsInCurrentBranchFullMessage: List<String>
    get() = commitsInCurrentBranchRevCommit.map(RevCommit::getFullMessage)

internal val Git.tagsInRepoRef: List<Ref>
    get() = Git(repository).tagList().call()

internal val Git.tagsInRepoHash: List<String>
    get() = tagsInRepoRef.map(::commitHash)

internal val Git.tagsInRepoName: List<String>
    get() = tagsInRepoRef.map(Ref::getName)

internal val Git.tagsInCurrentBranchRef: List<Ref>
    get() = tagsInRepoRef.filter { ref -> commitHash(ref) in commitsInCurrentBranchHash }

internal val Git.tagsInCurrentBranchHash: List<String>
    get() = tagsInCurrentBranchRef.map(::commitHash)

internal val Git.tagsInCurrentBranchName: List<String>
    get() = tagsInCurrentBranchRef.map(Ref::getName)

internal fun Git.tagsInCurrentCommitRef(revCommit: RevCommit): List<Ref> =
    tagsInCurrentBranchRef.filter { it.objectId.name == revCommit.toObjectId().name }

internal fun Git.commitHash(ref: Ref): String = commitHash(ref.objectId)

internal fun Git.commitHash(objectId: ObjectId): String =
    repository.parseCommit(objectId).toObjectId().name

internal val Ref.tagName: String
    get() = name.substringAfter("refs/tags/")

internal const val DEFAULT_SHORT_HASH_LENGTH = 7

internal fun Project.calculateAdditionalVersionData(): String =
    git.calculateAdditionalVersionData(mockDate)

internal fun Git.calculateAdditionalVersionData(mockDate: Date? = null): String {
    val lastCommitInCurrentBranch = commitsInCurrentBranch.firstOrNull()
    val lastTagCommitInCurrentBranch =
        tagsInCurrentBranch.map(GitRef.Tag::commit).reversed().firstOrNull()

    val commitsBetweenCurrentAndLastTagCommit =
        if (lastTagCommitInCurrentBranch == null) null
        else commitsBetweenTwoCommits(lastCommitInCurrentBranch, lastTagCommitInCurrentBranch)

    return commitsBetweenCurrentAndLastTagCommit.additionalDataIfTagIsNotInHead(this, mockDate)
}
