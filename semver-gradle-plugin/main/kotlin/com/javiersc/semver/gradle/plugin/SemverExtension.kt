package com.javiersc.semver.gradle.plugin

import com.javiersc.semver.gradle.plugin.internal.DefaultTagPrefix
import com.javiersc.semver.gradle.plugin.internal.git.gitDir
import java.io.File
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property

public abstract class SemverExtension @Inject constructor(objects: ObjectFactory) {

    public val tagPrefix: Property<String> = objects.property<String>().convention(DefaultTagPrefix)

    // TODO: check if unnecessary (probably it is unnecessary) and remove it
    @get:InputFiles public abstract val gitDirs: ConfigurableFileCollection

    public companion object {

        public const val extensionName: String = "semver"

        internal fun register(project: Project): SemverExtension =
            with(project) {
                extensions.create<SemverExtension>(extensionName).apply {
                    val files: List<File> = gitDir.walkTopDown().toList()
                    gitDirs.from(files)
                }
            }
    }
}

internal val Project.semverExtension: SemverExtension
    get() = extensions.getByType()
