package com.javiersc.semver.project.gradle.plugin.tables

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.project.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.project.gradle.plugin.shouldThrowVersionException
import com.javiersc.semver.project.gradle.plugin.tables.VersionTableCleanTest.VersionTable.Aggregator
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.AggregateWith
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.ArgumentsAggregator
import org.junit.jupiter.params.provider.CsvFileSource

@DisplayName("VersionTableCleanTest clean=true")
internal class VersionTableCleanTest {

    private lateinit var info: TestInfo

    @Suppress("unused")
    private val testIndex: Int
        get() = info.displayName.substringAfter("[").substringBefore("]").toInt()

    @BeforeEach
    fun setup(info: TestInfo) {
        this.info = info
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=.csv"], numLinesToSkip = 1)
    fun `scope= stage=`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=alpha.csv"], numLinesToSkip = 1)
    fun `scope= stage=alpha`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=auto.csv"], numLinesToSkip = 1)
    fun `scope= stage=auto`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=beta.csv"], numLinesToSkip = 1)
    fun `scope= stage=beta`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=dev.csv"], numLinesToSkip = 1)
    fun `scope= stage=dev`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=final.csv"], numLinesToSkip = 1)
    fun `scope= stage=final`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=null.csv"], numLinesToSkip = 1)
    fun `scope= stage=null`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=rc.csv"], numLinesToSkip = 1)
    fun `scope= stage=rc`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=snapshot.csv"], numLinesToSkip = 1)
    fun `scope= stage=snapshot`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope= stage=zasca.csv"], numLinesToSkip = 1)
    fun `scope= stage=zasca`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=alpha.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=alpha`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=auto.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=auto`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=beta.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=beta`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=dev.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=dev`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=final.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=final`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=null.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=null`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=rc.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=rc`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=snapshot.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=snapshot`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=auto stage=zasca.csv"], numLinesToSkip = 1)
    fun `scope=auto stage=zasca`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=.csv"], numLinesToSkip = 1)
    fun `scope=major stage=`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=alpha.csv"], numLinesToSkip = 1)
    fun `scope=major stage=alpha`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=auto.csv"], numLinesToSkip = 1)
    fun `scope=major stage=auto`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=beta.csv"], numLinesToSkip = 1)
    fun `scope=major stage=beta`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=dev.csv"], numLinesToSkip = 1)
    fun `scope=major stage=dev`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=final.csv"], numLinesToSkip = 1)
    fun `scope=major stage=final`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=rc.csv"], numLinesToSkip = 1)
    fun `scope=major stage=rc`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=snapshot.csv"], numLinesToSkip = 1)
    fun `scope=major stage=snapshot`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=zasca.csv"], numLinesToSkip = 1)
    fun `scope=major stage=zasca`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=major stage=null.csv"], numLinesToSkip = 1)
    fun `scope=major stage=null`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=alpha.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=alpha`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=auto.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=auto`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=beta.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=beta`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=final.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=final`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=null.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=null`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=rc.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=rc`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=snapshot.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=snapshot`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=minor stage=zasca.csv"], numLinesToSkip = 1)
    fun `scope=minor stage=zasca`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=.csv"], numLinesToSkip = 1)
    fun `scope=null stage=`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=alpha.csv"], numLinesToSkip = 1)
    fun `scope=null stage=alpha`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=beta.csv"], numLinesToSkip = 1)
    fun `scope=null stage=beta`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=dev.csv"], numLinesToSkip = 1)
    fun `scope=null stage=dev`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=final.csv"], numLinesToSkip = 1)
    fun `scope=null stage=final`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=null.csv"], numLinesToSkip = 1)
    fun `scope=null stage=null`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=rc.csv"], numLinesToSkip = 1)
    fun `scope=null stage=rc`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=snapshot.csv"], numLinesToSkip = 1)
    fun `scope=null stage=snapshot`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=null stage=zasca.csv"], numLinesToSkip = 1)
    fun `scope=null stage=zasca`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=alpha.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=alpha`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=auto.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=auto`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=beta.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=beta`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=dev.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=dev`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=final.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=final`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=null.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=null`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=rc.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=rc`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=snapshot.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=snapshot`(@AggregateWith(Aggregator::class) table: VersionTable) {
        table.checkTable()
    }

    @ParameterizedTest
    @Csv(resources = ["/tables/clean=true/scope=patch stage=zasca.csv"], numLinesToSkip = 1)
    fun `scope=patch stage=zasca`(@AggregateWith(Aggregator::class) table: VersionTable) {
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

        object Aggregator : ArgumentsAggregator {

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

typealias Csv = CsvFileSource
