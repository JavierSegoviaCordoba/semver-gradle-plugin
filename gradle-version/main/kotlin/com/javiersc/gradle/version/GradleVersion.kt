package com.javiersc.gradle.version

import com.javiersc.gradle.version.GradleVersion.CheckMode
import com.javiersc.gradle.version.GradleVersion.Companion.commitNumberRegex
import com.javiersc.gradle.version.GradleVersion.Companion.dotCommitsHashRegex
import com.javiersc.gradle.version.GradleVersion.Companion.hyphenStageRegex
import com.javiersc.gradle.version.GradleVersion.Companion.insignificantRegex
import com.javiersc.gradle.version.GradleVersion.Companion.metadataRegexWithPlus
import com.javiersc.gradle.version.GradleVersion.Companion.scopeRegex
import com.javiersc.gradle.version.GradleVersion.Companion.significantRegex
import com.javiersc.gradle.version.GradleVersion.Increase
import com.javiersc.gradle.version.GradleVersion.Stage
import com.javiersc.gradle.version.GradleVersionImpl.SpecialStage
import com.javiersc.gradle.version.SpecialStage.DEV
import com.javiersc.gradle.version.SpecialStage.GA
import com.javiersc.gradle.version.SpecialStage.RC
import com.javiersc.gradle.version.SpecialStage.RELEASE
import com.javiersc.gradle.version.SpecialStage.SNAPSHOT
import com.javiersc.gradle.version.SpecialStage.SP
import com.javiersc.gradle.version.SpecialStage.specials
import com.javiersc.kotlin.stdlib.isNotNullNorBlank
import com.javiersc.kotlin.stdlib.remove
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.text.RegexOption.IGNORE_CASE

public interface GradleVersion : Comparable<GradleVersion> {
    public val major: Int
    public val minor: Int
    public val patch: Int
    public val stage: Stage?
    public val commits: Int?
    public val hash: String?
    public val metadata: String?
    public val isSignificant: Boolean
    public val isInsignificant: Boolean
    public val isInvalid: Boolean

    public fun copy(
        major: Int = this.major,
        minor: Int = this.minor,
        patch: Int = this.patch,
        stageName: String? = this.stage?.name,
        stageNum: Int? = this.stage?.num,
        commits: Int? = this.commits,
        hash: String? = this.hash,
        metadata: String? = this.metadata,
        checkMode: CheckMode = CheckMode.Insignificant,
    ): GradleVersion

    public fun inc(number: Increase? = null, stageName: String = ""): GradleVersion

    public interface Stage : Comparable<Stage> {
        public val name: String
        public val num: Int?
    }

    public enum class CheckMode {
        Significant,
        Insignificant,
        None,
    }

    public sealed interface Increase {
        public object Major : Increase

        public object Minor : Increase

        public object Patch : Increase
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

        public val commitNumberRegex: Regex = Regex("""^\d+\.\d+\.\d+(?:-\w+\.\d+)?\.(\d+)\+""")

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
            checkMode: CheckMode = CheckMode.Insignificant,
        ): Result<GradleVersion> = runCatching { GradleVersion(value, checkMode) }

        public fun safe(
            version: String,
            stage: String?,
            checkMode: CheckMode = CheckMode.Insignificant,
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
            checkMode: CheckMode = CheckMode.Insignificant,
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
                checkMode,
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
            checkMode: CheckMode = CheckMode.Insignificant,
        ): GradleVersion? =
            safe(major, minor, patch, stageName, stageNum, commits, hash, metadata, checkMode)
                .getOrNull()

        public fun Stage(stage: String): Stage = StageImpl(stage)

        public fun Stage(name: String, num: Int?): Stage =
            if (num != null) Stage("$name.$num") else Stage(name)
    }
}

public fun GradleVersion(
    value: String,
    checkMode: CheckMode = CheckMode.Insignificant,
): GradleVersion = GradleVersionImpl(value = value, checkMode = checkMode)

public fun GradleVersion(
    version: String,
    stage: String?,
    checkMode: CheckMode = CheckMode.Insignificant,
): GradleVersion =
    if (stage.isNullOrBlank()) GradleVersion(version, checkMode)
    else GradleVersion("$version-$stage", checkMode)

public fun GradleVersion(
    major: Int,
    minor: Int,
    patch: Int,
    stageName: String?,
    stageNum: Int?,
    checkMode: CheckMode = CheckMode.Insignificant,
): GradleVersion =
    GradleVersion(
        value = buildVersion(major, minor, patch, stageName, stageNum, null, null, null),
        checkMode = checkMode,
    )

