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
        val conditionEnvironmentVariables: MapProperty<String, String>
        val conditionGradleProperties: MapProperty<String, String>
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
                parameters.putConditionProviders(project, definition)
                parameters.ruleMappings.set(project.provider { definition.toRuleMappings() })
            }
    }
}

private const val DefaultMappingKey: String = "<default>"

private data class DclVersionMapping(
    val overrideVersion: String?,
    val defaultMapping: VersionMapping,
    val ruleMappings: List<RuleMapping>,
    val conditionEnvironmentVariables: Map<String, String>,
    val conditionGradleProperties: Map<String, String>,
)

private data class DclVersionMapper(private val mapping: DclVersionMapping) {

    fun map(version: GradleVersion): String {
        val mappedByRule: String? =
            mapping.ruleMappings
                .firstOrNull { rule: RuleMapping ->
                    rule.rule.matches(
                        version = version,
                        mapping = rule.mapping,
                        environmentVariables = mapping.conditionEnvironmentVariables,
                        gradleProperties = mapping.conditionGradleProperties,
                    )
                }
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
) : Serializable

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
}

internal data class VersionRule(
    val all: VersionMatches,
    val any: VersionMatches,
    val none: VersionMatches,
) : Serializable {

    fun matches(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariables: Map<String, String>,
        gradleProperties: Map<String, String>,
    ): Boolean {
        val configuredGroups: List<Boolean> =
            listOf(all.isConfigured, any.isConfigured, none.isConfigured)

        val allMatches: Boolean =
            all.matchesAll(version, mapping, environmentVariables, gradleProperties)
        val anyMatches: Boolean =
            any.matchesAny(version, mapping, environmentVariables, gradleProperties)
        val noneMatches: Boolean =
            none.matchesNone(version, mapping, environmentVariables, gradleProperties)

        val allOrAnyOrNone: Boolean =
            (all.isConfigured && allMatches) ||
                (any.isConfigured && anyMatches) ||
                (none.isConfigured && noneMatches)

        return !configuredGroups.none { configured: Boolean -> configured } && allOrAnyOrNone
    }
}

