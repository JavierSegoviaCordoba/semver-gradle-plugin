package com.javiersc.semver.gradle.plugin.tasks

import com.javiersc.semver.gradle.plugin.internal.git
import com.javiersc.semver.gradle.plugin.internal.remoteProperty
import com.javiersc.semver.gradle.plugin.internal.tagPrefixProperty
import org.eclipse.jgit.transport.RemoteConfig
import org.gradle.api.Project

public open class PushSemverTag {

    internal companion object {
        internal const val name = "pushSemverTag"

        internal fun register(project: Project) {
            if (project.rootProject.tasks.findByName(name) == null) {
                project.rootProject.tasks.register(name) { task ->
                    task.dependsOn(CreateSemverTag.name)

                    task.doFirst {
                        check(project.remotes.isNotEmpty()) {
                            "There is no remote where pushing the git tag"
                        }
                    }

                    task.doLast {
                        project.exec { exec ->
                            val remoteProp: String? = project.remoteProperty

                            val remote: String =
                                when {
                                    remoteProp != null -> {
                                        checkNotNull(
                                            project.remotes.firstOrNull { it == remoteProp }
                                        ) { "There is no remote with the name $remoteProp" }
                                    }
                                    project.remotes.contains("origin") -> "origin"
                                    else -> project.remotes.first()
                                }

                            val tag = "${project.tagPrefixProperty}${project.version}"

                            exec.commandLine("git", "push", remote, tag)
                        }
                    }
                }
            }
        }
    }
}

private val Project.remotes: List<String>
    get() = git.remoteList().call().map(RemoteConfig::getName)
