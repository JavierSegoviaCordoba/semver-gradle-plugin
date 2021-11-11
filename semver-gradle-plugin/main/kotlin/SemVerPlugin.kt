package com.javiersc.semver.gradle.plugin

import com.javiersc.semanticVersioning.Version
import com.javiersc.semver.gradle.plugin.internal.GitRef
import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
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

            project.version = project.calculatedVersion(false)
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

internal val Project.lastSemVer: Version
    get() =
        git.tagsInCurrentCommit(git.headCommit.commit.hash).lastResultVersion(tagPrefix, true)
            ?: git.tagsInCurrentBranch.lastResultVersion(tagPrefix, false) ?: initialVersion

internal fun List<GitRef.Tag>.lastResultVersion(
    tagPrefix: String,
    inCurrentCommit: Boolean
): Version? =
    asSequence()
        .filter { tag -> tag.name.startsWith(tagPrefix) }
        .map { tag -> tag.name.substringAfter(tagPrefix) }
        .map(Version.Companion::safe)
        .mapNotNull(Result<Version>::getOrNull)
        .toList()
        .run { if (inCurrentCommit) maxOrNull() else lastOrNull() }

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
    Version.safe(calculatedVersion(false)).getOrNull()?.let { calculatedVersion ->
        check(calculatedVersion >= lastSemVer) {
            "Next version should be higher or the same than the current one"
        }
    }
}

private fun Project.configureTasks() {
    val createSemVerTag: Provider<Task> =
        tasks.register("createSemverTag") { task ->
            task.doFirst { doFirsTask ->
                doFirsTask.project.version = doFirsTask.project.calculatedVersion(true)
            }
            task.doLast {
                val semver = "${Version("${it.project.version}")}"
                val semverWithPrefix = "${it.project.tagPrefix}$semver"
                task.project.git.tag().setName(semverWithPrefix).call()
                it.project.generateVersionFile(it.project.tagPrefix)
                semverMessage("Created new semver tag: $semverWithPrefix")
            }
        }

    tasks.register("pushSemverTag") { task ->
        task.dependsOn(createSemVerTag)
        task.doLast { it.project.git.push().setPushTags().call() }
    }
}
