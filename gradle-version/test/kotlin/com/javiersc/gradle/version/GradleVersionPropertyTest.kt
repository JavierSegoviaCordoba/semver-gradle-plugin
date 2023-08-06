package com.javiersc.gradle.version

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll
import io.kotest.property.forAll
import kotlin.test.Test

internal class GradleVersionPropertyTest {

    private val major = Arb.positiveInt(11)
    private val minor = Arb.positiveInt(11)
    private val patch = Arb.positiveInt(11)
    private val stageName =
        Arb.choice(
            Arb.constant("alpha"),
            Arb.constant("beta"),
            Arb.constant("dev"),
            Arb.constant("rc"),
            Arb.constant("SNAPSHOT"),
            Arb.constant("zasca"),
            Arb.constant(null)
        )
    private val num =
        Arb.choice(
            Arb.positiveInt(11),
            Arb.constant(null),
        )

    private val commits =
        Arb.choice(
            Arb.positiveInt(11),
            Arb.constant(null),
        )

    private val hash =
        Arb.choice(
            Arb.constant("h123456"),
            Arb.constant("H123456"),
            Arb.constant("h4Ash34"),
            Arb.constant("4Hash3h"),
            Arb.constant("0h2az2U"),
            Arb.constant("102aY20"),
            Arb.constant(null)
        )

    private val metadata =
        Arb.choice(
            Arb.constant("DIRTY"),
            Arb.constant("M3T4D4T4"),
            Arb.constant("3T4D4T4"),
            Arb.constant("3T4D4T"),
            Arb.constant("M3T4D4T"),
            Arb.constant("m3T4d4t"),
            Arb.constant("4444"),
            Arb.constant("777777777"),
            Arb.constant("AAAA"),
            Arb.constant("BBBBBBBBB"),
            Arb.constant(null)
        )

    private val versionArbitrary: Arb<GradleVersion> = arbitrary {
        val major: Int = major.bind()
        val minor: Int = minor.bind()
        val patch: Int = patch.bind()
        val scope = "$major.$minor.$patch"
        val stageName: String? = stageName.bind()
        val num: Int? = num.bind()
        val stage: String =
            when {
                stageName.equals("SNAPSHOT", ignoreCase = true) -> "-$stageName"
                stageName == null || num == null -> ""
                else -> "-$stageName.$num"
            }
        val commits: Int? = commits.bind()
        val hash: String? = hash.bind()
        val metadata: String? = metadata.bind()
        val commitsAndOrHashAndOrMetadata: String =
            when {
                stageName.equals("SNAPSHOT", ignoreCase = true) -> ""
                commits != null && metadata != null -> ".$commits+$metadata"
                commits != null && hash != null -> ".$commits+$hash"
                metadata != null -> "+$metadata"
                else -> ""
            }
        if (!stage.contains("SNAPSHOT", ignoreCase = true)) {
            GradleVersion("$scope$stage$commitsAndOrHashAndOrMetadata")
        } else {
            GradleVersion("$scope$commitsAndOrHashAndOrMetadata$stage")
        }
    }

    private val versionArbitrarySameMajorMinorPatch: Arb<GradleVersion> = arbitrary {
        val major = 1
        val minor = 0
        val patch = 0
        val stageName = stageName.bind()
        val num = 1
        val stage: String =
            when {
                stageName.equals("SNAPSHOT", ignoreCase = true) -> "-$stageName"
                stageName.equals("snapshot", ignoreCase = true) -> "-$stageName"
                stageName == null -> ""
                else -> "-$stageName.$num"
            }
        GradleVersion("$major.$minor.$patch$stage")
    }

    init {
        PropertyTesting.defaultIterationCount = 100_000
    }

    @Test
    fun major_comparator() = runTestNoTimeout {
        forAll(versionArbitrary, versionArbitrary) { a: GradleVersion, b: GradleVersion ->
            if (a.major > b.major) a > b else true
        }
    }

    @Test
    fun minor_comparator() = runTestNoTimeout {
        forAll(versionArbitrary, versionArbitrary) { a: GradleVersion, b: GradleVersion ->
            if ((a.major == b.major) && (a.minor > b.minor)) a > b else true
        }
    }

    @Test
    fun patch_comparator() = runTestNoTimeout {
        forAll(versionArbitrary, versionArbitrary) { a: GradleVersion, b: GradleVersion ->
            if ((a.major == b.major) && (a.minor == b.minor) && (a.patch > b.patch)) a > b else true
        }
    }

