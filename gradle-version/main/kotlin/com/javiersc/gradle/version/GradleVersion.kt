package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.CheckMode.Insignificant
import com.javiersc.gradle.version.GradleVersion.CheckMode.Significant
import com.javiersc.gradle.version.GradleVersion.SpecialStage.Companion.dev
import com.javiersc.kotlin.stdlib.isNotNullNorBlank
import com.javiersc.kotlin.stdlib.remove
import com.javiersc.kotlin.stdlib.second
import com.javiersc.kotlin.stdlib.thirdOrNull
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.text.RegexOption.IGNORE_CASE

public class GradleVersion(
    private val value: String,
    checkMode: CheckMode = Insignificant,
) : Comparable<GradleVersion> {

    public constructor(
        scope: String,
        stage: String? = null,
        checkMode: CheckMode = Insignificant,
    ) : this(if (stage.isNullOrBlank()) scope else "$scope-$stage", checkMode)

    public constructor(
        scope: Scope,
        stage: Stage? = null,
        checkMode: CheckMode = Insignificant,
    ) : this(if (stage == null) "$scope" else "$scope-$stage", checkMode)

    public constructor(
        major: Int,
        minor: Int,
        patch: Int,
        stageName: String? = null,
        stageNum: Int? = null,
        commits: Int? = null,
        hash: String? = null,
        metadata: String? = null,
        checkMode: CheckMode = Insignificant,
    ) : this(
        buildVersion(major, minor, patch, stageName, stageNum, commits, hash, metadata),
        checkMode,
    )

    public constructor(
        scope: Scope,
        stage: Stage?,
        commits: Int? = null,
        hash: String? = null,
        metadata: String? = null,
        checkMode: CheckMode = Insignificant,
    ) : this(
        scope.major,
        scope.minor,
        scope.patch,
        stage?.name,
        stage?.num,
        commits,
        hash,
        metadata,
        checkMode,
    )

    init {
        if (checkMode == Significant) checkSignificantVersion(value)
        if (checkMode == Insignificant) checkInsignificantVersion(value)
    }

    private val scope: Scope
        get() = Scope(checkScope(scopeRegex.find(value)?.value))

    public val major: Int = scope.major

    public val minor: Int = scope.minor

    public val patch: Int = scope.patch

    public val stage: Stage? = hyphenStageRegex.find(value)?.value?.remove("-")?.let(::Stage)

    public val stageName: String? = stage?.name

    public val stageNum: Int? = stage?.num

    public val commits: Int? = run {
        val commitsAndHashOrDirty: String? =
            dotCommitsHashRegex.find(value)?.value ?: dotCommitsPlusMetadataRegex.find(value)?.value
        commitsAndHashOrDirty?.drop(1)?.takeWhile(Char::isDigit)?.toIntOrNull()
    }

    public val hash: String? = dotCommitsHashRegex.find(value)?.value?.substringAfter("+")

    public val metadata: String? = run {
        val valuesToBeRemoved: List<String> = buildList {
            scopeRegex.find(value)?.value?.let(::add)
            hyphenStageRegex.find(value)?.value?.let(::add)
            dotCommitsHashRegex.find(value)?.value?.let(::add)
        }
        var sanitizedValue: String = value
        for (valueToBeRemoved: String in valuesToBeRemoved) {
            sanitizedValue = sanitizedValue.remove(valueToBeRemoved)
        }

        metadataRegexWithPlus.find(sanitizedValue)?.value?.substringAfter("+")
    }

    public val isSignificant: Boolean = significantRegex.matches(value)

    public val isInsignificant: Boolean = insignificantRegex.matches(value)

    public val isInvalid: Boolean = !isSignificant && !isInsignificant

    public val isDirty: Boolean = value.contains("dirty", ignoreCase = true)

    @Suppress("ComplexMethod")
    override fun compareTo(other: GradleVersion): Int =
        when {
            scope.compareTo(other.scope) != 0 -> scope.compareTo(other.scope)
            stage == null && other.stage != null -> 1
            stage != null && other.stage == null -> -1
            stage != null && other.stage != null -> stage.compareTo(other.stage)
            commits != null && other.commits == null -> 1
            commits == null && other.commits != null -> -1
            commits != null && other.commits != null -> commits.compareTo(other.commits)
            metadata != null && other.metadata == null -> 1
            metadata == null && other.metadata != null -> -1
            metadata != null && other.metadata != null -> metadata.compareTo(other.metadata)
            else -> 0
        }

    @Suppress("ComplexMethod")
    public fun inc(number: Increase? = null, stageName: String = ""): GradleVersion {
        val incNum: Int? = if (stageName.lowercase() == "snapshot") null else 1
        val nextVersion: GradleVersion =
            when {
                number == null && stageName.isBlank() && !stage?.name.isNullOrBlank() -> {
                    GradleVersion(
                        scope = scope,
                        stage = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number == null && stageName.isBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number == null && stageName.isNotBlank() && stage?.name.isNullOrBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number == null && stageName.isNotBlank() && stageName == stage?.name -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch,
                        stageName = stageName,
                        stageNum = stage.num?.inc(),
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number == null && stageName.isNotBlank() && stageName != stage?.name -> {
                    GradleVersion(
                        scope = scope,
                        stage = Stage(stageName, incNum),
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number is Increase.Major && stageName.isBlank() -> {
                    GradleVersion(
                        major = major.inc(),
                        minor = 0,
                        patch = 0,
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number is Increase.Minor && stageName.isBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor.inc(),
                        patch = 0,
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number is Increase.Patch && stageName.isBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number is Increase.Major && stageName.isNotBlank() -> {
                    GradleVersion(
                        major = major.inc(),
                        minor = 0,
                        patch = 0,
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number is Increase.Minor && stageName.isNotBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor.inc(),
                        patch = 0,
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
                }
                number is Increase.Patch && stageName.isNotBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata
                    )
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
        commits: Int? = this.commits,
        hash: String? = this.hash,
        metadata: String? = this.metadata,
        checkMode: CheckMode = Insignificant,
    ): GradleVersion =
        GradleVersion(
            major = major,
            minor = minor,
            patch = patch,
            stageName = stageName,
            stageNum = if (stageName.equals("SNAPSHOT", ignoreCase = true)) null else stageNum,
            commits = commits,
            hash = hash,
            metadata = metadata,
            checkMode = checkMode,
        )

    public fun copy(
        scope: Scope = this.scope,
        stage: Stage? = this.stage,
        commits: Int? = this.commits,
        hash: String? = this.hash,
        metadata: String? = this.metadata,
        checkMode: CheckMode = Insignificant,
    ): GradleVersion =
        GradleVersion(
            major = scope.major,
            minor = scope.minor,
            patch = scope.patch,
            stageName = stage?.name,
            stageNum = if (stage?.name.equals("SNAPSHOT", ignoreCase = true)) null else stage?.num,
            commits = commits,
            hash = hash,
            metadata = metadata,
            checkMode = checkMode,
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

    public enum class CheckMode {
        Significant,
        Insignificant,
        None,
    }

    public companion object {

        public val numRegex: Regex = Regex("""(0|[1-9]\d*)""")

        public val scopeRegex: Regex = Regex("""($numRegex\.$numRegex\.$numRegex)""")

        public val stageNoSnapshotRegex: Regex = Regex("""((?!snapshot)[a-zA-Z]+)(\.\d+)""")

        public val snapshotRegex: Regex = Regex("""(snapshot(?!.))""")

        public val stageRegex: Regex =
            Regex("""($stageNoSnapshotRegex|$snapshotRegex)""", IGNORE_CASE)

        public val hyphenStageRegex: Regex = Regex("""(-$stageRegex)""", IGNORE_CASE)

        public val hashRegex: Regex = Regex("""([A-Za-z0-9]{7})""")

        public val commitsHashRegex: Regex = Regex("""($numRegex)\+($hashRegex)""")

        public val dotCommitsHashRegex: Regex = Regex("""(\.$commitsHashRegex)""")

        public val metadataRegex: Regex = Regex("""([A-Za-z0-9.\-_]+)""")

        public val commitsPlusMetadataRegex: Regex = Regex("""(($numRegex)\+($metadataRegex))""")

        public val dotCommitsPlusMetadataRegex: Regex =
            Regex("""(\.($numRegex)\+($metadataRegex))""")

        public val metadataRegexWithPlus: Regex = Regex("""(\+$metadataRegex)""")

        public val significantRegex: Regex =
            Regex(
                pattern = """($scopeRegex)($hyphenStageRegex)?($metadataRegexWithPlus)?""",
                option = IGNORE_CASE,
            )

        public val insignificantRegex: Regex =
            Regex(
                pattern =
                    """($scopeRegex)($hyphenStageRegex)?($dotCommitsHashRegex)?($dotCommitsPlusMetadataRegex)?($metadataRegexWithPlus)?""",
                option = IGNORE_CASE,
            )

        public fun safe(
            value: String,
            checkMode: CheckMode = Insignificant,
        ): Result<GradleVersion> = runCatching { GradleVersion(value, checkMode) }

        public fun safe(
            version: String,
            stage: String?,
            checkMode: CheckMode = Insignificant,
        ): Result<GradleVersion> = runCatching {
            if (stage.isNullOrBlank()) GradleVersion(version, checkMode)
            else GradleVersion("$version-$stage", checkMode)
        }

        public fun safe(
            major: Int,
            minor: Int,
            patch: Int,
            stageName: String?,
            stageNum: Int?,
            commits: Int?,
            hash: String?,
            metadata: String?,
            checkMode: CheckMode = Insignificant,
        ): Result<GradleVersion> = runCatching {
            GradleVersion(
                major,
                minor,
                patch,
                stageName,
                stageNum,
                commits,
                hash,
                metadata,
                checkMode
            )
        }

        public fun getOrNull(
            major: Int,
            minor: Int,
            patch: Int,
            stageName: String?,
            stageNum: Int?,
            commits: Int?,
            hash: String?,
            metadata: String?,
            checkMode: CheckMode = Insignificant,
        ): GradleVersion? =
            safe(major, minor, patch, stageName, stageNum, commits, hash, metadata, checkMode)
                .getOrNull()
    }

    public class Scope(private val value: String) : Comparable<Scope> {

        public constructor(major: Int, minor: Int, patch: Int) : this("$major.$minor.$patch")

        init {
            checkScope(value)
        }

        private val splitValue: List<String> = value.split(".")

        public val major: Int = splitValue.first().toInt()

        public val minor: Int = splitValue.second().toInt()

        public val patch: Int = splitValue.thirdOrNull()?.toInt() ?: 0

        @Suppress("ComplexMethod")
        override fun compareTo(other: Scope): Int {
            return when {
                major > other.major -> 1
                major < other.major -> -1
                minor > other.minor -> 1
                minor < other.minor -> -1
                patch > other.patch -> 1
                patch < other.patch -> -1
                else -> 0
            }
        }

        override fun equals(other: Any?): Boolean {
            val otherScope: Scope? = other as? Scope
            return when {
                otherScope == null -> false
                compareTo(otherScope) == 0 -> true
                else -> false
            }
        }

        override fun toString(): String = value

        override fun hashCode(): Int = value.hashCode()
    }

    public class Stage(private val value: String) : Comparable<Stage> {

        public constructor(name: String, num: Int?) : this(if (num != null) "$name.$num" else name)

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

private fun checkSignificantVersion(version: String) {
    checkVersion(version.matches(GradleVersion.significantRegex)) {
        """|The version is not semantic and significant, rules:
           |  - `major`, `minor` and `patch` are required, separated by `.`
           |  - `stage` and `num` are required if one of them is present, except for snapshots
           |    - `stage` follows `-`
           |    - `num` follows `.`
           |  - `commits number` and `hash` are required if one of them is present
           |    - `commits number` follows `.`
           |    - `hash` follows `+`
           |  - `metadata` is optional, it follows `+`
           |
           |Valid version: <major>.<minor>.<patch>[-<stage>.<num>][.<commits number>+<hash>][+<metadata>]
           |
           |Current version: $version
           |
           |Samples of semantic version:
           |`1.0.0` // scope
           |`1.0-alpha.1` // scope + stage
           |`1.0.0-SNAPSHOT` // scope + stage
           |`1.0.0-alpha.1` // scope + stage
           |`12.23.34-alpha.45` // scope + stage
           |`12.23.34-SNAPSHOT` // scope + stage
           |`1.0.0+M3T4D4T4` // scope + metadata
           |
        """
            .trimMargin()
            .red()
    }
}

private fun checkInsignificantVersion(version: String) {
    checkVersion(version.matches(GradleVersion.insignificantRegex)) {
        """|The version is not semantic and insignificant, rules:
           |  - `major`, `minor` and `patch` are required, separated by `.`
           |  - `stage` and `num` are required if one of them is present, except for snapshots
           |    - `stage` follows `-`
           |    - `num` follows `.`
           |  - `commits number` and `hash` are required if one of them is present
           |    - `commits number` follows `.`
           |    - `hash` follows `+`
           |  - `metadata` is optional, it follows `+`
           |
           |Valid version: <major>.<minor>.<patch>[-<stage>.<num>][.<commits number>+<hash>][+<metadata>]
           |
           |Current version: $version
           |
           |Samples of semantic version:
           |`1.0.0` // scope
           |`1.0-alpha.1` // scope + stage
           |`1.0.0-SNAPSHOT` // scope + stage
           |`1.0.0-alpha.1` // scope + stage
           |`12.23.34-alpha.45` // scope + stage
           |`12.23.34-SNAPSHOT` // scope + stage
           |`1.0.0+M3T4D4T4` // scope + metadata
           |`1.0.0.10+H4SH345` // scope + commits + hash
           |`1.0.0.10+DIRTY` // scope + commits + dirty
           |`1.0.0-alpha.1.10+H4SH345` // scope + stage + hash + dirty
           |`1.0.0-alpha.1.10+DIRTY` // scope + stage + commits + dirty
           |`1.0.0.10+H4SH345+M3T4D4T4` // scope + commits + hash + metadata
           |`1.0.0.10+DIRTY+M3T4D4T4` // scope + commits + dirty + metadata
           |`1.0.0-alpha.1.10+H4SH345+M3T4D4T4` // scope + stage + hash + dirty + metadata
           |`1.0.0-alpha.1.10+DIRTY+M3T4D4T4` // scope + stage + commits + dirty + metadata
           |
        """
            .trimMargin()
            .red()
    }
}

private fun checkScope(scope: String?): String {
    contract { returns() implies (scope != null) }
    checkVersion(scope != null && scope.matches(GradleVersion.scopeRegex)) {
        """|`scope` provided has an incorrect format
           |
           |Current stage: $scope
           |
           |Samples of scope:
           |1.0.0
           |12.23.34
           |12.0.44
        """
            .trimMargin()
            .red()
    }
    return scope
}

private fun checkStage(stage: String) {
    checkVersion(stage.matches(GradleVersion.stageRegex)) {
        """|`stage` provided has an incorrect format
           |
           |Current stage: $stage
           |
           |Samples of stage:
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
    commits: Int?,
    hash: String?,
    metadata: String?,
): String = buildString {
    append(major)
    append(".")
    append(minor)
    append(".")
    append(patch)
    if (!stageName.equals("SNAPSHOT", ignoreCase = true)) {
        appendStage(stageName, stageNum)
    }
    if (commits != null) {
        append(".")
        append(commits)
    }
    if (hash != null) {
        append("+")
        append(hash)
    }
    if (metadata != null) {
        append("+")
        append(metadata)
    }
    if (stageName.equals("SNAPSHOT", ignoreCase = true)) {
        appendStage(stageName, stageNum)
    }
}

private fun StringBuilder.appendStage(stageName: String?, stageNum: Int?) {
    if (stageName.isNotNullNorBlank()) {
        append("-")
        append(GradleVersion.Stage(stageName, stageNum).toString())
    }
}

private const val RED = "\u001b[31m"
private const val RESET = "\u001B[0m"
