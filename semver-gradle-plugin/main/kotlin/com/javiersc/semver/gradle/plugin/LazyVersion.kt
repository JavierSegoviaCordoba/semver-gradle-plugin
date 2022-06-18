package com.javiersc.semver.gradle.plugin

import org.gradle.api.provider.Provider

public class LazyVersion(internal val version: Provider<String>) {

    private var cachedVersion: String? = null

    override fun toString(): String {
        if (cachedVersion == null) cachedVersion = version.get()
        cachedVersion ?: version.get()
        return cachedVersion!!
    }
}