    @Test
    fun stage_name_comparator() = runTestNoTimeout {
        forAll(versionArbitrary, versionArbitrary) { a: GradleVersion, b: GradleVersion ->
            if (a nameComparator b) {
                val aName = a.stage?.name
                val bName = b.stage?.name
                val aIsSpecial = aName?.lowercase() in GradleVersion.SpecialStage.specials
                val bIsSpecial = bName?.lowercase() in GradleVersion.SpecialStage.specials
                val areBothSpecial = aIsSpecial && bIsSpecial
                when {
                    aName == null && bName == null -> true
                    aName != null && bName == null -> a < b
                    aName == null && bName != null -> a > b
                    aIsSpecial && !bIsSpecial -> a > b
                    !aIsSpecial && bIsSpecial -> a < b
                    areBothSpecial && aName!!.lowercase() == bName!!.lowercase() -> a == b
                    areBothSpecial && aName!!.lowercase() > bName!!.lowercase() -> a > b
                    areBothSpecial && aName!!.lowercase() < bName!!.lowercase() -> a < b
                    aName!!.lowercase() != GradleVersion.SpecialStage.dev &&
                        bName!!.lowercase() == GradleVersion.SpecialStage.dev -> a > b
                    aName.lowercase() == GradleVersion.SpecialStage.dev &&
                        bName!!.lowercase() != GradleVersion.SpecialStage.dev -> a < b
                    aName == bName -> a == b
                    aName > bName!! -> a > b
                    else -> b > a
                }
            } else true
        }

        forAll(versionArbitrarySameMajorMinorPatch, versionArbitrarySameMajorMinorPatch) {
            a: GradleVersion,
            b: GradleVersion ->
            val aName = a.stage?.name
            val bName = b.stage?.name
            val aIsSpecial = aName?.lowercase() in GradleVersion.SpecialStage.specials
            val bIsSpecial = bName?.lowercase() in GradleVersion.SpecialStage.specials
            when {
                aName == null && bName == null -> true
                aName != null && bName == null -> a < b
                aName == null && bName != null -> a > b
                aIsSpecial && bIsSpecial && aName?.lowercase() == bName?.lowercase() -> a == b
                aIsSpecial && !bIsSpecial -> a > b
                !aIsSpecial && bIsSpecial -> a < b
                aName!!.lowercase() != GradleVersion.SpecialStage.dev &&
                    bName!!.lowercase() == GradleVersion.SpecialStage.dev -> a > b
                aName.lowercase() == GradleVersion.SpecialStage.dev &&
                    bName!!.lowercase() != GradleVersion.SpecialStage.dev -> a < b
                aName.lowercase() == bName!!.lowercase() -> a == b
                aName.lowercase() > bName.lowercase() -> a > b
                else -> a < b
            }
        }
    }

    private infix fun GradleVersion.nameComparator(other: GradleVersion): Boolean =
        (this.major == other.major) &&
            (this.minor == other.minor) &&
            (this.patch == other.patch) &&
            (this.stage?.name != null) &&
            (other.stage?.name != null) &&
            (this.stage!!.name > other.stage!!.name)

    @Test
    fun stage_num_comparator() = runTestNoTimeout {
        forAll(versionArbitrary, versionArbitrary) { a: GradleVersion, b: GradleVersion ->
            if (a mumComparator b) a > b else true
        }
    }

    private infix fun GradleVersion.mumComparator(other: GradleVersion): Boolean =
        (this.major == other.major) &&
            (this.minor == other.minor) &&
            (this.patch == other.patch) &&
            (this.stage?.name != null) &&
            (other.stage?.name != null) &&
            (this.stage!!.name == other.stage!!.name) &&
            (this.stage?.num != null) &&
            (other.stage?.num != null) &&
            (this.stage!!.num!! > other.stage!!.num!!)

    @Test
    fun wrong_versions() = runTestNoTimeout {
        checkAll(major, minor, patch, stageName, num) { major, minor, patch, stageName, num ->
            when {
                stageName.equals("SNAPSHOT", true) && num != null -> {
                    shouldThrow<GradleVersionException> {
                        GradleVersion(major, minor, patch, stageName, num)
                    }
                }
                stageName.equals("snapshot", true) && num != null -> {
                    shouldThrow<GradleVersionException> {
                        GradleVersion(major, minor, patch, stageName, num)
                    }
                }
                stageName.equals("SNAPSHOT", true) && num == null -> {
                    GradleVersion(major, minor, patch, stageName, num)
                }
                stageName.equals("snapshot", true) && num == null -> {
                    GradleVersion(major, minor, patch, stageName, num)
                }
                stageName != null && num == null -> {
                    shouldThrow<GradleVersionException> {
                        GradleVersion(major, minor, patch, stageName, num)
                    }
                }
                else -> GradleVersion(major, minor, patch, stageName, num)
            }
        }
    }
}
