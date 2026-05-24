package com.javiersc.semver.shared.internal.git

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.shared.Commit
import com.javiersc.semver.shared.Tag
import com.javiersc.semver.shared.internal.InitialVersion
import com.javiersc.semver.shared.internal.semverWarningMessage
import com.javiersc.semver.shared.internal.warningLastVersionIsNotHigherVersion
import com.javiersc.semver.shared.services.hasNotCommits
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

@Suppress("TooManyFunctions")
public class GitCache
private constructor(private val gitDir: File, maxCount: Provider<Int>? = null) {

    public val git: Git by lazy {
        Git(FileRepositoryBuilder().setGitDir(gitDir).readEnvironment().findGitDir().build()).also {
            if (it.hasNotCommits()) {
                semverWarningMessage("semver plugin can't work if there are no commits")
            }
        }
    }

    // internal val gitFiles: List<File> = git.repository.directory.walkTopDown().toList()

    public val isClean: Boolean
        get() = git.status().call().isClean

    public val headRevCommit: RevCommit by lazy {
        RevWalk(git.repository).parseCommit(git.repository.resolve(Constants.HEAD))
    }

    public val headCommit: GitRef.Head by lazy {
        GitRef.Head(
            GitRef.Commit(
                message = headRevCommit.shortMessage,
                fullMessage = headRevCommit.fullMessage,
                hash = headRevCommit.toObjectId().name,
            )
        )
    }

    public val commitsInCurrentBranchRevCommit: List<RevCommit> by lazy {
        git.log().setMaxCount(maxCount?.get() ?: -1).call().toList()
    }
    public val tagsInRepoRef: List<Ref> by lazy { git.tagList().call() }

    public val commitsInCurrentBranchHash: List<String> by lazy {
        commitsInCurrentBranchRevCommit.map(RevCommit::getName)
    }

    public val commitsInTheCurrentBranchPublicApi: List<Commit> by lazy {
        commitsInCurrentBranchRevCommit.map { revCommit ->
            val hash: String = revCommit.toObjectId().name
            val tags: List<Tag> =
                tagsInCurrentBranchRef
                    .filter { ref -> commitHash(ref) == hash }
                    .map { ref -> Tag(name = ref.tagName, refName = ref.name) }
            Commit(
                message = revCommit.shortMessage,
                fullMessage = revCommit.fullMessage,
                hash = hash,
                timestampEpochSecond = revCommit.authorIdent.whenAsInstant.epochSecond,
                tags = tags,
            )
        }
    }

    public val commitsInCurrentBranch: List<GitRef.Commit> by lazy {
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

    public val lastCommitInCurrentBranch: GitRef.Commit? by lazy {
        commitsInCurrentBranch.firstOrNull()
    }

    public fun commitHash(ref: Ref): String = commitHash(ref.objectId)

    public fun commitHash(objectId: ObjectId): String =
        git.repository.parseCommit(objectId).toObjectId().name

    public fun lastVersionCommitInCurrentBranch(tagPrefix: String): GitRef.Commit? =
        lastVersionTagInCurrentBranch(tagPrefix)?.commit

    public val Ref.tagName: String
        get() = name.substringAfter("refs/tags/")

    public val tagsInCurrentBranchRef: List<Ref>
        get() = tagsInRepoRef.filter { ref -> commitHash(ref) in commitsInCurrentBranchHash }

    public val tagsInCurrentBranch: List<GitRef.Tag>
        get() = tagsInCurrentBranchRef.map { ref ->
            val commit: RevCommit = git.repository.parseCommit(ref.objectId)
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

    public fun tagsInCurrentCommit(hash: String): List<GitRef.Tag> = tagsInCurrentBranch.filter {
        it.commit.hash == hash
    }

    public fun versionTagsInCurrentCommit(hash: String, tagPrefix: String): List<GitRef.Tag> =
        tagsInCurrentCommit(hash).filter { tag ->
            tag.name.startsWith(tagPrefix) &&
                GradleVersion.safe(tag.name.removePrefix(tagPrefix)).isSuccess
        }

    public fun versionTagsInCurrentBranch(tagPrefix: String): List<GitRef.Tag> =
        tagsInCurrentBranch.filter { tag ->
            tag.name.startsWith(tagPrefix) &&
                GradleVersion.safe(tag.name.removePrefix(tagPrefix)).isSuccess
        }

    public fun versionsInCurrentBranch(tagPrefix: String): List<GradleVersion> =
        versionTagsInCurrentBranch(tagPrefix).mapNotNull { tag ->
            GradleVersion.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
        }

    public fun versionTagsSortedBySemver(tagPrefix: String): List<GitRef.Tag> =
        versionTagsInCurrentBranch(tagPrefix).sortedBy { tag ->
            GradleVersion.safe(tag.name.removePrefix(tagPrefix)).getOrNull()
        }

    public fun versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(
        tagPrefix: String
    ): List<GitRef.Tag> {
        val commitsByHash: Map<String, Int> =
            commitsInCurrentBranchHash.withIndex().associate { it.value to it.index }

        return versionTagsSortedBySemver(tagPrefix).sortedByDescending { tag ->
            commitsByHash[tag.commit.hash]
        }
    }

    public fun lastVersionTagInCurrentBranch(tagPrefix: String): GitRef.Tag? =
        versionTagsInCurrentBranchSortedByTimelineOrSemverOrder(tagPrefix).lastOrNull()

    // versions
    public fun lastVersionInCurrentBranch(
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

    // internal fun shouldRefresh(): Boolean =
    //     git.repository.directory.walkTopDown().toList() != gitFiles

    private fun List<GitRef.Tag>.lastResultVersion(tagPrefix: String): GradleVersion? =
        asSequence()
            .filter { tag -> tag.name.startsWith(tagPrefix) }
            .map { tag -> tag.name.substringAfter(tagPrefix) }
            .map(GradleVersion.Companion::safe)
            .mapNotNull(Result<GradleVersion>::getOrNull)
            .toList()
            .run { maxOrNull() }

    public companion object {

        public operator fun invoke(gitDir: File, maxCount: Provider<Int>? = null): GitCache {
            // val cache: GitCache? = gitCache
            // In-memory cache is disabled because `cache.shouldRefresh()` is flaky.
            // if (cache == null || cache.shouldRefresh() || true) {
            gitCache = GitCache(gitDir, maxCount)
            // }
            return checkedNotNullCache
        }
    }
}
