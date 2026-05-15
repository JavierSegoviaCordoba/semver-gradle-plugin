@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.features.plugin.api.SemverDefinition
import com.javiersc.semver.features.plugin.api.SemverMapVersionsDefinition
import com.javiersc.semver.features.plugin.api.SemverMapVersionsRuleDefinition
import com.javiersc.semver.features.plugin.api.SemverVersionDefinition
import com.javiersc.semver.shared.valuesources.SemverVersionValueSourceParams
import com.javiersc.semver.shared.valuesources.configureSemverVersionValueSourceParams
import com.javiersc.semver.shared.valuesources.obtainSemverVersion
import java.io.Serializable
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.kotlin.dsl.of

internal abstract class DclVersionValueSource : ValueSource<String, DclVersionValueSource.Params> {

    override fun obtain(): String = parameters.obtainSemverVersion { version: GradleVersion ->
        DclVersionMapper(parameters.toDclVersionMapping()).map(version)
    }

    internal interface Params : SemverVersionValueSourceParams {
        val overrideVersion: Property<String>
        val ruleMappings: ListProperty<RuleMapping>
        val mappingMajor: MapProperty<String, Int>
        val mappingMinor: MapProperty<String, Int>
        val mappingPatch: MapProperty<String, Int>
        val mappingStageName: MapProperty<String, String>
        val mappingStageNum: MapProperty<String, Int>
        val mappingCommits: MapProperty<String, Int>
        val mappingHash: MapProperty<String, String>
        val mappingMetadata: MapProperty<String, String>
    }

    internal companion object {

        fun register(
            project: Project,
            gitDir: Provider<out Directory>,
            commitsMaxCount: Provider<Int>,
            tagPrefix: Provider<String>,
            definition: SemverDefinition,
        ): Provider<String> =
            project.providers.of(DclVersionValueSource::class) { valueSourceSpec ->
                val parameters: Params = valueSourceSpec.parameters

                project.configureSemverVersionValueSourceParams(
                    parameters = parameters,
                    gitDir = gitDir,
                    commitsMaxCount = commitsMaxCount,
                    tagPrefix = tagPrefix,
                )
                parameters.overrideVersion.set(definition.overrideVersion)
                parameters.putMapping(DefaultMappingKey, definition.mapVersion)
                parameters.ruleMappings.set(project.provider { definition.toRuleMappings() })
            }
    }
}

private const val DefaultMappingKey: String = "<default>"

private data class DclVersionMapping(
    val overrideVersion: String?,
    val defaultMapping: VersionMapping,
    val ruleMappings: List<RuleMapping>,
)

private data class DclVersionMapper(private val mapping: DclVersionMapping) {

    fun map(version: GradleVersion): String {
        val mappedByRule: String? =
            mapping.ruleMappings
                .firstOrNull { rule: RuleMapping -> rule.rule.matches(version) }
                ?.let { rule: RuleMapping -> rule.mapping.map(version).toString() }

        return mappedByRule
            ?: mapping.overrideVersion
            ?: mapping.defaultMapping.map(version).toString()
    }
}

internal data class RuleMapping(
    val declarationIndex: Int,
    val priority: Int?,
    val mapping: VersionMapping,
    val rule: VersionRule,
) : Serializable {

    private companion object {
        private const val serialVersionUID: Long = 0L
    }
}

internal data class VersionMapping(
    val major: Int?,
    val minor: Int?,
    val patch: Int?,
    val stageName: String?,
    val stageNum: Int?,
    val commits: Int?,
    val hash: String?,
    val metadata: String?,
) : Serializable {

    fun map(version: GradleVersion): GradleVersion =
        version.copy(
            major = major ?: version.major,
            minor = minor ?: version.minor,
            patch = patch ?: version.patch,
            stageName = stageName ?: version.stage?.name,
            stageNum = stageNum ?: version.stage?.num,
            commits = commits ?: version.commits,
            hash = hash ?: version.hash,
            metadata = metadata ?: version.metadata,
        )

    private companion object {
        private const val serialVersionUID: Long = 0L
    }
}

internal data class VersionRule(
    val all: VersionMatches,
    val any: VersionMatches,
    val none: VersionMatches,
) : Serializable {

    fun matches(version: GradleVersion): Boolean {
        val configuredGroups: List<Boolean> =
            listOf(all.isConfigured, any.isConfigured, none.isConfigured)
        if (configuredGroups.none { configured: Boolean -> configured }) return false

        return (all.isConfigured && all.matchesAll(version)) ||
            (any.isConfigured && any.matchesAny(version)) ||
            (none.isConfigured && none.matchesNone(version))
    }

    private companion object {
        private const val serialVersionUID: Long = 0L
    }
}

