package com.javiersc.semver.gradle.plugin.internal.git

internal sealed class GitRef {

    data class Head(val commit: Commit) : GitRef()

    data class Commit(val message: String, val fullMessage: String, val hash: String) : GitRef()

    data class Tag(val name: String, val refName: String, val commit: Commit) : GitRef()

    data class Branch(
        val name: String, // example: `main`
        val refName: String, // example: `refs/heads/main`
        val commits: List<Commit>,
        val tags: List<Tag>,
    ) : GitRef()
}