internal data class VersionMatches(
    val mappedCommits: Int?,
    val mappedCommitsIsPresent: Boolean?,
    val mappedHash: String?,
    val mappedHashIgnoreCase: Boolean?,
    val mappedHashIsPresent: Boolean?,
    val mappedMajor: Int?,
    val mappedMajorIsPresent: Boolean?,
    val mappedMetadata: Map<String, Boolean>,
    val mappedMetadataIsPresent: Boolean?,
    val mappedMinor: Int?,
    val mappedMinorIsPresent: Boolean?,
    val mappedPatch: Int?,
    val mappedPatchIsPresent: Boolean?,
    val mappedStageIsPresent: Boolean?,
    val mappedStageName: String?,
    val mappedStageNameIgnoreCase: Boolean?,
    val mappedStageNameIsPresent: Boolean?,
    val mappedStageNumber: Int?,
    val mappedStageNumberIsPresent: Boolean?,
    val metadataIsPresent: Boolean?,
    val requestedTagPrefix: Boolean?,
    val contains: Map<String, Boolean>,
    val endsWith: Map<String, Boolean>,
    val environmentVariables: Map<String, Boolean>,
    val mappedContains: Map<String, Boolean>,
    val mappedEndsWith: Map<String, Boolean>,
    val mappedPatterns: Map<String, Boolean>,
    val mappedStartsWith: Map<String, Boolean>,
    val patterns: Map<String, Boolean>,
    val gradleProperties: Map<String, Boolean>,
    val startsWith: Map<String, Boolean>,
) : Serializable {

    val isConfigured: Boolean =
        mappedCommits != null ||
            mappedCommitsIsPresent != null ||
            mappedHash != null ||
            mappedHashIsPresent != null ||
            mappedMajor != null ||
            mappedMajorIsPresent != null ||
            mappedMetadata.isNotEmpty() ||
            mappedMetadataIsPresent != null ||
            mappedMinor != null ||
            mappedMinorIsPresent != null ||
            mappedPatch != null ||
            mappedPatchIsPresent != null ||
            mappedStageIsPresent != null ||
            mappedStageName != null ||
            mappedStageNameIsPresent != null ||
            mappedStageNumber != null ||
            mappedStageNumberIsPresent != null ||
            metadataIsPresent != null ||
            requestedTagPrefix != null ||
            contains.isNotEmpty() ||
            endsWith.isNotEmpty() ||
            environmentVariables.isNotEmpty() ||
            mappedContains.isNotEmpty() ||
            mappedEndsWith.isNotEmpty() ||
            mappedPatterns.isNotEmpty() ||
            mappedStartsWith.isNotEmpty() ||
            patterns.isNotEmpty() ||
            gradleProperties.isNotEmpty() ||
            startsWith.isNotEmpty()

    fun matchesAll(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariables: Map<String, String>,
        gradleProperties: Map<String, String>,
    ): Boolean =
        matchResults(version, mapping, environmentVariables, gradleProperties).all { result: Boolean
            ->
            result
        }

    fun matchesAny(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariables: Map<String, String>,
        gradleProperties: Map<String, String>,
    ): Boolean =
        matchResults(version, mapping, environmentVariables, gradleProperties).any { result: Boolean
            ->
            result
        }

    fun matchesNone(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariables: Map<String, String>,
        gradleProperties: Map<String, String>,
    ): Boolean =
        matchResults(version, mapping, environmentVariables, gradleProperties).none {
            result: Boolean ->
            result
        }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private fun matchResults(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariableValues: Map<String, String>,
        gradlePropertyValues: Map<String, String>,
    ): List<Boolean> {
        val value: String = version.toString()
        val mappedVersion: GradleVersion = mapping.map(version)
        val mappedValue: String = mappedVersion.toString()
        return buildList {
            mappedCommits?.let { expected: Int -> add(mappedVersion.commits == expected) }
            mappedCommitsIsPresent?.let { expected: Boolean ->
                add((mappedVersion.commits != null) == expected)
            }
            mappedHash?.let { expected: String ->
                add(mappedVersion.hash?.equals(expected, mappedHashIgnoreCase ?: false) == true)
            }
            mappedHashIsPresent?.let { expected: Boolean ->
                add((mappedVersion.hash != null) == expected)
            }
            mappedMajor?.let { expected: Int -> add(mappedVersion.major == expected) }
            mappedMajorIsPresent?.let { expected: Boolean -> add(true == expected) }
            mappedMetadata.forEach { (expected: String, ignoreCase: Boolean) ->
                add(mappedVersion.metadata?.equals(expected, ignoreCase) == true)
            }
            mappedMetadataIsPresent?.let { expected: Boolean ->
                add((mappedVersion.metadata != null) == expected)
            }
            mappedMinor?.let { expected: Int -> add(mappedVersion.minor == expected) }
            mappedMinorIsPresent?.let { expected: Boolean -> add(true == expected) }
            mappedPatch?.let { expected: Int -> add(mappedVersion.patch == expected) }
            mappedPatchIsPresent?.let { expected: Boolean -> add(true == expected) }
            mappedStageIsPresent?.let { expected: Boolean ->
                add((mappedVersion.stage != null) == expected)
            }
            mappedStageName?.let { expected: String ->
                add(
                    mappedVersion.stage
                        ?.name
                        ?.equals(expected, mappedStageNameIgnoreCase ?: false) == true
                )
            }
            mappedStageNameIsPresent?.let { expected: Boolean ->
                add((mappedVersion.stage?.name != null) == expected)
            }
            mappedStageNumber?.let { expected: Int -> add(mappedVersion.stage?.num == expected) }
            mappedStageNumberIsPresent?.let { expected: Boolean ->
                add((mappedVersion.stage?.num != null) == expected)
            }
            mappedContains.forEach { (element: String, ignoreCase: Boolean) ->
                add(mappedValue.contains(element, ignoreCase))
            }
            mappedEndsWith.forEach { (element: String, ignoreCase: Boolean) ->
                add(mappedValue.endsWith(element, ignoreCase))
            }
            mappedPatterns.forEach { (pattern: String, ignoreCase: Boolean) ->
                val options: Set<RegexOption> =
                    if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()
                add(Regex(pattern, options).containsMatchIn(mappedValue))
            }
            mappedStartsWith.forEach { (element: String, ignoreCase: Boolean) ->
                add(mappedValue.startsWith(element, ignoreCase))
            }
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
            environmentVariables.forEach { (name: String, expected: Boolean) ->
                add(environmentVariableValues.containsKey(name) == expected)
            }
            patterns.forEach { (pattern: String, ignoreCase: Boolean) ->
                val options: Set<RegexOption> =
                    if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()
                add(Regex(pattern, options).containsMatchIn(value))
            }
            gradleProperties.forEach { (name: String, expected: Boolean) ->
                add(gradlePropertyValues.containsKey(name) == expected)
            }
            startsWith.forEach { (element: String, ignoreCase: Boolean) ->
                add(value.startsWith(element, ignoreCase))
            }
        }
    }
}

