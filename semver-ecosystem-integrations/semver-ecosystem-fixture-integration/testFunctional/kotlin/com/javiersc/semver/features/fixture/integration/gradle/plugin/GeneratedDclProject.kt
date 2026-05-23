package com.javiersc.semver.features.fixture.integration.gradle.plugin

import com.javiersc.semver.shared.Insignificant
import com.javiersc.semver.shared.assertVersion
import java.io.File
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.ArgumentsAggregator

private const val NullValue: String = "null"

internal fun String?.csvOrNull(): String? = this?.takeUnless { it == NullValue || it.isBlank() }

internal fun ArgumentsAccessor.stringOrNull(index: Int): String? = getString(index).csvOrNull()

internal fun ArgumentsAccessor.boolean(index: Int): Boolean = getString(index).toBoolean()

internal fun ArgumentsAccessor.intOrNull(index: Int): Int? = stringOrNull(index)?.toInt()

internal fun File.writeDclProject(
    rootTagPrefix: String?,
    libraryTagPrefix: String?,
    rootLastTag: String,
    libraryLastTag: String?,
    includeLibrary: Boolean,
    gradlePropertiesTagPrefix: String?,
    semverBlock: String = "",
) {
    writeSettings(includeLibrary)
    resolve("last-tag.txt").writeText(rootLastTag)
    resolve("build.gradle.dcl").writeText(dclBuild(rootTagPrefix, semverBlock))
    writeGradleProperties(gradlePropertiesTagPrefix)

    if (includeLibrary) {
        resolve("library").mkdirs()
        resolve("library/build.gradle.dcl").writeText(dclBuild(libraryTagPrefix, ""))
        libraryLastTag?.let { lastTag: String ->
            resolve("library/last-tag.txt").writeText(lastTag)
        }
    }
}

internal fun File.assertGeneratedVersion(expectedVersion: String?, expectedTagVersion: String?) {
    if (expectedVersion == null || expectedTagVersion == null) return
    assertVersionLines(expectedVersion, expectedTagVersion)
}

private fun File.assertVersionLines(expectedVersion: String, expectedTagVersion: String) {
    val tagPrefix: String = expectedTagVersion.takeWhile(Char::isLetter)
    val insignificant: Insignificant? =
        when {
            expectedVersion.endsWith("+HASH") || expectedTagVersion.endsWith("+HASH") -> {
                Insignificant.Hash
            }
            expectedVersion.endsWith("+DIRTY") || expectedTagVersion.endsWith("+DIRTY") -> {
                Insignificant.Dirty
            }
            else -> null
        }
    val version: String = expectedVersion.substringBeforeLast("+HASH").substringBeforeLast("+DIRTY")
    assertVersion(tagPrefix, version, insignificant)
}

private fun File.writeSettings(includeLibrary: Boolean) {
    val include = if (includeLibrary) "\ninclude(\":library\")" else ""
    resolve("settings.gradle.dcl")
        .writeText(
            """
            |plugins {
            |    id("com.javiersc.semver.features")
            |    id("semver.ecosystem.fixture")
            |    id("semver.ecosystem.fixture.integration")
            |}
            |
            |rootProject.name = "sandbox-project"
            |$include
            |"""
                .trimMargin()
        )
}

private fun File.writeGradleProperties(gradlePropertiesTagPrefix: String?) {
    val semverTagPrefix =
        gradlePropertiesTagPrefix?.let { tagPrefix: String -> "\nsemver.tagPrefix=$tagPrefix" }
            ?: ""
    resolve("gradle.properties").writeText("org.gradle.kotlin.dsl.dcl=true$semverTagPrefix\n")
}

