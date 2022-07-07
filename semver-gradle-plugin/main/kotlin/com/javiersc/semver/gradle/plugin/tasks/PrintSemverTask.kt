package com.javiersc.semver.gradle.plugin.tasks

import com.javiersc.gradle.tasks.extensions.maybeRegisterLazily
import com.javiersc.gradle.tasks.extensions.namedLazily
import com.javiersc.semver.gradle.plugin.internal.semverMessage
import com.javiersc.semver.gradle.plugin.semverExtension
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME

@CacheableTask
public abstract class PrintSemverTask
@Inject
constructor(
    private val projectName: String,
    objects: ObjectFactory,
    layout: ProjectLayout,
) : DefaultTask() {

    init {
        group = "semver"
    }

    @get:Input public abstract val tagPrefix: Property<String>

    @get:Input public abstract val version: Property<String>

    @OutputFile
    public val semverFile: RegularFileProperty =
        objects.fileProperty().convention(layout.buildDirectory.file("semver/version.txt"))

    @TaskAction
    public fun run() {
        val semver: String = version.get()
        val prefix: String = tagPrefix.get()
        val semverWithPrefix = "$prefix$semver"

        val name: String =
            if (projectName.isBlank() || projectName == ":") "the root project" else projectName

        semverMessage("semver for $name: $semverWithPrefix")

        semverFile.get().asFile.apply {
            parentFile.mkdirs()
            createNewFile()
            writeText(
                """
                    |$semver
                    |$semverWithPrefix
                    |
                """.trimMargin()
            )
        }
    }

    internal companion object {
        const val taskName: String = "printSemver"

        fun register(project: Project): TaskProvider<PrintSemverTask> {
            val printSemverTask: TaskProvider<PrintSemverTask> =
                project.tasks.register(taskName, project.name)

            printSemverTask.configure {
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
