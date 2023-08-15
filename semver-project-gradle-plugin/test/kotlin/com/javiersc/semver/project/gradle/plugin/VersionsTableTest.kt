package com.javiersc.semver.project.gradle.plugin

import app.softwork.serialization.csv.CSVFormat
import com.javiersc.gradle.version.GradleVersion
import com.javiersc.kotlin.stdlib.resource
import com.javiersc.semver.project.gradle.plugin.internal.calculatedVersion
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import kotlin.test.Test
import kotlin.time.Duration
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

class VersionTableTest {

    @Test
    fun sample() {
        val index = 0
        val tables: List<VersionTable> = versionTablesForFile(fileName = "1.0.0")
        val table: VersionTable = tables[index]
        checkTable(index, table)
    }

    @Test fun `1,0,0`() = propertyTest("1.0.0")

    @Test fun `1,0,0-alpha,1`() = propertyTest("1.0.0-alpha.1")

    @Test fun `1,0,0-beta,1`() = propertyTest("1.0.0-beta.1")
}

private fun propertyTest(fileName: String): Unit = runTestNoTimeout {
    val indexToVersionTable: List<Pair<Int, VersionTable>> =
        versionTablesForFile(fileName = fileName)
            .mapIndexed { index, versionTable -> (index) to versionTable }
            .sortedBy(Pair<Int, VersionTable>::first)
    val exhaustive: Exhaustive<Pair<Int, VersionTable>> = exhaustive(indexToVersionTable)
    exhaustive.checkAll { (index: Int, table: VersionTable) -> checkTable(index, table) }
}

private fun checkTable(index: Int, table: VersionTable) {
    val humanIndex: Int = index + 1
    val tableLineNumber: Int = index + 5
    val expectedVersion: String = table.expectedVersion
    fun calculatedVersion(): String = table.asCalculatedVersion()
    if (expectedVersion == "fail") {
        shouldThrowVersionException { calculatedVersion() }
    } else {
        val calculatedVersion: String = calculatedVersion()
        (index + 1).shouldBe(humanIndex)
        (index + 5).shouldBe(tableLineNumber)
        calculatedVersion.shouldBe(expectedVersion)
    }
}

private fun VersionTable.asCalculatedVersion(): String =
    calculatedVersion(
        lastSemver = GradleVersion(lastVersion),
        stageProperty = stageOrNull,
        scopeProperty = scopeOrNull,
        isCreatingSemverTag = isCreatingTag,
        versionTagsInBranch = listOf(lastVersion),
        clean = clean,
        checkClean = checkClean,
        force = force,
        lastCommitInCurrentBranch = lastCommitInCurrentBranchOrNull,
        commitsInCurrentBranch = commitsInCurrentBranchAsList,
        headCommit = head,
        lastVersionCommitInCurrentBranch = lastVersionCommitInCurrentBranchOrNull,
    )

@OptIn(ExperimentalSerializationApi::class)
private fun versionTablesForFile(fileName: String): List<VersionTable> =
    resource("tables/$fileName.csv")
        .readText()
        .lines()
        .filter(String::isNotBlank)
        .joinToString("\n")
        .run { CSVFormat.decodeFromString(this) }

@Serializable
private data class VersionTable(
    val lastVersion: String,
    val clean: Boolean,
    private val scope: String,
    private val stage: String,
    val isCreatingTag: Boolean,
    val checkClean: Boolean,
    val force: Boolean,
    private val lastCommitInCurrentBranch: String,
    private val commitsInCurrentBranch: String,
    val head: String,
    private val lastVersionCommitInCurrentBranch: String,
    val expectedVersion: String,
) {

    val scopeOrNull: String?
        get() = scope.takeIfNotNull()

    val stageOrNull: String?
        get() = stage.takeIfNotNull()

    val lastCommitInCurrentBranchOrNull: String?
        get() = lastCommitInCurrentBranch.takeIfNotNull()

    val commitsInCurrentBranchAsList: List<String>
        get() = commitsInCurrentBranch.split("-").map { it.filterNot(Char::isWhitespace) }

    val lastVersionCommitInCurrentBranchOrNull: String?
        get() = lastVersionCommitInCurrentBranch.takeIfNotNull()

    private fun String.takeIfNotNull(): String? = takeIf { it != "null" && it.isNotBlank() }
}

private fun runTestNoTimeout(block: suspend TestScope.() -> Unit): TestResult =
    runTest(timeout = Duration.INFINITE, testBody = block)
