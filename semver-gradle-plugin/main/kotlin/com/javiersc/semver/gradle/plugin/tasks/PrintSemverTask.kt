package com.javiersc.semver.gradle.plugin.tasks

import com.javiersc.gradle.project.extensions.isRootProject
import com.javiersc.gradle.tasks.extensions.maybeRegisterLazily
import com.javiersc.gradle.tasks.extensions.namedLazily
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.semverExtension
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.UntrackedTask
import org.gradle.api.tasks.options.Option
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME

@UntrackedTask(because = "It must always print the version")
public abstract class PrintSemverTask
@Inject
constructor(
    private val isRootProject: Boolean,
    private val projectName: String,
    objects: ObjectFactory,
) : DefaultTask() {

    init {
        group = "semver"
    }

    @Input
    @Option(
        option = "githubOnlyRoot",
        description = "Set any GitHub option to work only on the root project `printSemver` task"
    )
    public val githubOnlyRoot: Property<Boolean> = objects.property<Boolean>().convention(false)

    @Input
    @Option(
        option = "githubEnvTag",
        description =
            "Set the version as `semver-tag-subproject-name` environment variable of the GitHub Actions"
    )
    public val githubEnvTag: Property<Boolean> = objects.property<Boolean>().convention(false)

    @Input
    @Option(
        option = "githubEnvVersion",
        description =
            "Set the version as `semver-version-subproject-name` environment variable of the GitHub Actions"
    )
    public val githubEnvVersion: Property<Boolean> = objects.property<Boolean>().convention(false)

    @Input
    @Option(
        option = "githubEnv",
        description =
            "Set the version as `semver-subproject-name` environment variable of the GitHub Actions"
    )
    public val githubEnv: Property<Boolean> = objects.property<Boolean>().convention(false)

    @Input
    @Option(
        option = "githubOutputTag",
        description = "Set the version as `semver-tag-subproject-name` output of the GitHub Actions"
    )
    public val githubOutputTag: Property<Boolean> = objects.property<Boolean>().convention(false)

    @Input
    @Option(
        option = "githubOutputVersion",
        description =
            "Set the version as `semver-version-subproject-name` output of the GitHub Actions"
    )
    public val githubOutputVersion: Property<Boolean> =
        objects.property<Boolean>().convention(false)

    @Input
    @Option(
        option = "githubOutput",
        description =
            "Set the version as `semver-subproject-name` output of the GitHub Actions `semver` step ID"
    )
    public val githubOutput: Property<Boolean> = objects.property<Boolean>().convention(false)

    @get:Input public abstract val tagPrefix: Property<String>

    @get:Input public abstract val version: Property<String>

    @TaskAction
    public fun run() {
        val semver: String = version.get()
        val prefix: String = tagPrefix.get()
        val semverWithPrefix = "$prefix$semver"

        val name: String =
            if (projectName.isBlank() || projectName == ":") "the root project" else projectName

        semverMessage("semver for $name: $semverWithPrefix")

        val onlyRoot: Boolean = githubOnlyRoot.orNull ?: false
        val allProjects: Boolean = !onlyRoot
        when {
            onlyRoot && isRootProject -> configureGitHub(prefix, semver, semverWithPrefix)
            allProjects -> configureGitHub(prefix, semver, semverWithPrefix)
        }
    }

    private fun configureGitHub(tagPrefix: String, semver: String, semverWithPrefix: String) {
        val tagName = if (isRootProject) "semver-tag" else "semver-tag-$projectName"
        if (githubOutputTag.orNull == true) executeGitHubOutput(tagName, tagPrefix)
        if (githubEnvTag.orNull == true) executeGitHubEnvironmentVariable(tagName, tagPrefix)

        val versionName = if (isRootProject) "semver-version" else "semver-version-$projectName"
        if (githubOutputVersion.orNull == true) executeGitHubOutput(versionName, semver)
        if (githubEnvVersion.orNull == true) executeGitHubEnvironmentVariable(versionName, semver)

        val name = if (isRootProject) "semver" else "semver-$projectName"
        if (githubOutput.orNull == true) executeGitHubOutput(name, semverWithPrefix)
        if (githubEnv.orNull == true) executeGitHubEnvironmentVariable(name, semverWithPrefix)
    }

    private fun executeGitHubOutput(key: String, value: String) {
        semverMessage("\nSetting $value as `$key` output:")
        println("::set-output name=$key::$value")
    }

    private fun executeGitHubEnvironmentVariable(key: String, value: String) {
        val snakeCaseKey = key.toSnakeCase()
        semverMessage("\nSetting $value as `$snakeCaseKey` environment variable:")
        val githubEnvFile = File(System.getenv("GITHUB_ENV"))
        val currentText = githubEnvFile.readText()
        githubEnvFile.writeText("$currentText\n$snakeCaseKey=$value")
    }

    private fun String.toSnakeCase(): String =
        map { char -> if (char.isUpperCase()) "_$char" else char.uppercaseChar() }
            .joinToString("")
            .replace(".", "_")
            .replace("-", "_")

    internal companion object {
        const val taskName: String = "printSemver"

        fun register(project: Project): TaskProvider<PrintSemverTask> {
            val printSemverTask: TaskProvider<PrintSemverTask> =
                project.tasks.register(taskName, project.isRootProject, project.name)

            printSemverTask.configure {
                dependsOn(WriteSemverTask.taskName)
                tagPrefix.set(project.semverExtension.tagPrefix)
                version.set(project.version.toString())
            }

            project.tasks.namedLazily<CreateSemverTagTask>(CreateSemverTagTask.taskName) {
                dependsOn(printSemverTask)
            }
            project.tasks.maybeRegisterLazily<Task>(ASSEMBLE_TASK_NAME) {
                dependsOn(printSemverTask)
            }
            project.tasks.maybeRegisterLazily<Task>(BUILD_TASK_NAME) { dependsOn(printSemverTask) }
            project.tasks.maybeRegisterLazily<Task>(CHECK_TASK_NAME) { dependsOn(printSemverTask) }
            project.tasks.withType<Jar>().configureEach { dependsOn(printSemverTask) }
            project.tasks.withType<Test>().configureEach { dependsOn(printSemverTask) }

            return printSemverTask
        }
    }
}