internal data class VersionMatches(
    val metadataIsPresent: Boolean?,
    val requestedTagPrefix: Boolean?,
    val contains: Map<String, Boolean>,
    val endsWith: Map<String, Boolean>,
    val patterns: Map<String, Boolean>,
    val startsWith: Map<String, Boolean>,
) : Serializable {

    val isConfigured: Boolean =
        metadataIsPresent != null ||
            requestedTagPrefix != null ||
            contains.isNotEmpty() ||
            endsWith.isNotEmpty() ||
            patterns.isNotEmpty() ||
            startsWith.isNotEmpty()

    fun matchesAll(version: GradleVersion): Boolean =
        matchResults(version).all { result: Boolean -> result }

    fun matchesAny(version: GradleVersion): Boolean =
        matchResults(version).any { result: Boolean -> result }

    fun matchesNone(version: GradleVersion): Boolean =
        matchResults(version).none { result: Boolean -> result }

    private fun matchResults(version: GradleVersion): List<Boolean> {
        val value: String = version.toString()
        return buildList {
            metadataIsPresent?.let { expected: Boolean ->
                add((version.metadata != null) == expected)
            }
            requestedTagPrefix?.let { expected: Boolean -> add(expected) }
            contains.forEach { (element: String, ignoreCase: Boolean) ->
                add(value.contains(element, ignoreCase))
            }
            endsWith.forEach { (element: String, ignoreCase: Boolean) ->
                add(value.endsWith(element, ignoreCase))
            }
            patterns.forEach { (pattern: String, ignoreCase: Boolean) ->
                val options: Set<RegexOption> =
                    if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()
                add(Regex(pattern, options).containsMatchIn(value))
            }
            startsWith.forEach { (element: String, ignoreCase: Boolean) ->
                add(value.startsWith(element, ignoreCase))
            }
        }
    }

    private companion object {
        private const val serialVersionUID: Long = 0L
    }
}

private fun DclVersionValueSource.Params.toDclVersionMapping(): DclVersionMapping {
    return DclVersionMapping(
        overrideVersion = overrideVersion.orNull,
        defaultMapping = toVersionMapping(DefaultMappingKey),
        ruleMappings = ruleMappings.getOrElse(emptyList()).sortedByPriority(),
    )
}

private fun DclVersionValueSource.Params.toVersionMapping(mappingKey: String): VersionMapping =
    VersionMapping(
        major = mappingMajor.getOrElse(emptyMap())[mappingKey],
        minor = mappingMinor.getOrElse(emptyMap())[mappingKey],
        patch = mappingPatch.getOrElse(emptyMap())[mappingKey],
        stageName = mappingStageName.getOrElse(emptyMap())[mappingKey],
        stageNum = mappingStageNum.getOrElse(emptyMap())[mappingKey],
        commits = mappingCommits.getOrElse(emptyMap())[mappingKey],
        hash = mappingHash.getOrElse(emptyMap())[mappingKey],
        metadata = mappingMetadata.getOrElse(emptyMap())[mappingKey],
    )

private fun DclVersionValueSource.Params.putMapping(
    mappingKey: String,
    mapping: SemverVersionDefinition,
) {
    mappingMajor.put(mappingKey, mapping.major.value)
    mappingMinor.put(mappingKey, mapping.minor.value)
    mappingPatch.put(mappingKey, mapping.patch.value)
    mappingStageName.put(mappingKey, mapping.stage.name.value)
    mappingStageNum.put(mappingKey, mapping.stage.number.value)
    mappingCommits.put(mappingKey, mapping.commits.value)
    mappingHash.put(mappingKey, mapping.hash.value)
    mappingMetadata.put(mappingKey, mapping.metadata.value)
}

private fun SemverDefinition.toRuleMappings(): List<RuleMapping> =
    mapVersions
        .flatMapIndexed { mappingIndex: Int, mapping: SemverMapVersionsDefinition ->
            mapping.rules.map { rule: SemverMapVersionsRuleDefinition ->
                RuleMapping(
                    declarationIndex = mappingIndex,
                    priority = rule.priority.orNull,
                    mapping = mapping.toVersionMapping(),
                    rule = rule.toVersionRule(),
                )
            }
        }
        .sortedByPriority()

private fun List<RuleMapping>.sortedByPriority(): List<RuleMapping> =
    sortedWith(
        compareByDescending<RuleMapping> { rule: RuleMapping -> rule.priority ?: Int.MIN_VALUE }
            .thenBy { rule: RuleMapping -> rule.declarationIndex }
    )

private fun SemverVersionDefinition.toVersionMapping(): VersionMapping =
    VersionMapping(
        major = major.value.orNull,
        minor = minor.value.orNull,
        patch = patch.value.orNull,
        stageName = stage.name.value.orNull,
        stageNum = stage.number.value.orNull,
        commits = commits.value.orNull,
        hash = hash.value.orNull,
        metadata = metadata.value.orNull,
    )

private fun SemverMapVersionsRuleDefinition.toVersionRule(): VersionRule =
    VersionRule(
        all = all.toVersionMatches(),
        any = any.toVersionMatches(),
        none = none.toVersionMatches(),
    )

private fun SemverMapVersionsRuleDefinition.SemverMapVersionsMatches.toVersionMatches():
    VersionMatches =
    VersionMatches(
        metadataIsPresent = conditions.metadataIsPresent.orNull,
        requestedTagPrefix = conditions.requestedTagPrefix.orNull,
        contains = contains.getOrElse(emptyMap()),
        endsWith = endsWith.getOrElse(emptyMap()),
        patterns = patterns.getOrElse(emptyMap()),
        startsWith = startsWith.getOrElse(emptyMap()),
    )
