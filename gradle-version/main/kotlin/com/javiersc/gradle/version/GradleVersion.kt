package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.SpecialStage.Companion.dev
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.text.RegexOption.IGNORE_CASE

public class GradleVersion
private constructor(
    private val value: String,
) : Comparable<GradleVersion> {

    init {
        checkFullVersion(value)
    }

    public val major: Int = preStage.split(".").first().toInt()

    public val minor: Int = preStage.split(".")[1].toInt()

    public val patch: Int = preStage.split(".").getOrNull(2)?.toInt() ?: 0

    public val stage: Stage? = stageAndNum?.let { Stage(it) }

    @Suppress("ComplexMethod")
    override fun compareTo(other: GradleVersion): Int =
        when {
            major > other.major -> 1
            major < other.major -> -1
            minor > other.minor -> 1
            minor < other.minor -> -1
            patch > other.patch -> 1
            patch < other.patch -> -1
            stage == null && other.stage != null -> 1
            stage != null && other.stage == null -> -1
            stage != null && other.stage != null && stage > other.stage -> 1
            stage != null && other.stage != null && stage < other.stage -> -1
            else -> 0
        }

    @Suppress("ComplexMethod")
    public fun inc(number: Increase? = null, stageName: String = ""): GradleVersion {
        val incNum = if (stageName.lowercase() == "snapshot") null else 1
        val nextVersion: GradleVersion =
            when {
                number == null && stageName.isBlank() && !stage?.name.isNullOrBlank() -> {
                    invoke(major, minor, patch, null, null)
                }
                number == null && stageName.isBlank() -> {
                    invoke(major, minor, patch.inc(), null, null)
                }
                number == null && stageName.isNotBlank() && stage?.name.isNullOrBlank() -> {
                    invoke(major, minor, patch.inc(), stageName, incNum)
                }
                number == null && stageName.isNotBlank() && stageName == stage?.name -> {
                    invoke(major, minor, patch, stageName, stage.num?.inc())
                }
                number == null && stageName.isNotBlank() && stageName != stage?.name -> {
                    invoke(major, minor, patch, stageName, incNum)
                }
                number is Increase.Major && stageName.isBlank() -> {
                    invoke(major.inc(), 0, 0, null, null)
                }
                number is Increase.Minor && stageName.isBlank() -> {
                    invoke(major, minor.inc(), 0, null, null)
                }
                number is Increase.Patch && stageName.isBlank() -> {
                    invoke(major, minor, patch.inc(), null, null)
                }
                number is Increase.Major && stageName.isNotBlank() -> {
                    invoke(major.inc(), 0, 0, stageName, incNum)
                }
                number is Increase.Minor && stageName.isNotBlank() -> {
                    invoke(major, minor.inc(), 0, stageName, incNum)
                }
                number is Increase.Patch && stageName.isNotBlank() -> {
                    invoke(major, minor, patch.inc(), stageName, incNum)
                }
                else -> null
            }
                ?: gradleVersionError("There were an error configuring the version")

        if (nextVersion < this) {
            gradleVersionError(
                "Next version ($nextVersion) should be higher than the current one ($this)"
            )
        }
        return nextVersion
    }

    public fun copy(
        major: Int = this.major,
        minor: Int = this.minor,
        patch: Int = this.patch,
        stageName: String? = this.stage?.name,
        stageNum: Int? = this.stage?.num,
    ): GradleVersion =
        GradleVersion(
            major = major,
            minor = minor,
            patch = patch,
            stageName = stageName,
            stageNum = if (stageName.equals("SNAPSHOT", ignoreCase = true)) null else stageNum
        )

    override fun equals(other: Any?): Boolean {
        val otherVersion = other as? GradleVersion
        return when {
            otherVersion == null -> false
            compareTo(otherVersion) == 0 -> true
            else -> false
        }
    }

    override fun toString(): String = value

    override fun hashCode(): Int = value.hashCode()

    public companion object {
        public val regex: Regex =
            Regex(
                """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(-(?!SNAPSHOT\.\d)([a-zA-Z]+(\.\d+)|\bSNAPSHOT\b))?$""",
                IGNORE_CASE
            )

        public val scopeRegex: Regex = Regex("""^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)""")

        public operator fun invoke(value: String): GradleVersion = GradleVersion(value)

        public fun safe(value: String): Result<GradleVersion> = runCatching { GradleVersion(value) }

        public operator fun invoke(version: String, stage: String?): GradleVersion =
            if (stage.isNullOrBlank()) GradleVersion(version) else GradleVersion("$version-$stage")

        public fun safe(version: String, stage: String?): Result<GradleVersion> = runCatching {
            if (stage.isNullOrBlank()) GradleVersion(version) else GradleVersion("$version-$stage")
        }

        public operator fun invoke(
            major: Int,
            minor: Int,
            patch: Int,
            stageName: String?,
            stageNum: Int?,
        ): GradleVersion = GradleVersion(buildVersion(major, minor, patch, stageName, stageNum))

        public fun safe(
            major: Int,
            minor: Int,
            patch: Int,
            stageName: String?,
            stageNum: Int?,
        ): Result<GradleVersion> = runCatching {
            GradleVersion(major, minor, patch, stageName, stageNum)
        }
    }

    private val preStage: String
        get() = value.split("-").first()

    private val stageAndNum: String?
        get() = value.split("-").getOrNull(1)

    public class Stage private constructor(private val value: String) : Comparable<Stage> {

        init {
            checkStage(value)
        }

        public val name: String = value.split(".").first()

        public val num: Int? = value.split(".").getOrNull(1)?.toInt()

        @Suppress("ComplexMethod")
        override fun compareTo(other: Stage): Int {
            val special = SpecialStage(this)
            val otherSpecial = SpecialStage(other)

            val specialComparison =
                when {
                    special != null && otherSpecial == null -> 1
                    special == null && otherSpecial != null -> -1
                    special != null && otherSpecial != null -> special.compareTo(otherSpecial)
                    else -> null
                }

            val devComparison =
                when {
                    name.lowercase() == dev && other.name.lowercase() == dev -> {
                        when {
                            num!! == other.num!! -> 0
                            num > other.num -> 1
                            num < other.num -> -1
                            else -> gradleVersionError("`dev` version stage must contain a `num`")
                        }
                    }
                    name.lowercase() != dev && other.name.lowercase() == dev -> 1
                    name.lowercase() == dev && other.name.lowercase() != dev -> -1
                    else -> null
                }

            return when {
                specialComparison != null -> specialComparison
                devComparison != null -> devComparison
                name > other.name -> 1
                name < other.name -> -1
                num != null && other.num == null -> 1
                num == null && other.num != null -> -1
                num != null && other.num != null && num > other.num -> 1
                num != null && other.num != null && num < other.num -> -1
                else -> 0
            }
        }

        override fun equals(other: Any?): Boolean {
            val otherStage = other as? Stage
            return when {
                otherStage == null -> false
                compareTo(otherStage) == 0 -> true
                else -> false
            }
        }

        override fun toString(): String = value

        override fun hashCode(): Int = value.hashCode()

        public companion object {
            public val stageRegex: Regex =
                Regex("""(?!SNAPSHOT\.\d)([a-zA-Z]+(\.\d+)|\bSNAPSHOT\b)""", IGNORE_CASE)

            public operator fun invoke(stage: String): Stage = Stage(stage)

            public operator fun invoke(name: String, num: Int?): Stage =
                if (num != null) Stage("$name.$num") else Stage(name)
        }
    }

    internal class SpecialStage
    private constructor(
        private val stage: Stage,
    ) : Comparable<SpecialStage> {

        override fun compareTo(other: SpecialStage): Int {
            val name = stage.name.lowercase()
            val num = stage.num
            val otherName = other.stage.name.lowercase()
            val otherNum = other.stage.num

            val hasSameName = name == otherName

            return when {
                hasSameName && num == otherNum -> 0
                hasSameName && num != null && otherNum != null && num > otherNum -> 1
                hasSameName && num != null && otherNum != null && num < otherNum -> -1
                name == sp && otherName == sp -> 0
                name == sp && otherName != sp -> 1
                name != sp && otherName == sp -> -1
                name == release && otherName == release -> 0
                name == release && otherName != release -> 1
                name != release && otherName == release -> -1
                name == ga && otherName == ga -> 0
                name == ga && otherName != ga -> 1
                name != ga && otherName == ga -> -1
                name == snapshot && otherName == snapshot -> 0
                name == snapshot && otherName != snapshot -> 1
                name != snapshot && otherName == snapshot -> -1
                name == rc && otherName == rc -> 0
                name == rc && otherName != rc -> 1
                name != rc && otherName == rc -> -1
                name == dev && otherName == dev -> 0
                name == dev && otherName != dev -> 1
                name != dev && otherName == dev -> -1
                else -> {
                    val invalidStage = "Invalid stage, it must be one of ${specials.joinToString()}"
                    gradleVersionError(invalidStage)
                }
            }
        }

        companion object {

            internal const val dev = "dev"
            internal const val rc = "rc"
            internal const val snapshot = "snapshot"
            internal const val ga = "ga"
            internal const val release = "release"
            internal const val sp = "sp"

            internal val specials = listOf(rc, snapshot, ga, release, sp)

            operator fun invoke(stage: Stage): SpecialStage? =
                if (specials.any { it.lowercase() == stage.name.lowercase() }) SpecialStage(stage)
                else null
        }
    }

    public sealed interface Increase {
        public object Major : Increase

        public object Minor : Increase

        public object Patch : Increase
    }
}

