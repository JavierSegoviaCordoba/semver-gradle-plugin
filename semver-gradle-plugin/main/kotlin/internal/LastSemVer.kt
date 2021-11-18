package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.semanticVersioning.Version
import org.gradle.api.Project

internal val Project.lastSemVer: Version
    get() =
        git.tagsInCurrentCommit(git.headCommit.commit.hash).lastResultVersion(tagPrefix)
            ?: git.tagsInCurrentBranch.lastResultVersion(tagPrefix) ?: InitialVersion

internal fun List<GitRef.Tag>.lastResultVersion(tagPrefix: String): Version? =
    asSequence()
        .filter { tag -> tag.name.startsWith(tagPrefix) }
        .map { tag -> tag.name.substringAfter(tagPrefix) }
        .map(Version.Companion::safe)
        .mapNotNull(Result<Version>::getOrNull)
        .toList()
        .run { maxOrNull() }
