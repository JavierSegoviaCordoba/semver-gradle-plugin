package com.javiersc.semver.gradle.plugin

import org.gradle.api.provider.Provider

public class LazyVersion(internal val version: Provider<String>) {

    override fun toString(): String = version.get()
}
