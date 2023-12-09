package com.javiersc.semver.project.gradle.plugin._internal

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.gradle.version.GradleVersion
import com.javiersc.gradle.version.GradleVersionException
import com.javiersc.kotlin.stdlib.AnsiColor
import com.javiersc.kotlin.stdlib.ansiColor
import com.javiersc.kotlin.stdlib.notContain
import com.javiersc.semver.project.gradle.plugin.Commit
import com.javiersc.semver.project.gradle.plugin.Tag
import com.javiersc.semver.project.gradle.plugin.createSandboxFile
import com.javiersc.semver.project.gradle.plugin.git
import com.javiersc.semver.project.gradle.plugin.internal.AdditionalVersionData
import com.javiersc.semver.project.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.project.gradle.plugin.internal.git.GitCache
import com.javiersc.semver.project.gradle.plugin.internal.git.GitRef
import com.javiersc.semver.project.gradle.plugin.internal.git.commitsInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.headCommit
import com.javiersc.semver.project.gradle.plugin.internal.git.lastCommitInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.lastVersionCommitInCurrentBranch
import com.javiersc.semver.project.gradle.plugin.internal.git.versionTagsInCurrentBranch
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import java.time.Instant
import java.util.Locale
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo

abstract class SemverGradleTestKit : GradleTestKitTest() {

    private lateinit var testInfo: TestInfo

    @BeforeEach
    fun setTestInfo(testInfo: TestInfo) {
        this.testInfo = testInfo
    }

    internal fun semverGradleTestKit(
        testData: SemverTestData,
        initialTestData: (SemverTestData.(GradleRunner) -> Unit)? = null,
        test: (SemverTestData.(GradleRunner) -> Unit)? = null,
    ) {
        semverGradleTestKit(
            arguments = testData.arguments,
            lastTags = testData.lastTags,
            root = testData.root,
            subprojects = testData.subprojects,
            buildAndFail = testData.buildAndFail,
            initialTestData = initialTestData,
            test = test,
        )
    }

    internal fun semverGradleTestKit(
        arguments: String,
        lastTags: List<String>,
        root: SemverTestData.Project,
        subprojects: List<SemverTestData.Project> = emptyList(),
        buildAndFail: Boolean = false,
        initialTestData: (SemverTestData.(GradleRunner) -> Unit)? = null,
        test: (SemverTestData.(GradleRunner) -> Unit)? = null,
    ) {
        val initialTestDataBuilt: SemverTestData.(GradleRunner) -> Unit =
            initialTestData
                ?: {
                    it.projectDir.generateInitialCommitAddVersionTagAndAddNewCommit(
                        lastTags = this.lastTags
                    )
                }
        val testBuilt: SemverTestData.(GradleRunner) -> Unit =
            test
                ?: { runner ->
                    runner.withArguments(arguments.split(" "))
                    val result: BuildResult =
                        if (buildAndFail) runner.buildAndFail() else runner.build()
                    val successResult: Boolean =
                        !buildAndFail && result.output.notContain("FAILURE: Build failed")
                    if (successResult) {
                        runner.projectDir.assertVersionFromExpectVersion(
                            root.expectVersion,
                            root.tagPrefix
                        )
                        for ((index, subproject) in subprojects.withIndex()) {
                            runner.projectDir
                                .resolve("subproject-$index")
                                .assertVersionFromExpectVersion(
                                    subproject.expectVersion,
                                    subproject.tagPrefix
                                )
                        }
                    }
                }

        val data =
            SemverTestData(
                arguments = arguments,
                lastTags = lastTags,
                root = root,
                subprojects = subprojects,
                buildAndFail = buildAndFail,
            )
        val testName: String = testInfo.testMethod.get().name
        gradleTestKitTest(name = testName) {
            println("\nTesting: $testName".ansiColor(AnsiColor.Foreground.Purple))
            println("Arguments: $data".ansiColor(AnsiColor.Foreground.Cyan))
            projectDir.writeSemverTestData(data)
            data.initialTestDataBuilt(this)
            data.testBuilt(this)
        }
    }
}

