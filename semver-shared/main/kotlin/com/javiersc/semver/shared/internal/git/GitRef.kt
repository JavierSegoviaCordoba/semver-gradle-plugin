package com.javiersc.semver.shared.internal.git

public sealed class GitRef {

    public data class Head(val commit: Commit) : GitRef()

    public data class Commit(val message: String, val fullMessage: String, val hash: String) :
        GitRef()

    public data class Tag(val name: String, val refName: String, val commit: Commit) : GitRef()

    public data class Branch(
        val name: String, // example: `main`
        val refName: String, // example: `refs/heads/main`
        val commits: List<Commit>,
        val tags: List<Tag>,
    ) : GitRef()
}