private fun String.red() = "$RED$this$RESET"

private fun checkFullVersion(version: String) {
    checkVersion(version.matches(GradleVersion.regex)) {
        """|The version is not semantic, rules:
           |  - `major`, `minor` and `patch` are required, rest are optional
           |  - `num` is required if `stage` is present and it is not snapshot
           |
           |Current version: $version
           |
           |Samples of semantic versions:
           |1.0.0
           |1.0-alpha.1
           |1.0.0-SNAPSHOT
           |1.0.0-alpha.1
           |12.23.34-alpha.45
           |12.23.34-SNAPSHOT
           |
        """
            .trimMargin()
            .red()
    }
}

private fun checkStage(stage: String) {
    checkVersion(stage.matches(GradleVersion.Stage.stageRegex)) {
        """|`stage` provided has an incorrect format
           | 
           |Samples of stages:
           |alpha.1
           |beta.23
           |SNAPSHOT
        """
            .trimMargin()
            .red()
    }
}

public class GradleVersionException(override val message: String) : Exception(message)

internal fun gradleVersionError(message: String): Nothing = throw GradleVersionException(message)

@OptIn(ExperimentalContracts::class)
private inline fun checkVersion(value: Boolean, lazyMessage: () -> Any) {
    contract { returns() implies value }
    if (!value) gradleVersionError(lazyMessage().toString())
}

private fun buildVersion(
    major: Int,
    minor: Int,
    patch: Int?,
    stageName: String?,
    stageNum: Int?,
): String = buildString {
    append(major)
    append(".")
    append(minor)
    append(".")
    append(patch)
    if (!stageName.isNullOrBlank()) {
        append("-")
        append(GradleVersion.Stage(stageName, stageNum).toString())
    }
}

private const val RED = "\u001b[31m"
private const val RESET = "\u001B[0m"