internal data class SemverTestData(
    val arguments: String,
    val lastTags: List<String>,
    val root: Project,
    val subprojects: List<Project>,
    val buildAndFail: Boolean = false,
) {

    constructor(
        arguments: String,
        lastTag: String,
        root: Project,
        subprojects: List<Project>,
        buildAndFail: Boolean = false,
    ) : this(arguments, listOf(lastTag), root, subprojects, buildAndFail)

    data class Project(
        val tagPrefix: String,
        val expectVersion: String,
    )

    override fun toString(): String =
        """
            |arguments: $arguments
            |lastTags: $lastTags
            |root: $root
            |subprojects: $subprojects
            |buildAndFail: $buildAndFail
            |
        """
            .trimMargin()
}

private fun File.writeSemverTestData(testData: SemverTestData) {
    writeSettingsFile(testData)
    writeRootFile(testData)
    writeProjectFiles(testData)
}

private fun File.writeSettingsFile(testData: SemverTestData) {
    val content: String = buildString {
        appendLine("rootProject.name = \"sandbox-project\"")
        appendLine()
        testData.subprojects.forEachIndexed { index, subproject ->
            appendLine("include(\":subproject-$index\")")
        }
    }
    resolve("settings.gradle.kts").mkdirsAndWriteText(content)
}

private fun File.writeRootFile(testData: SemverTestData) {
    val content: String = buildString {
        appendLine("plugins {")
        appendLine("""    id("com.javiersc.semver.project")""")
        appendLine("}")
        appendLine()
        appendLine("semver {")
        appendLine("    tagPrefix = \"${testData.root.tagPrefix}\"")
        appendLine("}")
    }
    resolve("build.gradle.kts").mkdirsAndWriteText(content)
}

