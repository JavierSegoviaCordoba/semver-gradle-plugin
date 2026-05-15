@file:Suppress("UnstableApiUsage")

package com.javiersc.semver.features.plugin.api

import com.javiersc.gradle.version.GradleVersion
import com.javiersc.semver.shared.VersionMapper
import java.io.Serializable

public fun SemverDefinition.toVersionMapper(): VersionMapper =
    DclVersionMapper(
        overrideVersion = overrideVersion.orNull,
        defaultMapping = mapVersion.toMapping(),
        ruleMappings =
            mapVersions
                .flatMapIndexed { index: Int, mapping: SemverMapVersionsDefinition ->
                    mapping.rules.map { rule: SemverMapVersionsRuleDefinition ->
                        RuleMapping(
                            declarationIndex = index,
                            priority = rule.priority.orNull,
                            mapping = mapping.toMapping(),
                            rule = rule.toRule(),
                        )
                    }
                }
                .sortedWith(
                    compareByDescending<RuleMapping> { rule: RuleMapping ->
                            rule.priority ?: Int.MIN_VALUE
                        }
                        .thenBy { rule: RuleMapping -> rule.declarationIndex }
                ),
    )

private data class DclVersionMapper(
    private val overrideVersion: String?,
    private val defaultMapping: VersionMapping,
    private val ruleMappings: List<RuleMapping>,
) : VersionMapper {

    override fun map(version: GradleVersion): String {
        val mappedByRule: String? =
            ruleMappings
                .firstOrNull { rule: RuleMapping -> rule.rule.matches(version, rule.mapping) }
                ?.let { rule: RuleMapping -> rule.mapping.map(version).toString() }

        return mappedByRule ?: overrideVersion ?: defaultMapping.map(version).toString()
    }
}

private data class RuleMapping(
    val declarationIndex: Int,
    val priority: Int?,
    val mapping: VersionMapping,
    val rule: VersionRule,
) : Serializable

private data class VersionMapping(
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

private data class VersionRule(
    val all: VersionMatches,
    val any: VersionMatches,
    val none: VersionMatches,
) : Serializable {

    fun matches(version: GradleVersion, mapping: VersionMapping): Boolean {
        val configuredGroups: List<Boolean> =
            listOf(all.isConfigured, any.isConfigured, none.isConfigured)
        return !configuredGroups.none { configured: Boolean -> configured } &&
            ((all.isConfigured && all.matchesAll(version, mapping)) ||
                (any.isConfigured && any.matchesAny(version, mapping)) ||
                (none.isConfigured && none.matchesNone(version, mapping)))
    }
}

private data class VersionMatches(
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

    fun matchesAll(version: GradleVersion, mapping: VersionMapping): Boolean =
        matchResults(version, mapping).all { result: Boolean -> result }

    fun matchesAny(version: GradleVersion, mapping: VersionMapping): Boolean =
        matchResults(version, mapping).any { result: Boolean -> result }

    fun matchesNone(version: GradleVersion, mapping: VersionMapping): Boolean =
        matchResults(version, mapping).none { result: Boolean -> result }

    private fun matchResults(version: GradleVersion, mapping: VersionMapping): List<Boolean> {
        val value: String = version.toString()
        return mappedFieldResults(mapping) +
            buildList {
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
                    add((System.getenv(name) != null) == expected)
                }
                patterns.forEach { (pattern: String, ignoreCase: Boolean) ->
                    val options: Set<RegexOption> =
                        if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()
                    add(Regex(pattern, options).containsMatchIn(value))
                }
                gradleProperties.forEach { (name: String, expected: Boolean) ->
                    add((System.getProperty(name) != null) == expected)
                }
                startsWith.forEach { (element: String, ignoreCase: Boolean) ->
                    add(value.startsWith(element, ignoreCase))
                }
            }
    }

    private fun mappedFieldResults(mapping: VersionMapping): List<Boolean> = buildList {
        mappedCommitsIsPresent?.let { expected: Boolean ->
            add((mapping.commits != null) == expected)
        }
        mappedHashIsPresent?.let { expected: Boolean -> add((mapping.hash != null) == expected) }
        mappedMajorIsPresent?.let { expected: Boolean -> add((mapping.major != null) == expected) }
        mappedMetadataIsPresent?.let { expected: Boolean ->
            add((mapping.metadata != null) == expected)
        }
        mappedMinorIsPresent?.let { expected: Boolean -> add((mapping.minor != null) == expected) }
        mappedPatchIsPresent?.let { expected: Boolean -> add((mapping.patch != null) == expected) }
        mappedStageIsPresent?.let { expected: Boolean ->
            add(((mapping.stageName != null) || (mapping.stageNum != null)) == expected)
        }
        mappedStageNameIsPresent?.let { expected: Boolean ->
            add((mapping.stageName != null) == expected)
        }
        mappedStageNumberIsPresent?.let { expected: Boolean ->
            add((mapping.stageNum != null) == expected)
        }
    }
}

private fun SemverVersionDefinition.toMapping(): VersionMapping =
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

private fun SemverMapVersionsRuleDefinition.toRule(): VersionRule =
    VersionRule(all = all.toMatches(), any = any.toMatches(), none = none.toMatches())

private fun SemverMapVersionsRuleDefinition.SemverMapVersionsMatches.toMatches(): VersionMatches =
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
