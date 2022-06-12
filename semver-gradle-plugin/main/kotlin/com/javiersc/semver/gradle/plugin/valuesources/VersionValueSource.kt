package com.javiersc.semver.gradle.plugin.valuesources

import com.javiersc.semver.gradle.plugin.internal.calculatedVersion
import com.javiersc.semver.gradle.plugin.internal.checkCleanProperty
import com.javiersc.semver.gradle.plugin.internal.checkVersionIsHigherOrSame
import com.javiersc.semver.gradle.plugin.internal.git.git
import com.javiersc.semver.gradle.plugin.internal.git.gitDir
import com.javiersc.semver.gradle.plugin.internal.git.lastVersionInCurrentBranch
import com.javiersc.semver.gradle.plugin.internal.scopeProperty
import com.javiersc.semver.gradle.plugin.internal.stageProperty
import com.javiersc.semver.gradle.plugin.internal.tagPrefixProperty
import com.javiersc.semver.gradle.plugin.semverExtension
import com.javiersc.semver.gradle.plugin.tasks.SemverCreateTag
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.kotlin.dsl.of

public abstract class VersionValueSource : ValueSource<String, VersionValueSource.Params> {

    override fun obtain(): String =
        with(parameters) {
            val isSamePrefix = tagPrefixProperty.get() == projectTagPrefix.get()
            val git = gitDir.asFile.get().git
            val version =
                git.calculatedVersion(
                    tagPrefix = projectTagPrefix.get(),
                    stageProperty = stageProperty.orNull.takeIf { isSamePrefix },
                    scopeProperty = scopeProperty.orNull.takeIf { isSamePrefix },
                    isCreatingSemverTag = creatingSemverTag.get().takeIf { isSamePrefix } ?: false,
                    checkClean = checkClean.get(),
                )

            checkVersionIsHigherOrSame(
                version,
                git.lastVersionInCurrentBranch(projectTagPrefix.get())
            )

            version
        }

    public interface Params : ValueSourceParameters {
        public val gitDir: DirectoryProperty
        public val projectTagPrefix: Property<String>
        public val tagPrefixProperty: Property<String>
        public val stageProperty: Property<String?>
        public val scopeProperty: Property<String?>
        public val creatingSemverTag: Property<Boolean>
        public val checkClean: Property<Boolean>
    }

    public companion object {
        internal fun register(project: Project): Provider<String> =
            with(project) {
                providers
                    .of(VersionValueSource::class) {
                        it.parameters.gitDir.set(layout.dir(provider { gitDir }))
                        it.parameters.tagPrefixProperty.set(tagPrefixProperty)
                        it.parameters.projectTagPrefix.set(semverExtension.tagPrefix)
                        it.parameters.stageProperty.set(stageProperty)
                        it.parameters.scopeProperty.set(scopeProperty)
                        it.parameters.creatingSemverTag.set(isCreatingSemverTag)
                        it.parameters.checkClean.set(checkCleanProperty)
                    }
                    .forUseAtConfigurationTime()
            }
    }
}

private val Project.isCreatingSemverTag: Boolean
    get() =
        gradle.startParameter.taskNames.any { taskName: String ->
            taskName == SemverCreateTag.taskName
        }
