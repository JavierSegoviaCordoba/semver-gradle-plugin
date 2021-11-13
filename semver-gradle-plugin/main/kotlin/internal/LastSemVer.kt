package com.javiersc.semver.gradle.plugin.internal

import com.javiersc.semanticVersioning.Version
import com.javiersc.semver.gradle.plugin.tagPrefix
import org.gradle.api.Project

internal val Project.lastSemVer: Version
    get() =
        git.tagsInCurrentCommit(git.headCommit.commit.hash).lastResultVersion(tagPrefix, true)
            ?: git.tagsInCurrentBranch.lastResultVersion(tagPrefix, false) ?: InitialVersion

internal fun List<GitRef.Tag>.lastResultVersion(
    tagPrefix: String,
    inCurrentCommit: Boolean
): Version? =
    asSequence()
        .filter { tag -> tag.name.startsWith(tagPrefix) }
        .map { tag -> tag.name.substringAfter(tagPrefix) }
        .map(Version.Companion::safe)
        .mapNotNull(Result<Version>::getOrNull)
        .toList()
        .run { if (inCurrentCommit) maxOrNull() else lastOrNull() }
