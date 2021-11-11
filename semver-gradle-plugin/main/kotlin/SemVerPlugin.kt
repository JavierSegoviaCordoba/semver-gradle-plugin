package com.javiersc.semver.gradle.plugin

import com.javiersc.semanticVersioning.Version
import com.javiersc.semanticVersioning.Version.Increase
import com.javiersc.semver.gradle.plugin.internal.GitRef
import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.git
import com.javiersc.semver.gradle.plugin.internal.headCommit
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentCommit
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create

public class SemVerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<SemVerExtension>(SemVerExtension.name)

        extension.tagPrefix.convention(SemVerExtension.defaultTagPrefix)

        target.afterEvaluate { project: Project ->
            project.checkScopeIsCorrect()
            project.checkVersionIsHigherOrSame()

            project.version = project.calculatedVersion
            project.generateVersionFile(project.tagPrefix)

            project.gradle.projectsEvaluated {
                if (project.appliedOnlyInRootProject) {
                    project.semverMessage("semver: ${project.version}")
                    project.allprojects { it.project.version = project.version }
                } else {
                    project.semverMessage("semver for ${project.name}: ${project.version}")
                }
            }
        }

        target.configureTasks()
    }
}

internal val Project.semanticVersion: Version
    get() =
        git.tagsInCurrentCommit(git.headCommit.commit.hash).lastResultVersion(tagPrefix)
            ?: git.tagsInCurrentBranch.lastResultVersion(tagPrefix) ?: initialVersion

internal fun List<GitRef.Tag>.lastResultVersion(tagPrefix: String): Version? =
    asSequence()
        .filter { tag -> tag.name.startsWith(tagPrefix) }
        .map { tag -> tag.name.substringAfter(tagPrefix) }
        .map(Version.Companion::safe)
        .mapNotNull(Result<Version>::getOrNull)
        .toList()
        .lastOrNull()

private fun Project.generateVersionFile(tagPrefix: String) =
    File("$buildDir/semver/version.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(
            """
               |$version
               |$tagPrefix$version
               |
            """.trimMargin()
        )
    }

private val initialVersion: Version = Version("0.1.0")

internal val Project.hasSemVerPlugin: Boolean
    get() = pluginManager.hasPlugin("com.javiersc.semver.gradle.plugin")

private val Project.appliedOnlyInRootProject: Boolean
    get() = rootProject.hasSemVerPlugin && rootProject.subprojects.none(Project::hasSemVerPlugin)

private fun Project.checkScopeIsCorrect() {
    check(scopeProperty in Scope.values().map(Scope::value) || scopeProperty.isNullOrBlank()) {
        "`scope` value must be one of ${Scope.values().map(Scope::value)} or empty"
    }
}

private fun Project.checkVersionIsHigherOrSame() {
    Version.safe(calculatedVersion).getOrNull()?.let { calculatedVersion ->
        check(calculatedVersion >= semanticVersion) {
            "Next version should be higher or the same than the current one"
        }
    }
}

private val Project.calculatedVersion: String
    get() =
        when {
            (stageProperty.isNullOrBlank() && scopeProperty.isNullOrBlank()) ||
                git.status().call().isClean.not() -> {
                "$semanticVersion${calculateAdditionalVersionData()}"
            }
            stageProperty.equals("SNAPSHOT", ignoreCase = true) -> {
                when (scopeProperty) {
                    Scope.Major.value -> "${semanticVersion.nextSnapshotMajor()}"
                    Scope.Minor.value -> "${semanticVersion.nextSnapshotMinor()}"
                    Scope.Patch.value -> "${semanticVersion.nextSnapshotPatch()}"
                    else -> "${semanticVersion.nextSnapshotPatch()}"
                }
            }
            stageProperty.equals("final", ignoreCase = true) -> {
                "${semanticVersion.inc()}"
            }
            else -> {
                val incStage = stageProperty ?: semanticVersion.stage?.name ?: ""
                when (scopeProperty) {
                    Scope.Major.value -> "${semanticVersion.inc(Increase.Major, incStage)}"
                    Scope.Minor.value -> "${semanticVersion.inc(Increase.Minor, incStage)}"
                    Scope.Patch.value -> "${semanticVersion.inc(Increase.Patch, incStage)}"
                    else -> "${semanticVersion.inc(stageName = incStage)}"
                }
            }
        }

private fun Project.configureTasks() {
    val createSemVerTag: Provider<Task> =
        tasks.register("createSemverTag") { task ->
            task.doLast {
                val semverWithPrefix =
                    "${it.project.tagPrefix}${Version(it.project.version.toString())}"
                task.project.git.tag().setName(semverWithPrefix).call()
            }
        }

    tasks.register("pushSemverTag") { task ->
        task.dependsOn(createSemVerTag)
        task.doLast { it.project.git.push().setPushTags().call() }
    }
}
