package com.javiersc.semver.project.gradle.plugin

import org.gradle.api.provider.Provider

public class LazyVersion(internal val version: Provider<String>) {

    private var cachedVersion: String? = null

    public fun map(transform: (String) -> String) {
        cachedVersion = transform(version.get())
    }

    override fun toString(): String {
        if (cachedVersion == null) cachedVersion = version.get()
        cachedVersion ?: version.get()
        return cachedVersion!!
    }
}