public fun GradleVersion(
    major: Int,
    minor: Int,
    patch: Int,
    stageName: String?,
    stageNum: Int?,
    commits: Int?,
    hash: String?,
    metadata: String?,
    checkMode: CheckMode = CheckMode.Insignificant,
): GradleVersion =
    GradleVersion(
        buildVersion(major, minor, patch, stageName, stageNum, commits, hash, metadata),
        checkMode,
    )

private class GradleVersionImpl(
    private val value: String,
    checkMode: CheckMode = CheckMode.Insignificant,
) : GradleVersion {

    init {
        if (checkMode == CheckMode.Significant) checkSignificantVersion(value)
        if (checkMode == CheckMode.Insignificant) checkInsignificantVersion(value)
    }

    override val major: Int = scope.split(".").first().toInt()

    override val minor: Int = scope.split(".")[1].toInt()

    override val patch: Int = scope.split(".").getOrNull(2)?.toInt() ?: 0

    override val stage: Stage? =
        hyphenStageRegex.find(value)?.value?.remove("-")?.let(GradleVersion::Stage)

    override val commits: Int? = run {
        val commitsNumber: String? = commitNumberRegex.find(value)?.groupValues?.get(1)
        commitsNumber?.toInt()
    }

    override val hash: String? = dotCommitsHashRegex.find(value)?.value?.substringAfter("+")

    override val metadata: String? = run {
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

    override val isSignificant: Boolean = significantRegex.matches(value)

    override val isInsignificant: Boolean = insignificantRegex.matches(value)

    override val isInvalid: Boolean = !isSignificant && !isInsignificant

    @Suppress("ComplexMethod")
    override fun compareTo(other: GradleVersion): Int {
        val otherStage: Stage? = other.stage
        return when {
            major > other.major -> 1
            major < other.major -> -1
            minor > other.minor -> 1
            minor < other.minor -> -1
            patch > other.patch -> 1
            patch < other.patch -> -1
            stage == null && otherStage != null -> 1
            stage != null && otherStage == null -> -1
            stage != null && otherStage != null -> {
                val stageComparison: Int = stage.compareTo(otherStage)
                if (stageComparison == 0) this.compareAfterStage(other) else stageComparison
            }

            else -> this.compareAfterStage(other)
        }
    }

    @Suppress("ComplexMethod")
    override fun inc(number: Increase?, stageName: String): GradleVersion {
        val incNum: Int? = if (stageName.lowercase() == "snapshot") null else 1
        val nextVersion: GradleVersion =
            when (number) {
                null if stageName.isBlank() && !stage?.name.isNullOrBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch,
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }
                null if stageName.isBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }
                null if stageName.isNotBlank() && stage?.name.isNullOrBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }
                null if stageName.isNotBlank() && stageName == stage?.name -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch,
                        stageName = stageName,
                        stageNum = stage.num?.inc(),
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }
                null if stageName.isNotBlank() && stageName != stage?.name -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch,
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }
                is Increase.Major if stageName.isBlank() -> {
                    GradleVersion(
                        major = major.inc(),
                        minor = 0,
                        patch = 0,
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }

                is Increase.Minor if stageName.isBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor.inc(),
                        patch = 0,
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }

                is Increase.Patch if stageName.isBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = null,
                        stageNum = null,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }

                is Increase.Major if stageName.isNotBlank() -> {
                    GradleVersion(
                        major = major.inc(),
                        minor = 0,
                        patch = 0,
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }

                is Increase.Minor if stageName.isNotBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor.inc(),
                        patch = 0,
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }

                is Increase.Patch if stageName.isNotBlank() -> {
                    GradleVersion(
                        major = major,
                        minor = minor,
                        patch = patch.inc(),
                        stageName = stageName,
                        stageNum = incNum,
                        commits = commits,
                        hash = hash,
                        metadata = metadata,
                    )
                }

                else -> null
            } ?: gradleVersionError("There were an error configuring the version")

        if (nextVersion < this) {
            gradleVersionError(
                "Next version ($nextVersion) should be higher than the current one ($this)"
            )
        }
        return nextVersion
    }

    override fun copy(
        major: Int,
        minor: Int,
        patch: Int,
        stageName: String?,
        stageNum: Int?,
        commits: Int?,
        hash: String?,
        metadata: String?,
        checkMode: CheckMode,
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

    private val scope: String
        get() = scopeRegex.find(value)?.value ?: gradleVersionError("Incorrect version: $value")

    private fun GradleVersion.compareAfterStage(other: GradleVersion): Int {
        val commits: Int? = commits
        val otherCommits: Int? = other.commits
        val metadata: String? = metadata
        val otherMetadata: String? = other.metadata
        return when {
            commits != null && otherCommits == null -> 1
            commits == null && otherCommits != null -> -1
            commits != null && otherCommits != null -> commits.compareTo(otherCommits)
            metadata != null && otherMetadata == null -> 1
            metadata == null && otherMetadata != null -> -1
            metadata != null && otherMetadata != null -> metadata.compareTo(otherMetadata)
            else -> 0
        }
    }

    class SpecialStage private constructor(private val stage: Stage) : Comparable<SpecialStage> {

        override fun compareTo(other: SpecialStage): Int {
            val name = stage.name.lowercase()
            val num = stage.num
            val otherName = other.stage.name.lowercase()
            val otherNum = other.stage.num

            val hasSameName = name == otherName

            return when {
                hasSameName && num == otherNum -> 0
                hasSameName && num != null && otherNum != null && num > otherNum -> 1
                hasSameName && num != null && otherNum != null -> -1
                name == SP && otherName == SP -> 0
                name == SP && otherName != SP -> 1
                name != SP && otherName == SP -> -1
                name == RELEASE && otherName == RELEASE -> 0
                name == RELEASE && otherName != RELEASE -> 1
                name != RELEASE && otherName == RELEASE -> -1
                name == GA && otherName == GA -> 0
                name == GA && otherName != GA -> 1
                name != GA && otherName == GA -> -1
                name == SNAPSHOT && otherName == SNAPSHOT -> 0
                name == SNAPSHOT && otherName != SNAPSHOT -> 1
                name != SNAPSHOT && otherName == SNAPSHOT -> -1
                name == RC && otherName == RC -> 0
                name == RC && otherName != RC -> 1
                name != RC && otherName == RC -> -1
                name == DEV && otherName == DEV -> 0
                name == DEV && otherName != DEV -> 1
                name != DEV && otherName == DEV -> -1
                else -> {
                    val invalidStage = "Invalid stage, it must be one of ${specials.joinToString()}"
                    gradleVersionError(invalidStage)
                }
            }
        }

        companion object {
            operator fun invoke(stage: Stage): SpecialStage? =
                if (specials.any { it.equals(stage.name, ignoreCase = true) }) SpecialStage(stage)
                else null
        }
    }
}

