package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersionException
import io.kotest.assertions.throwables.shouldThrow

fun shouldThrowVersionException(block: () -> Unit): GradleVersionException =
    shouldThrow<GradleVersionException>(block)
