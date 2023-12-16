package com.javiersc.semver.project.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.VersionTableTest.VersionTable.Companion
import com.javiersc.semver.project.gradle.plugin.internal.calculatedVersion
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.AggregateWith
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.ArgumentsAggregator
import org.junit.jupiter.params.provider.CsvFileSource

internal class VersionTableTest {

    private lateinit var info: TestInfo

    @Suppress("unused")
    private val testIndex: Int
        get() = info.displayName.substringAfter("[").substringBefore("]").toInt()

    @BeforeEach
    fun setup(info: TestInfo) {
        this.info = info
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=auto stage=auto.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=auto stage=auto`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=major stage=null.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=major stage=null`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=minor stage=null.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=minor stage=null`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=null stage=alpha.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=null stage=alpha`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=null stage=beta.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=null stage=beta`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=null stage=dev.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=null stage=dev`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=null stage=final.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=null stage=final`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=null stage=null.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=null stage=null`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=null stage=rc.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=null stage=rc`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=null stage=snapshot.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=null stage=snapshot`(
        @AggregateWith(Companion::class) table: VersionTable
    ) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=patch stage=alpha.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=patch stage=alpha`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/tables/clean=true/scope=patch stage=null.csv"],
        numLinesToSkip = 1,
    )
    fun `clean=true scope=patch stage=null`(@AggregateWith(Companion::class) table: VersionTable) {
        table.checkTable()
    }

    internal data class VersionTable(
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

        private val scopeOrNull: String?
            get() = scope.takeIfNotNull()

        private val stageOrNull: String?
            get() = stage.takeIfNotNull()

        private val lastCommitInCurrentBranchOrNull: String?
            get() = lastCommitInCurrentBranch.takeIfNotNull()

        private val commitsInCurrentBranchAsList: List<String>
            get() = commitsInCurrentBranch.split("-").map { it.filterNot(Char::isWhitespace) }

        private val lastVersionCommitInCurrentBranchOrNull: String?
            get() = lastVersionCommitInCurrentBranch.takeIfNotNull()

        internal fun checkTable() {
            fun calculatedVersion(): String = asCalculatedVersion()
            if (expectedVersion == "fail") {
                shouldThrowVersionException { calculatedVersion() }
            } else {
                val calculatedVersion: String = calculatedVersion()
                calculatedVersion.shouldBe(expectedVersion)
            }
        }

        private fun String.takeIfNotNull(): String? = takeIf { it != "null" && it.isNotBlank() }

        private fun asCalculatedVersion(): String =
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

        companion object : ArgumentsAggregator {

            override fun aggregateArguments(
                accessor: ArgumentsAccessor,
                context: ParameterContext,
            ): VersionTable =
                VersionTable(
                    lastVersion = accessor.getString(0),
                    clean = accessor.getBoolean(1),
                    scope = accessor.getString(2),
                    stage = accessor.getString(3),
                    isCreatingTag = accessor.getBoolean(4),
                    checkClean = accessor.getBoolean(5),
                    force = accessor.getBoolean(6),
                    lastCommitInCurrentBranch = accessor.getString(7),
                    commitsInCurrentBranch = accessor.getString(8),
                    head = accessor.getString(9),
                    lastVersionCommitInCurrentBranch = accessor.getString(10),
                    expectedVersion = accessor.getString(11),
                )
        }
    }
}
