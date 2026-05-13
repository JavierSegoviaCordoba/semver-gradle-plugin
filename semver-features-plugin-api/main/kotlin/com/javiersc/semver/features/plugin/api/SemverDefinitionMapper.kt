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
                .firstOrNull { rule: RuleMapping -> rule.rule.matches(version) }
                ?.let { rule: RuleMapping -> rule.mapping.map(version).toString() }

        return mappedByRule ?: overrideVersion ?: defaultMapping.map(version).toString()
    }
}

private data class RuleMapping(
    val declarationIndex: Int,
    val priority: Int?,
    val mapping: VersionMapping,
    val rule: VersionRule,
) : Serializable {

    private companion object {
        private const val serialVersionUID: Long = 0L
    }
}

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

    private companion object {
        private const val serialVersionUID: Long = 0L
    }
}

private data class VersionRule(
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

private data class VersionMatches(
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
        metadataIsPresent = conditions.metadataIsPresent.orNull,
        requestedTagPrefix = conditions.requestedTagPrefix.orNull,
        contains = contains.getOrElse(emptyMap()),
        endsWith = endsWith.getOrElse(emptyMap()),
        patterns = patterns.getOrElse(emptyMap()),
        startsWith = startsWith.getOrElse(emptyMap()),
    )
