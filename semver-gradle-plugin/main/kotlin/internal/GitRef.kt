package com.javiersc.semver.gradle.plugin.internal

import org.eclipse.jgit.api.Git
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
        headRevCommit.run {
            GitRef.Head(
                GitRef.Commit(
                    shortMessage,
                    fullMessage,
                    toObjectId().name,
                )
            )
        }

internal val Git.commitsInCurrentBranch: List<GitRef.Commit>
    get() =
        commitsInCurrentBranchRevCommit.map { revCommit ->
            revCommit.run {
                GitRef.Commit(
                    shortMessage,
                    fullMessage,
                    toObjectId().name,
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
            ref.run {
                GitRef.Tag(
                    name = name.substringAfter("refs/tags/"),
                    refName = name,
                    commit =
                        GitRef.Commit(
                            commit.shortMessage,
                            commit.fullMessage,
                            commit.toObjectId().name,
                        )
                )
            }
        }

internal val Git.currentBranch: GitRef.Branch
    get() =
        repository.run {
            GitRef.Branch(branch, fullBranch, commitsInCurrentBranch, tagsInCurrentBranch)
        }

internal val Git.headRevCommit: RevCommit
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
    get() = tagsInRepoRef.map { it.objectId.name }

internal val Git.tagsInRepoName: List<String>
    get() = tagsInRepoRef.map(Ref::getName)

internal val Git.tagsInCurrentBranchRef: List<Ref>
    get() = tagsInRepoRef.filter { it.objectId.name in commitsInCurrentBranchHash }

internal val Git.tagsInCurrentBranchHash: List<String>
    get() = tagsInCurrentBranchRef.map { it.objectId.name }

internal val Git.tagsInCurrentBranchName: List<String>
    get() = tagsInCurrentBranchRef.map(Ref::getName)

internal fun Git.tagsInCurrentCommitRef(revCommit: RevCommit): List<Ref> =
    tagsInCurrentBranchRef.filter { it.objectId.name == revCommit.toObjectId().name }