private fun File.writeProjectFiles(testData: SemverTestData) {
    testData.subprojects.forEachIndexed { index, subproject ->
        val content: String = buildString {
            appendLine("plugins {")
            appendLine("""    id("com.javiersc.semver.project")""")
            appendLine("}")
            appendLine()
            appendLine("semver {")
            appendLine("""    tagPrefix = "${subproject.tagPrefix}"""")
            appendLine("}")
        }
        resolve("subproject-$index/build.gradle.kts").mkdirsAndWriteText(content)
    }
}

private fun File.mkdirsAndWriteText(content: String) {
    parentFile.mkdirs()
    writeText(content)
}

internal fun File.generateInitialCommitAddVersionTagAndAddNewCommit(
    lastTags: List<String> = emptyList(),
    doBefore: Git.() -> Unit = {},
    doAfter: Git.() -> Unit = {},
): List<Commit> {
    createGitIgnore()
    val git: Git = Git.init().setDirectory(this).call()
    doBefore(git)
    git.add().addFilepattern(".").call()
    val commit = git.commit().setMessage("Initial commit").call()
    resolve("new.txt").createNewFile()
    for (lastTag in lastTags) {
        git.tag().setObjectId(commit).setName(lastTag).call()
    }
    git.add().addFilepattern(".").call()
    git.commit().setMessage("Add new").call()
    resolve("library/last-tag.txt").apply {
        if (exists()) git.tag().setObjectId(commit).setName(readLines().first()).call()
    }
    resolve("new2.txt").createNewFile()
    git.add().addFilepattern(".").call()
    git.commit().setMessage("Add new2").call()
    doAfter(git)

    return listOf(
        Commit(
            message = "Add new2",
            fullMessage = "Add new2",
            hash = "f099fed42808c387d38f8e34934d4ef2d6f3c2c5",
            timestampEpochSecond = Instant.now().epochSecond,
            tags = emptyList()
        ),
        Commit(
            message = "Add new",
            fullMessage = "Add new",
            hash = "0d9be4d12a836435c91fc38e0d64bcd3c14c89c8",
            timestampEpochSecond = Instant.now().epochSecond,
            tags = emptyList()
        ),
        Commit(
            message = "Initial commit",
            fullMessage = "Initial commit",
            hash = "e2592aafc259bd797e32f1ea4fe60cc7c0698f70",
            timestampEpochSecond = Instant.now().epochSecond,
            tags =
                listOf(
                    Tag(name = "1.0.0", refName = "refs/tags/1.0.0"),
                )
        )
    )
}

internal fun File.createGitIgnore() {
    resolve(".gitignore").apply {
        createNewFile()
        writeText(
            """
                |.idea/
                |build/
                |.gradle/
                |local.properties
                |environment/
                |
            """
                .trimMargin()
        )
    }
}

private fun initialCommitAnd(and: File.() -> Unit) {
    val fileName: String =
        Thread.currentThread()
            .stackTrace
            .first { element -> element.methodName.count { char -> char.isWhitespace() } > 0 }
            .methodName

    createSandboxFile(fileName).apply {
        val git: Git = Git.init().setDirectory(this).call()
        resolve("Initial commit.txt").createNewFile()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Initial commit").call()
        and(this)
    }
}

private fun Git.calculateAdditionalVersionData(
    tagPrefix: String,
    checkIsClean: Boolean,
): AdditionalVersionData? =
    calculateAdditionalVersionData(
        clean = status().call().isClean,
        checkClean = checkIsClean,
        lastCommitInCurrentBranch = lastCommitInCurrentBranch?.hash,
        commitsInCurrentBranch = commitsInCurrentBranch.map(GitRef.Commit::hash),
        isThereVersionTags = versionTagsInCurrentBranch(tagPrefix).isNotEmpty(),
        headCommit = headCommit.commit.hash,
        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranch(tagPrefix)?.hash,
    )

val File.hash7: String
    get() = git.lastCommitInCurrentBranch!!.hash.take(7)

private fun File.tagCall(name: String): Ref = git.tag().setName(name).call()

private fun File.commitCall(message: String): RevCommit = git.commit().setMessage(message).call()

private fun File.addAllCall() {
    git.add().addFilepattern(".").call()
}

fun File.createNewFile(file: String) = resolve(file).createNewFile()

private val File.gitCache: GitCache
    get() = GitCache(gitDir = git.repository.directory, maxCount = null)

private fun File.assertVersionFromExpectVersion(version: String, tagPrefix: String) {
    val hash = "+HASH"
    val dirty = "+DIRTY"
    val insignificant =
        when {
            version.endsWith(hash, false) || version.endsWith(hash, false) -> {
                Insignificant.Hash
            }
            version.endsWith(dirty, false) || version.endsWith(dirty, false) -> {
                Insignificant.Dirty
            }
            else -> null
        }
    val sanitizeVersion = version.substringBeforeLast(hash).substringBeforeLast(dirty)
    assertVersion(tagPrefix, sanitizeVersion, insignificant)
}

private fun File.assertVersion(
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
            shouldThrow<GradleVersionException> {
                GradleVersion(buildVersion, GradleVersion.CheckMode.Significant)
            }
        }
        Insignificant.Dirty -> {
            buildVersion.startsWith(version).shouldBeTrue()
            buildTagVersion.startsWith("$prefix$version").shouldBeTrue()
            buildVersion.shouldContain("+").shouldContain("DIRTY")
            shouldThrow<GradleVersionException> {
                GradleVersion(buildVersion, GradleVersion.CheckMode.Significant)
            }
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
                        .trimMargin()
                )
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
    val actualVersion = "ACTUAL: $buildVersion".ansiColor(AnsiColor.Foreground.Yellow)
    val hyphen = "-".ansiColor(AnsiColor.Reset)
    val expectedVersion =
        "EXPECTED: $version$insignificantText".ansiColor(AnsiColor.Foreground.BrightYellow)

    val actualTaggedVersion = "ACTUAL: $buildTagVersion".ansiColor(AnsiColor.Foreground.Yellow)
    val expectedTaggedVersion =
        "EXPECTED: $prefix$version$insignificantText".ansiColor(AnsiColor.Foreground.BrightYellow)

    println("VERSION -> $actualVersion $hyphen $expectedVersion")
    println("VERSION WITH TAG -> $actualTaggedVersion $hyphen $expectedTaggedVersion")
    println("----------------------------------------".ansiColor(AnsiColor.Foreground.Purple))
}

private enum class Insignificant {
    Hash,
    Dirty;

    override fun toString(): String = name.uppercase(Locale.getDefault())
}
