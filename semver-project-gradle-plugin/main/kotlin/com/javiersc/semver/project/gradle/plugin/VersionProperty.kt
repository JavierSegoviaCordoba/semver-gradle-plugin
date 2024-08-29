package com.javiersc.semver.project.gradle.plugin

import org.gradle.api.provider.Provider

public class VersionProperty(private val version: Provider<String>) : Provider<String> by version {

    override fun toString(): String = "${version.orNull}"
}
