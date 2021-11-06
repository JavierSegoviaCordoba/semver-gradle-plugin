package com.javiersc.semver.gradle.plugin

import com.javiersc.semanticVersioning.Version
import com.javiersc.semanticVersioning.Version.Increase
import com.javiersc.semver.gradle.plugin.internal.calculateAdditionalVersionData
import com.javiersc.semver.gradle.plugin.internal.git
import com.javiersc.semver.gradle.plugin.internal.tagsInCurrentBranch
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create

public class SemVerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<SemVerExtension>(SemVerExtension.name)

        extension.applyToAllProjects.convention(SemVerExtension.defaultApplyToAllProjects)
        extension.tagPrefix.convention(SemVerExtension.defaultTagPrefix)

        target.afterEvaluate { project: Project ->
            check(project.applyToAllProjects && target == target.rootProject) {
                "if `applyToAllProjects` is true, semver plugin must be applied in the root project"
            }

            val lastTagSemVer = project.semanticVersion

            val stage = project.stageProperty
            val scope = project.scopeProperty

            check(scope in Scope.values().map(Scope::value) || scope.isNullOrBlank()) {
                "`scope` value must be one of ${Scope.values().map(Scope::value)} or empty"
            }

            project.version =
                when {
                    stage.isNullOrBlank() && scope.isNullOrBlank() -> {
                        "$lastTagSemVer${project.calculateAdditionalVersionData()}"
                    }
                    stage.equals("SNAPSHOT", ignoreCase = true) -> {
                        when (scope) {
                            Scope.Major.value -> "${lastTagSemVer.nextSnapshotMajor()}"
                            Scope.Minor.value -> "${lastTagSemVer.nextSnapshotMinor()}"
                            Scope.Patch.value -> "${lastTagSemVer.nextSnapshotPatch()}"
                            else -> "${lastTagSemVer.nextSnapshotPatch()}"
                        }
                    }
                    stage.equals("final", ignoreCase = true) -> {
                        "${lastTagSemVer.inc()}"
                    }
                    else -> {
                        val incStage = stage ?: lastTagSemVer.stage?.name ?: ""
                        when (scope) {
                            Scope.Major.value -> "${lastTagSemVer.inc(Increase.Major, incStage)}"
                            Scope.Minor.value -> "${lastTagSemVer.inc(Increase.Minor, incStage)}"
                            Scope.Patch.value -> "${lastTagSemVer.inc(Increase.Patch, incStage)}"
                            else -> "${lastTagSemVer.inc(stageName = incStage)}"
                        }
                    }
                }

            val nextVersion = Version.safe("${project.version}").getOrNull()
            if (nextVersion != null) {
                check(nextVersion > lastTagSemVer) {
                    "Next version should be higher than the current one"
                }
            }

            project.generateVersionFile()
        }

        val createSemVerTag: Provider<Task> =
            target.tasks.register("createSemverTag") {
                val semverWithPrefix =
                    "${it.project.tagPrefix}${Version(it.project.version.toString())}"
                it.project.git.tag().setName(semverWithPrefix).call()
            }

        target.tasks.register("pushSemverTag") {
            it.dependsOn(createSemVerTag)
            it.project.git.push().setPushTags().call()
        }
    }
}

internal val Project.semanticVersion: Version
    get() =
        git.tagsInCurrentBranch
            .asSequence()
            .filter { tag -> tag.name.startsWith(tagPrefix) }
            .map { tag -> tag.name.substringAfter(tagPrefix) }
            .map(Version.Companion::safe)
            .mapNotNull(Result<Version>::getOrNull)
            .sortedDescending()
            .toList()
            .firstOrNull()
            ?: initialVersion

private fun Project.generateVersionFile() =
    File("$buildDir/semver/version.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText("$version")
    }

private val initialVersion: Version = Version("0.1.0")