private class StageImpl(private val value: String) : Stage {

    init {
        checkStage(value)
    }

    override val name: String = value.split(".").first()

    override val num: Int? = value.split(".").getOrNull(1)?.toInt()

    @Suppress("ComplexMethod")
    override fun compareTo(other: Stage): Int {
        val special = SpecialStage(this)
        val otherSpecial = SpecialStage(other)
        val otherNum: Int? = other.num

        val specialComparison: Int? =
            when {
                special != null && otherSpecial == null -> 1
                special == null && otherSpecial != null -> -1
                special != null && otherSpecial != null -> special.compareTo(otherSpecial)
                else -> null
            }

        val devComparison: Int? =
            when {
                name.lowercase() == DEV && other.name.lowercase() == DEV -> {
                    when {
                        num!! == otherNum!! -> 0
                        num > otherNum -> 1
                        num < otherNum -> -1
                        else -> gradleVersionError("`dev` version stage must contain a `num`")
                    }
                }

                name.lowercase() != DEV && other.name.lowercase() == DEV -> 1
                name.lowercase() == DEV && other.name.lowercase() != DEV -> -1
                else -> null
            }

        return when {
            specialComparison != null -> specialComparison
            devComparison != null -> devComparison
            name > other.name -> 1
            name < other.name -> -1
            num != null && otherNum == null -> 1
            num == null && otherNum != null -> -1
            num != null && otherNum != null && num > otherNum -> 1
            num != null && otherNum != null && num < otherNum -> -1
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

internal object SpecialStage {
    const val DEV = "dev"
    const val RC = "rc"
    const val SNAPSHOT = "snapshot"
    const val GA = "ga"
    const val RELEASE = "release"
    const val SP = "sp"

    val specials = listOf(RC, SNAPSHOT, GA, RELEASE, SP)
}

private fun String.red() = "$RED$this$RESET"

private fun checkSignificantVersion(version: String) {
    checkVersion(version.matches(significantRegex)) {
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
           |Samples of semantic versions:
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
    checkVersion(version.matches(insignificantRegex)) {
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
           |Samples of semantic versions:
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

private fun checkStage(stage: String) {
    checkVersion(stage.matches(GradleVersion.stageRegex)) {
        """|`stage` provided has an incorrect format
           |
           |Current stage: $stage
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

// TODO: MAKE IT PRIVATE
internal fun buildVersion(
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
