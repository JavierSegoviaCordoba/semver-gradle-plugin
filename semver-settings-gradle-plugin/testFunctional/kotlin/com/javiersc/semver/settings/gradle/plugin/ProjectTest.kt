package com.javiersc.semver.settings.gradle.plugin

import com.javiersc.gradle.testkit.test.extensions.GradleTestKitTest
import com.javiersc.semver.settings.gradle.plugin.utils.createGitIgnore
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test
import org.eclipse.jgit.api.Git

internal class ProjectTest : GradleTestKitTest() {

    @Test
    fun `project with included build`() {
        gradleTestKitTest("project-with-included-build") {
            val git: Git = Git.init().setDirectory(projectDir).call()
            projectDir.createGitIgnore()
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Initial commit").call()
            git.tag().setName("v1.0.0").call()
            git.tag().setName("o2.0.3").call()
            git.tag().setName("z6.2.1").call()
            git.tag().setName("f8.1.7").call()

            val output: String = gradlew("assemble", stacktrace()).output
            val outputIncludedBuildRootProject: String =
                gradlew(":build-logic:assemble", stacktrace()).output
            val outputIncludedBuildModuleA: String =
                gradlew(":build-logic:module-a:assemble", stacktrace()).output

            output
                .shouldContain("semver for multi-project: v1.0.0")
                .shouldContain("semver for library-one: o2.0.3")
            outputIncludedBuildRootProject.shouldContain("semver for build-logic: z6.2.1")
            outputIncludedBuildModuleA.shouldContain("semver for module-a: f8.1.7")
        }
    }
}
