# detekt

## Metrics

* 181 number of properties

* 151 number of functions

* 49 number of classes

* 10 number of packages

* 36 number of kt files

## Complexity Report

* 4,137 lines of code (loc)

* 3,449 source lines of code (sloc)

* 2,667 logical lines of code (lloc)

* 27 comment lines of code (cloc)

* 278 cyclomatic complexity (mcc)

* 79 cognitive complexity

* 23 number of total code smells

* 0% comment source ratio

* 104 mcc per 1,000 lloc

* 8 code smells per 1,000 lloc

## Findings (23)

### complexity, LargeClass (1)

One class should have one responsibility. Large classes tend to handle many things at once. Split up large classes into smaller classes that are easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#largeclass)

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/git/GitTagTest.kt:29:16
```
Class GitTagTest is too large. Consider splitting it into smaller pieces.
```
```kotlin
26 import kotlin.test.Test
27 import org.eclipse.jgit.lib.Ref
28 
29 internal class GitTagTest {
!!                ^ error
30 
31     @Test
32     fun `tags in repo`() {

```

### complexity, LongMethod (9)

One method should have one responsibility. Long methods tend to handle many things at once. Prefer smaller methods to make them easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#longmethod)

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/internal/CalculatedVersion.kt:8:14
```
The function calculatedVersion is too long (83). The maximum length is 60.
```
```kotlin
5  import com.javiersc.semver.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst
6  
7  @Suppress("ComplexMethod")
8  internal fun calculatedVersion(
!               ^ error
9      stageProperty: String?,
10     scopeProperty: String?,
11     isCreatingSemverTag: Boolean,

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/GitHubVariablesTest.kt:13:9
```
The function GitHub variables is too long (65). The maximum length is 60.
```
```kotlin
10 class GitHubVariablesTest : GradleTest() {
11 
12     @Test
13     fun `GitHub variables`() {
!!         ^ error
14         gradleTestKitTest("github-variables") {
15             val githubEnvPath = projectDir.resolve("environment/github.env").path
16             val githubEnvFile =

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/MassiveTagsInSameCommitTest.kt:14:9
```
The function massive tags in same commit is too long (67). The maximum length is 60.
```
```kotlin
11 
12     @Test
13     @Suppress("ComplexMethod")
14     fun `massive tags in same commit`() {
!!         ^ error
15         gradleTestKitTest("examples/one-project") {
16             Git.init().setDirectory(projectDir).call()
17 

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/TagsOrderTest.kt:14:9
```
The function random is too long (63). The maximum length is 60.
```
```kotlin
11 internal class TagsOrderTest : GradleTest() {
12 
13     @Test
14     fun random() {
!!         ^ error
15         gradleTestKitTest("tags-order/random") {
16             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
17 

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/git/GitTagTest.kt:494:9
```
The function version tags in current branch is too long (85). The maximum length is 60.
```
```kotlin
491     }
492 
493     @Test
494     fun `version tags in current branch`() {
!!!         ^ error
495         initialCommitAnd {
496             git.tag().setName("hello").call()
497             git.tag().setName("vhello").call()

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/git/GitTagTest.kt:589:9
```
The function version tags sorted by semver is too long (85). The maximum length is 60.
```
```kotlin
586     }
587 
588     @Test
589     fun `version tags sorted by semver`() {
!!!         ^ error
590         initialCommitAnd {
591             git.tag().setName("hello").call()
592             git.tag().setName("vhello").call()

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/git/GitTagTest.kt:687:9
```
The function version tags sorted by timeline or semver 1 is too long (89). The maximum length is 60.
```
```kotlin
684     }
685 
686     @Test
687     fun `version tags sorted by timeline or semver 1`() {
!!!         ^ error
688         initialCommitAnd {
689             git.tag().setName("hello").call()
690             git.tag().setName("vhello").call()

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/git/GitTagTest.kt:789:9
```
The function version tags sorted by timeline or semver 2 is too long (169). The maximum length is 60.
```
```kotlin
786     }
787 
788     @Test
789     fun `version tags sorted by timeline or semver 2`() {
!!!         ^ error
790         initialCommitAnd {
791             git.tag().setName("hello").call()
792             git.tag().setName("vhello").call()

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/git/GitTagTest.kt:978:9
```
The function last version tag in current branch is too long (71). The maximum length is 60.
```
```kotlin
975     }
976 
977     @Test
978     fun `last version tag in current branch`() {
!!!         ^ error
979         initialCommitAnd {
980             git.tag().setName("hello").call()
981             git.tag().setName("vhello").call()

```

### complexity, LongParameterList (2)

The more parameters a function has the more complex it is. Long parameter lists are often used to control complex algorithms and violate the Single Responsibility Principle. Prefer functions with short parameter lists.

[Documentation](https://detekt.dev/docs/rules/complexity#longparameterlist)

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/internal/CalculatedVersion.kt:8:31
```
The function calculatedVersion(stageProperty: String?, scopeProperty: String?, isCreatingSemverTag: Boolean, lastSemverMajorInCurrentBranch: Int, lastSemverMinorInCurrentBranch: Int, lastSemverPatchInCurrentBranch: Int, lastSemverStageInCurrentBranch: String?, lastSemverNumInCurrentBranch: Int?, versionTagsInCurrentBranch: List<String>, clean: Boolean, checkClean: Boolean, lastCommitInCurrentBranch: String?, commitsInCurrentBranch: List<String>, headCommit: String, lastVersionCommitInCurrentBranch: String?) has too many parameters. The current threshold is set to 6.
```
```kotlin
5  import com.javiersc.semver.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst
6  
7  @Suppress("ComplexMethod")
8  internal fun calculatedVersion(
!                                ^ error
9      stageProperty: String?,
10     scopeProperty: String?,
11     isCreatingSemverTag: Boolean,

```

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/internal/CalculatedVersion.kt:114:44
```
The function calculateAdditionalVersionData(clean: Boolean, checkClean: Boolean, lastCommitInCurrentBranch: String?, commitsInCurrentBranch: List<String>, isThereVersionTags: Boolean, headCommit: String, lastVersionCommitInCurrentBranch: String?) has too many parameters. The current threshold is set to 6.
```
```kotlin
111     return calculatedVersion
112 }
113 
114 internal fun calculateAdditionalVersionData(
!!!                                            ^ error
115     clean: Boolean = true,
116     checkClean: Boolean = true,
117     lastCommitInCurrentBranch: String?,

```

### complexity, TooManyFunctions (2)

Too many functions inside a/an file/class/object/interface always indicate a violation of the single responsibility principle. Maybe the file/class/object/interface wants to manage too many things at once. Extract functionality which clearly belongs together.

[Documentation](https://detekt.dev/docs/rules/complexity#toomanyfunctions)

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/internal/git/GitCache.kt:13:16
```
Class 'GitCache' with '12' functions detected. Defined threshold inside classes is set to '11'
```
```kotlin
10 import org.eclipse.jgit.revwalk.RevCommit
11 import org.eclipse.jgit.revwalk.RevWalk
12 
13 internal class GitCache(private val git: Git) {
!!                ^ error
14 
15     internal val isClean: Boolean
16         get() = git.status().call().isClean

```

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/internal/git/GitRefGitExtensions.kt:1:1
```
File '/home/runner/work/semver-gradle-plugin/semver-gradle-plugin/semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/internal/git/GitRefGitExtensions.kt' with '11' functions detected. Defined threshold inside files is set to '11'
```
```kotlin
1 package com.javiersc.semver.gradle.plugin.internal.git
! ^ error
2 
3 import com.javiersc.semver.Version
4 import org.eclipse.jgit.api.Git

```

### exceptions, SwallowedException (1)

The caught exception is swallowed. The original exception could be lost.

[Documentation](https://detekt.dev/docs/rules/exceptions#swallowedexception)

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/services/GitBuildService.kt:117:14
```
The caught exception is swallowed. The original exception could be lost.
```
```kotlin
114 private fun Git.hasCommits(): Boolean =
115     try {
116         commitsInCurrentBranchRevCommit.isNotEmpty()
117     } catch (exception: NoHeadException) {
!!!              ^ error
118         false
119     }
120 

```

### naming, TopLevelPropertyNaming (1)

Top level property names should follow the naming convention set in the projects configuration.

[Documentation](https://detekt.dev/docs/rules/naming#toplevelpropertynaming)

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/internal/SemverProperties.kt:8:20
```
Top level constant names should match the pattern: [A-Z][_A-Z0-9]*
```
```kotlin
5  internal val Project.tagPrefixProperty: String
6      get() = properties[SemverProperties.TagPrefix.key]?.toString() ?: DefaultTagPrefix
7  
8  internal const val DefaultTagPrefix = ""
!                     ^ error
9  
10 internal val Project.stageProperty: String?
11     get() = properties[SemverProperties.Stage.key]?.toString()

```

### naming, VariableNaming (2)

Variable names should follow the naming convention set in the projects configuration.

[Documentation](https://detekt.dev/docs/rules/naming#variablenaming)

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/GradleFeaturesTest.kt:88:13
```
Variable names should match the pattern: [a-z][A-Za-z0-9]*
```
```kotlin
85         build()
86         projectDir.assertVersion("v", "0.9.0", Hash)
87 
88         val LAST_0_9_0_HASH_1 = projectDir.resolve("build/semver/version.txt").readLines()[1]
!!             ^ error
89         projectDir
90             .resolve("expect-version.txt")
91             .writeText(

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/GradleFeaturesTest.kt:103:13
```
Variable names should match the pattern: [a-z][A-Za-z0-9]*
```
```kotlin
100         git.commit().setMessage("Change expect-version again").call()
101 
102         build()
103         val LAST_0_9_0_HASH_2 = projectDir.resolve("build/semver/version.txt").readLines()[1]
!!!             ^ error
104 
105         LAST_0_9_0_HASH_1.shouldNotBe(LAST_0_9_0_HASH_2)
106 

```

### style, MaxLineLength (4)

Line detected, which is longer than the defined maximum line length in the code style.

[Documentation](https://detekt.dev/docs/rules/style#maxlinelength)

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/examples/MultiProjectExampleTest.kt:43:13
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
40             `4_ Run gradlew assemble`()
41             `5_ Create, add and commit a new file in library-one-a, then run gradlew assemble`()
42             `6_ Run gradlew createSemverTag tagPrefix=a`()
43             `7_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=alpha tagPrefix=a`()
!!             ^ error
44             `8_ Run gradlew createSemverTag stage=beta tagPrefix=a`()
45             `9_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=final tagPrefix=a`()
46             `10_ Run gradlew createSemverTag stage=final scope=major tagPrefix=a`()

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/examples/MultiProjectExampleTest.kt:45:13
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
42             `6_ Run gradlew createSemverTag tagPrefix=a`()
43             `7_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=alpha tagPrefix=a`()
44             `8_ Run gradlew createSemverTag stage=beta tagPrefix=a`()
45             `9_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=final tagPrefix=a`()
!!             ^ error
46             `10_ Run gradlew createSemverTag stage=final scope=major tagPrefix=a`()
47             `11_ Run gradlew printSemver stage=snapshot tagPrefix=a`()
48             `12_ Run gradlew createSemverTag scope=minor tagPrefix=b`()

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/examples/MultiProjectExampleTest.kt:175:9
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
172     }
173 
174     private fun GradleRunner
175         .`7_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=alpha tagPrefix=a`() {
!!!         ^ error
176         projectDirByName("library-one-a").resolve("new7.txt").createNewFile()
177         git.add().addFilepattern(".").call()
178         git.commit().setMessage("Add new7 to library-one-a").call()

```

* semver-gradle-plugin/test/kotlin/com/javiersc/semver/gradle/plugin/examples/MultiProjectExampleTest.kt:210:9
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
207     }
208 
209     private fun GradleRunner
210         .`9_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=final tagPrefix=a`() {
!!!         ^ error
211         projectDirByName("library-one-a").resolve("new9.txt").createNewFile()
212         git.add().addFilepattern(".").call()
213         git.commit().setMessage("Add new9 to library-one-a").call()

```

### style, UnnecessaryAbstractClass (1)

An abstract class is unnecessary and can be refactored. An abstract class should have both abstract and concrete properties or functions. An abstract class without a concrete member can be refactored to an interface. An abstract class without an abstract member can be refactored to a concrete class.

[Documentation](https://detekt.dev/docs/rules/style#unnecessaryabstractclass)

* semver-gradle-plugin/main/kotlin/com/javiersc/semver/gradle/plugin/SemverExtension.kt:12:23
```
An abstract class without an abstract member can be refactored to a concrete class.
```
```kotlin
9  import org.gradle.kotlin.dsl.getByType
10 import org.gradle.kotlin.dsl.property
11 
12 public abstract class SemverExtension @Inject constructor(objects: ObjectFactory) {
!!                       ^ error
13 
14     public val tagPrefix: Property<String> = objects.property<String>().convention(DefaultTagPrefix)
15 

```

generated with [detekt version 1.21.0](https://detekt.dev/) on 2022-07-17 19:37:06 UTC