private fun dclBuild(tagPrefix: String?, semverBlock: String): String {
    val tagPrefixLine = tagPrefix?.let { """            tagPrefix = "$it"""" } ?: ""
    val block = semverBlock.prependIndent("            ")
    return """
        |semverEcosystemFixture {
        |    semver {
        |$tagPrefixLine
        |$block
        |    }
        |}
        |"""
        .trimMargin()
}

internal fun versionMappingBlock(case: VersionMappingCase): String = buildString {
    case.overrideVersion?.let { version: String -> appendLine("""mapVersion("$version")""") }
    if (case.hasMapVersionBlock) {
        appendLine("mapVersion {")
        appendVersionMapping(case, indent = "    ")
        appendLine("}")
    }
    if (case.hasRuleBlock) {
        appendLine("mapVersions {")
        appendLine("""    mapVersion("${case.ruleName}") {""")
        appendVersionMapping(case, indent = "        ")
        appendLine("        rules {")
        appendLine("""            rule("${case.ruleName}") {""")
        case.rulePriority?.let { priority: Int ->
            appendLine("                priority = $priority")
        }
        appendLine("                ${case.ruleGroup} {")
        appendConditions(case.conditions, indent = "                    ")
        appendLine("                }")
        appendLine("            }")
        appendLine("        }")
        appendLine("    }")
        appendLine("}")
    }
}

private val VersionMappingCase.hasMapVersionBlock: Boolean
    get() =
        listOf(
                mapMajor?.toString(),
                mapMinor?.toString(),
                mapPatch?.toString(),
                mapStageName,
                mapStageNumber?.toString(),
                mapCommits?.toString(),
                mapHash,
                mapMetadata,
                mapMetadataGradleProperty,
            )
            .any { value: String? -> value != null }

private val VersionMappingCase.hasRuleBlock: Boolean
    get() = ruleName != null && ruleGroup != null

private fun StringBuilder.appendVersionMapping(case: VersionMappingCase, indent: String) {
    case.mapMajor?.let { appendLine("${indent}major($it)") }
    case.mapMinor?.let { appendLine("${indent}minor($it)") }
    case.mapPatch?.let { appendLine("${indent}patch($it)") }
    if (case.mapStageName != null || case.mapStageNumber != null) {
        appendLine("${indent}stage {")
        case.mapStageName?.let { appendLine("""$indent    name("$it")""") }
        case.mapStageNumber?.let { appendLine("${indent}    number($it)") }
        appendLine("$indent}")
    }
    case.mapCommits?.let { appendLine("${indent}commits($it)") }
    case.mapHash?.let { appendLine("""${indent}hash("$it")""") }
    case.mapMetadata?.let { appendLine("""${indent}metadata("$it")""") }
    case.mapMetadataGradleProperty?.let { property: String ->
        appendLine("${indent}metadata {")
        appendLine("""$indent    gradleProperty("$property")""")
        appendLine("$indent}")
    }
}

private fun StringBuilder.appendConditions(conditions: String?, indent: String) {
    conditions?.split(";")?.mapNotNull(String::csvOrNull)?.forEach { condition: String ->
        val name = condition.substringBefore("=")
        val value = condition.substringAfter("=", "")
        appendLine("$indent${conditionCall(name, value)}")
    }
}

private fun conditionCall(name: String, value: String): String {
    fun stringBooleanCall(function: String): String {
        val parts = value.split("|")
        val text = parts[0]
        val ignoreCase = parts.getOrNull(1)?.toBooleanStrictOrNull()
        return if (ignoreCase == null) {
            """$function("$text")"""
        } else {
            """$function("$text", $ignoreCase)"""
        }
    }

    fun booleanCall(function: String): String =
        if (value.isBlank() || value == "true") "$function()" else "$function($value)"

    fun intCall(function: String): String = "$function($value)"

    return when (name) {
        "contains" -> stringBooleanCall("contains")
        "endsWith" -> stringBooleanCall("endsWith")
        "environmentVariableIsPresent" -> stringBooleanCall("environmentVariableIsPresent")
        "gradlePropertyIsPresent" -> stringBooleanCall("gradlePropertyIsPresent")
        "mappedContains" -> stringBooleanCall("mappedContains")
        "mappedEndsWith" -> stringBooleanCall("mappedEndsWith")
        "mappedHash" -> stringBooleanCall("mappedHash")
        "mappedMetadata" -> stringBooleanCall("mappedMetadata")
        "mappedPattern" -> stringBooleanCall("mappedPattern")
        "mappedStageName" -> stringBooleanCall("mappedStageName")
        "mappedStartsWith" -> stringBooleanCall("mappedStartsWith")
        "pattern" -> stringBooleanCall("pattern")
        "startsWith" -> stringBooleanCall("startsWith")
        "mappedCommits" -> intCall("mappedCommits")
        "mappedMajor" -> intCall("mappedMajor")
        "mappedMinor" -> intCall("mappedMinor")
        "mappedPatch" -> intCall("mappedPatch")
        "mappedStageNumber" -> intCall("mappedStageNumber")
        "mappedCommitsIsPresent" -> booleanCall("mappedCommitsIsPresent")
        "mappedHashIsPresent" -> booleanCall("mappedHashIsPresent")
        "mappedMajorIsPresent" -> booleanCall("mappedMajorIsPresent")
        "mappedMetadataIsPresent" -> booleanCall("mappedMetadataIsPresent")
        "mappedMinorIsPresent" -> booleanCall("mappedMinorIsPresent")
        "mappedPatchIsPresent" -> booleanCall("mappedPatchIsPresent")
        "mappedStageIsPresent" -> booleanCall("mappedStageIsPresent")
        "mappedStageNameIsPresent" -> booleanCall("mappedStageNameIsPresent")
        "mappedStageNumberIsPresent" -> booleanCall("mappedStageNumberIsPresent")
        "metadataIsPresent" -> booleanCall("metadataIsPresent")
        "requestedTagPrefix" -> booleanCall("requestedTagPrefix")
        else -> error("Unsupported condition: $name")
    }
}

internal data class VersionMappingCase(
    val name: String,
    val lastTag: String,
    val rootTagPrefix: String?,
    val task: String,
    val scopeProperty: String?,
    val stageProperty: String?,
    val tagPrefixProperty: String?,
    val gradlePropertyName: String?,
    val gradlePropertyValue: String?,
    val environmentName: String?,
    val environmentValue: String?,
    val setup: String,
    val expectedRoot: String,
    val expectedRootTag: String,
    val overrideVersion: String?,
    val mapMajor: Int?,
    val mapMinor: Int?,
    val mapPatch: Int?,
    val mapStageName: String?,
    val mapStageNumber: Int?,
    val mapCommits: Int?,
    val mapHash: String?,
    val mapMetadata: String?,
    val mapMetadataGradleProperty: String?,
    val ruleName: String?,
    val rulePriority: Int?,
    val ruleGroup: String?,
    val conditions: String?,
) {
    object Aggregator : ArgumentsAggregator {
        override fun aggregateArguments(
            accessor: ArgumentsAccessor,
            context: ParameterContext,
        ): VersionMappingCase =
            VersionMappingCase(
                name = accessor.getString(0),
                lastTag = accessor.getString(1),
                rootTagPrefix = accessor.stringOrNull(2),
                task = accessor.getString(3),
                scopeProperty = accessor.stringOrNull(4),
                stageProperty = accessor.stringOrNull(5),
                tagPrefixProperty = accessor.stringOrNull(6),
                gradlePropertyName = accessor.stringOrNull(7),
                gradlePropertyValue = accessor.stringOrNull(8),
                environmentName = accessor.stringOrNull(9),
                environmentValue = accessor.stringOrNull(10),
                setup = accessor.getString(11),
                expectedRoot = accessor.getString(12),
                expectedRootTag = accessor.getString(13),
                overrideVersion = accessor.stringOrNull(14),
                mapMajor = accessor.intOrNull(15),
                mapMinor = accessor.intOrNull(16),
                mapPatch = accessor.intOrNull(17),
                mapStageName = accessor.stringOrNull(18),
                mapStageNumber = accessor.intOrNull(19),
                mapCommits = accessor.intOrNull(20),
                mapHash = accessor.stringOrNull(21),
                mapMetadata = accessor.stringOrNull(22),
                mapMetadataGradleProperty = accessor.stringOrNull(23),
                ruleName = accessor.stringOrNull(24),
                rulePriority = accessor.intOrNull(25),
                ruleGroup = accessor.stringOrNull(26),
                conditions = accessor.stringOrNull(27),
            )
    }
}