private fun DclVersionValueSource.Params.toDclVersionMapping(): DclVersionMapping {
    return DclVersionMapping(
        overrideVersion = overrideVersion.orNull,
        defaultMapping = toVersionMapping(DefaultMappingKey),
        ruleMappings = ruleMappings.getOrElse(emptyList()).sortedByPriority(),
        conditionEnvironmentVariables = conditionEnvironmentVariables.getOrElse(emptyMap()),
        conditionGradleProperties = conditionGradleProperties.getOrElse(emptyMap()),
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

private fun DclVersionValueSource.Params.putConditionProviders(
    project: Project,
    definition: SemverDefinition,
) {
    definition.conditionEnvironmentVariableNames().forEach { name: String ->
        conditionEnvironmentVariables.put(name, project.providers.environmentVariable(name))
    }
    definition.conditionGradlePropertyNames().forEach { name: String ->
        conditionGradleProperties.put(name, project.providers.gradleProperty(name))
    }
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
        compareByDescending { rule: RuleMapping -> rule.priority ?: Int.MIN_VALUE }
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
        mappedCommits = conditions.mappedCommits.orNull,
        mappedCommitsIsPresent = conditions.mappedCommitsIsPresent.orNull,
        mappedHash = conditions.mappedHash.orNull,
        mappedHashIgnoreCase = conditions.mappedHashIgnoreCase.orNull,
        mappedHashIsPresent = conditions.mappedHashIsPresent.orNull,
        mappedMajor = conditions.mappedMajor.orNull,
        mappedMajorIsPresent = conditions.mappedMajorIsPresent.orNull,
        mappedMetadata = mappedMetadata.getOrElse(emptyMap()),
        mappedMetadataIsPresent = conditions.mappedMetadataIsPresent.orNull,
        mappedMinor = conditions.mappedMinor.orNull,
        mappedMinorIsPresent = conditions.mappedMinorIsPresent.orNull,
        mappedPatch = conditions.mappedPatch.orNull,
        mappedPatchIsPresent = conditions.mappedPatchIsPresent.orNull,
        mappedStageIsPresent = conditions.mappedStageIsPresent.orNull,
        mappedStageName = conditions.mappedStageName.orNull,
        mappedStageNameIgnoreCase = conditions.mappedStageNameIgnoreCase.orNull,
        mappedStageNameIsPresent = conditions.mappedStageNameIsPresent.orNull,
        mappedStageNumber = conditions.mappedStageNumber.orNull,
        mappedStageNumberIsPresent = conditions.mappedStageNumberIsPresent.orNull,
        metadataIsPresent = conditions.metadataIsPresent.orNull,
        requestedTagPrefix = conditions.requestedTagPrefix.orNull,
        contains = contains.getOrElse(emptyMap()),
        endsWith = endsWith.getOrElse(emptyMap()),
        environmentVariables = environmentVariables.getOrElse(emptyMap()),
        mappedContains = mappedContains.getOrElse(emptyMap()),
        mappedEndsWith = mappedEndsWith.getOrElse(emptyMap()),
        mappedPatterns = mappedPatterns.getOrElse(emptyMap()),
        mappedStartsWith = mappedStartsWith.getOrElse(emptyMap()),
        patterns = patterns.getOrElse(emptyMap()),
        gradleProperties = gradleProperties.getOrElse(emptyMap()),
        startsWith = startsWith.getOrElse(emptyMap()),
    )

private fun SemverDefinition.conditionEnvironmentVariableNames(): Set<String> =
    mapVersions.flatMapTo(mutableSetOf()) { mapping: SemverMapVersionsDefinition ->
        mapping.rules.flatMapTo(mutableSetOf()) { rule: SemverMapVersionsRuleDefinition ->
            rule.conditionEnvironmentVariableNames()
        }
    }

private fun SemverDefinition.conditionGradlePropertyNames(): Set<String> =
    mapVersions.flatMapTo(mutableSetOf()) { mapping: SemverMapVersionsDefinition ->
        mapping.rules.flatMapTo(mutableSetOf()) { rule: SemverMapVersionsRuleDefinition ->
            rule.conditionGradlePropertyNames()
        }
    }

private fun SemverMapVersionsRuleDefinition.conditionEnvironmentVariableNames(): Set<String> =
    setOf(all, any, none).flatMapTo(mutableSetOf()) {
        matches: SemverMapVersionsRuleDefinition.SemverMapVersionsMatches ->
        matches.environmentVariables.getOrElse(emptyMap()).keys
    }

private fun SemverMapVersionsRuleDefinition.conditionGradlePropertyNames(): Set<String> =
    setOf(all, any, none).flatMapTo(mutableSetOf()) {
        matches: SemverMapVersionsRuleDefinition.SemverMapVersionsMatches ->
        matches.gradleProperties.getOrElse(emptyMap()).keys
    }
