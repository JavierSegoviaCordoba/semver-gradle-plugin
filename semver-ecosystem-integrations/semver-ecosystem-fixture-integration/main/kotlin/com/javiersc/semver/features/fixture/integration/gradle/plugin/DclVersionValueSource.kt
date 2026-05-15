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
        if (configuredGroups.none { configured: Boolean -> configured }) return false

        return (all.isConfigured &&
            all.matchesAll(version, mapping, environmentVariables, gradleProperties)) ||
            (any.isConfigured &&
                any.matchesAny(version, mapping, environmentVariables, gradleProperties)) ||
            (none.isConfigured &&
                none.matchesNone(version, mapping, environmentVariables, gradleProperties))
    }
}

internal data class VersionMatches(
    val mappedCommitsIsPresent: Boolean?,
    val mappedHashIsPresent: Boolean?,
    val mappedMajorIsPresent: Boolean?,
    val mappedMetadataIsPresent: Boolean?,
    val mappedMinorIsPresent: Boolean?,
    val mappedPatchIsPresent: Boolean?,
    val mappedStageIsPresent: Boolean?,
    val mappedStageNameIsPresent: Boolean?,
    val mappedStageNumberIsPresent: Boolean?,
    val metadataIsPresent: Boolean?,
    val requestedTagPrefix: Boolean?,
    val contains: Map<String, Boolean>,
    val endsWith: Map<String, Boolean>,
    val environmentVariables: Map<String, Boolean>,
    val patterns: Map<String, Boolean>,
    val gradleProperties: Map<String, Boolean>,
    val startsWith: Map<String, Boolean>,
) : Serializable {

    val isConfigured: Boolean =
        mappedCommitsIsPresent != null ||
            mappedHashIsPresent != null ||
            mappedMajorIsPresent != null ||
            mappedMetadataIsPresent != null ||
            mappedMinorIsPresent != null ||
            mappedPatchIsPresent != null ||
            mappedStageIsPresent != null ||
            mappedStageNameIsPresent != null ||
            mappedStageNumberIsPresent != null ||
            metadataIsPresent != null ||
            requestedTagPrefix != null ||
            contains.isNotEmpty() ||
            endsWith.isNotEmpty() ||
            environmentVariables.isNotEmpty() ||
            patterns.isNotEmpty() ||
            gradleProperties.isNotEmpty() ||
            startsWith.isNotEmpty()

    fun matchesAll(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariableValues: Map<String, String>,
        gradlePropertyValues: Map<String, String>,
    ): Boolean =
        matchResults(version, mapping, environmentVariableValues, gradlePropertyValues).all {
            result: Boolean ->
            result
        }

    fun matchesAny(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariableValues: Map<String, String>,
        gradlePropertyValues: Map<String, String>,
    ): Boolean =
        matchResults(version, mapping, environmentVariableValues, gradlePropertyValues).any {
            result: Boolean ->
            result
        }

    fun matchesNone(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariableValues: Map<String, String>,
        gradlePropertyValues: Map<String, String>,
    ): Boolean =
        matchResults(version, mapping, environmentVariableValues, gradlePropertyValues).none {
            result: Boolean ->
            result
        }

    private fun matchResults(
        version: GradleVersion,
        mapping: VersionMapping,
        environmentVariableValues: Map<String, String>,
        gradlePropertyValues: Map<String, String>,
    ): List<Boolean> {
        val value: String = version.toString()
        return buildList {
            mappedCommitsIsPresent?.let { expected: Boolean ->
                add((mapping.commits != null) == expected)
            }
            mappedHashIsPresent?.let { expected: Boolean ->
                add((mapping.hash != null) == expected)
            }
            mappedMajorIsPresent?.let { expected: Boolean ->
                add((mapping.major != null) == expected)
            }
            mappedMetadataIsPresent?.let { expected: Boolean ->
                add((mapping.metadata != null) == expected)
            }
            mappedMinorIsPresent?.let { expected: Boolean ->
                add((mapping.minor != null) == expected)
            }
            mappedPatchIsPresent?.let { expected: Boolean ->
                add((mapping.patch != null) == expected)
            }
            mappedStageIsPresent?.let { expected: Boolean ->
                add(((mapping.stageName != null) || (mapping.stageNum != null)) == expected)
            }
            mappedStageNameIsPresent?.let { expected: Boolean ->
                add((mapping.stageName != null) == expected)
            }
            mappedStageNumberIsPresent?.let { expected: Boolean ->
                add((mapping.stageNum != null) == expected)
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
        mappedCommitsIsPresent = conditions.mappedCommitsIsPresent.orNull,
        mappedHashIsPresent = conditions.mappedHashIsPresent.orNull,
        mappedMajorIsPresent = conditions.mappedMajorIsPresent.orNull,
        mappedMetadataIsPresent = conditions.mappedMetadataIsPresent.orNull,
        mappedMinorIsPresent = conditions.mappedMinorIsPresent.orNull,
        mappedPatchIsPresent = conditions.mappedPatchIsPresent.orNull,
        mappedStageIsPresent = conditions.mappedStageIsPresent.orNull,
        mappedStageNameIsPresent = conditions.mappedStageNameIsPresent.orNull,
        mappedStageNumberIsPresent = conditions.mappedStageNumberIsPresent.orNull,
        metadataIsPresent = conditions.metadataIsPresent.orNull,
        requestedTagPrefix = conditions.requestedTagPrefix.orNull,
        contains = contains.getOrElse(emptyMap()),
        endsWith = endsWith.getOrElse(emptyMap()),
        environmentVariables = environmentVariables.getOrElse(emptyMap()),
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
