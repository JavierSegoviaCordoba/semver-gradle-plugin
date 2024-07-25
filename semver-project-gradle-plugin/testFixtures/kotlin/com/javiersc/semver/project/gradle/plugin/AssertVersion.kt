package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.gradle.version.GradleVersion.CheckMode.Significant
import com.javiersc.gradle.version.GradleVersionException
import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.BrightYellow
import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.Purple
import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.Yellow
import com.javiersc.kotlin.stdlib.AnsiColor.Reset
import com.javiersc.kotlin.stdlib.ansiColor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import java.util.Locale

internal fun File.assertVersionFromExpectVersionFiles() {
    walkTopDown()
        .filter { it.name == "expect-version.txt" }
        .map { it.parentFile }
        .toList()
        .also { check(it.isNotEmpty()) }
        .forEach(::assertVersionFromExpectVersionFile)
}

internal fun assertVersionFromExpectVersionFile(file: File) {
    val expectedVersionFile = file.resolve("expect-version.txt")
    val lines: List<String> = expectedVersionFile.readLines()
    val (version: String, tagPrefixVersion: String) = lines
    val tagPrefix = tagPrefixVersion.takeWhile(Char::isLetter)
    val hash = "+HASH"
    val dirty = "+DIRTY"
    val insignificant =
        when {
            version.endsWith(hash, false) || tagPrefixVersion.endsWith(hash, false) -> {
                Insignificant.Hash
            }
            version.endsWith(dirty, false) || tagPrefixVersion.endsWith(dirty, false) -> {
                Insignificant.Dirty
            }
            else -> null
        }
    val sanitizeVersion = version.substringBeforeLast(hash).substringBeforeLast(dirty)
    file.assertVersion(tagPrefix, sanitizeVersion, insignificant)
}

internal fun File.assertVersion(
    prefix: String,
    version: String,
    insignificant: Insignificant? = null
) {
    val buildVersionFile = resolve("build/semver/version.txt")
    val buildVersion = buildVersionFile.readLines().first()
    val buildTagVersion = buildVersionFile.readLines()[1]
    printExpectedAndActualVersions(buildVersion, version, insignificant, buildTagVersion, prefix)
    when (insignificant) {
        Insignificant.Hash -> {
            buildVersion.startsWith(version).shouldBeTrue()
            buildTagVersion.startsWith("$prefix$version").shouldBeTrue()
            buildVersion.shouldContain("+")
            shouldThrow<GradleVersionException> { GradleVersion(buildVersion, Significant) }
        }
        Insignificant.Dirty -> {
            buildVersion.startsWith(version).shouldBeTrue()
            buildTagVersion.startsWith("$prefix$version").shouldBeTrue()
            buildVersion.shouldContain("+").shouldContain("DIRTY")
            shouldThrow<GradleVersionException> { GradleVersion(buildVersion, Significant) }
        }
        else -> {
            buildVersionFile
                .readText()
                .shouldBe(
                    """
                       |$version
                       |$prefix$version
                       |
                    """
                        .trimMargin())
        }
    }
}

private fun printExpectedAndActualVersions(
    buildVersion: String,
    version: String,
    insignificant: Insignificant?,
    buildTagVersion: String,
    prefix: String
) {
    val insignificantText = if (insignificant != null) "+$insignificant" else ""
    val actualVersion = "ACTUAL: $buildVersion".ansiColor(Yellow)
    val hyphen = "-".ansiColor(Reset)
    val expectedVersion = "EXPECTED: $version$insignificantText".ansiColor(BrightYellow)

    val actualTaggedVersion = "ACTUAL: $buildTagVersion".ansiColor(Yellow)
    val expectedTaggedVersion =
        "EXPECTED: $prefix$version$insignificantText".ansiColor(BrightYellow)

    println("VERSION -> $actualVersion $hyphen $expectedVersion")
    println("VERSION WITH TAG -> $actualTaggedVersion $hyphen $expectedTaggedVersion")
    println("----------------------------------------".ansiColor(Purple))
}

internal enum class Insignificant {
    Hash,
    Dirty;

    override fun toString(): String = name.uppercase(Locale.getDefault())
}
