# detekt

## Metrics

* 221 number of properties

* 170 number of functions

* 56 number of classes

* 11 number of packages

* 47 number of kt files

## Complexity Report

* 4,755 lines of code (loc)

* 3,967 source lines of code (sloc)

* 3,043 logical lines of code (lloc)

* 37 comment lines of code (cloc)

* 315 cyclomatic complexity (mcc)

* 100 cognitive complexity

* 71 number of total code smells

* 0% comment source ratio

* 103 mcc per 1,000 lloc

* 23 code smells per 1,000 lloc

## Findings (71)

### complexity, LargeClass (1)

One class should have one responsibility. Large classes tend to handle many things at once. Split up large classes into smaller classes that are easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#largeclass)

* semver-project-gradle-plugin/test/kotlin/com/javiersc/semver/project/gradle/plugin/GitTagTest.kt:27:16
```
Class GitTagTest is too large. Consider splitting it into smaller pieces.
```
```kotlin
24 import kotlin.test.Test
25 import org.eclipse.jgit.lib.Ref
26 
27 internal class GitTagTest {
!!                ^ error
28 
29     @Test
30     fun `tags in repo`() {

```

### complexity, LongMethod (8)

One method should have one responsibility. Long methods tend to handle many things at once. Prefer smaller methods to make them easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#longmethod)

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/CalculatedVersion.kt:8:14
```
The function calculatedVersion is too long (85). The maximum length is 60.
```
```kotlin
5  import com.javiersc.semver.project.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst
6  
7  @Suppress("ComplexMethod")
8  internal fun calculatedVersion(
!               ^ error
9      stageProperty: String?,
10     scopeProperty: String?,
11     isCreatingSemverTag: Boolean,

```

* semver-project-gradle-plugin/test/kotlin/com/javiersc/semver/project/gradle/plugin/GitTagTest.kt:492:9
```
The function version tags in current branch is too long (85). The maximum length is 60.
```
```kotlin
489     }
490 
491     @Test
492     fun `version tags in current branch`() {
!!!         ^ error
493         initialCommitAnd {
494             git.tag().setName("hello").call()
495             git.tag().setName("vhello").call()

```

* semver-project-gradle-plugin/test/kotlin/com/javiersc/semver/project/gradle/plugin/GitTagTest.kt:587:9
```
The function version tags sorted by semver is too long (85). The maximum length is 60.
```
```kotlin
584     }
585 
586     @Test
587     fun `version tags sorted by semver`() {
!!!         ^ error
588         initialCommitAnd {
589             git.tag().setName("hello").call()
590             git.tag().setName("vhello").call()

```

* semver-project-gradle-plugin/test/kotlin/com/javiersc/semver/project/gradle/plugin/GitTagTest.kt:685:9
```
The function version tags sorted by timeline or semver 1 is too long (89). The maximum length is 60.
```
```kotlin
682     }
683 
684     @Test
685     fun `version tags sorted by timeline or semver 1`() {
!!!         ^ error
686         initialCommitAnd {
687             git.tag().setName("hello").call()
688             git.tag().setName("vhello").call()

```

* semver-project-gradle-plugin/test/kotlin/com/javiersc/semver/project/gradle/plugin/GitTagTest.kt:787:9
```
The function version tags sorted by timeline or semver 2 is too long (169). The maximum length is 60.
```
```kotlin
784     }
785 
786     @Test
787     fun `version tags sorted by timeline or semver 2`() {
!!!         ^ error
788         initialCommitAnd {
789             git.tag().setName("hello").call()
790             git.tag().setName("vhello").call()

```

* semver-project-gradle-plugin/test/kotlin/com/javiersc/semver/project/gradle/plugin/GitTagTest.kt:976:9
```
The function last version tag in current branch is too long (71). The maximum length is 60.
```
```kotlin
973     }
974 
975     @Test
976     fun `last version tag in current branch`() {
!!!         ^ error
977         initialCommitAnd {
978             git.tag().setName("hello").call()
979             git.tag().setName("vhello").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/MassiveTagsInSameCommitTest.kt:11:9
```
The function massive tags in same commit is too long (67). The maximum length is 60.
```
```kotlin
8  
9      @Test
10     @Suppress("ComplexMethod")
11     fun `massive tags in same commit`() {
!!         ^ error
12         gradleTestKitTest("examples/one-project") {
13             Git.init().setDirectory(projectDir).call()
14 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/TagsOrderTest.kt:11:9
```
The function random is too long (63). The maximum length is 60.
```
```kotlin
8  internal class TagsOrderTest : GradleTestKitTest() {
9  
10     @Test
11     fun random() {
!!         ^ error
12         gradleTestKitTest("tags-order/random") {
13             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
14 

```

### complexity, LongParameterList (2)

The more parameters a function has the more complex it is. Long parameter lists are often used to control complex algorithms and violate the Single Responsibility Principle. Prefer functions with short parameter lists.

[Documentation](https://detekt.dev/docs/rules/complexity#longparameterlist)

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/CalculatedVersion.kt:8:31
```
The function calculatedVersion(stageProperty: String?, scopeProperty: String?, isCreatingSemverTag: Boolean, lastSemverMajorInCurrentBranch: Int, lastSemverMinorInCurrentBranch: Int, lastSemverPatchInCurrentBranch: Int, lastSemverStageInCurrentBranch: String?, lastSemverNumInCurrentBranch: Int?, versionTagsInCurrentBranch: List<String>, clean: Boolean, checkClean: Boolean, lastCommitInCurrentBranch: String?, commitsInCurrentBranch: List<String>, headCommit: String, lastVersionCommitInCurrentBranch: String?) has too many parameters. The current threshold is set to 6.
```
```kotlin
5  import com.javiersc.semver.project.gradle.plugin.internal.git.commitsBetweenTwoCommitsIncludingLastExcludingFirst
6  
7  @Suppress("ComplexMethod")
8  internal fun calculatedVersion(
!                                ^ error
9      stageProperty: String?,
10     scopeProperty: String?,
11     isCreatingSemverTag: Boolean,

```

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/CalculatedVersion.kt:118:44
```
The function calculateAdditionalVersionData(clean: Boolean, checkClean: Boolean, lastCommitInCurrentBranch: String?, commitsInCurrentBranch: List<String>, isThereVersionTags: Boolean, headCommit: String, lastVersionCommitInCurrentBranch: String?) has too many parameters. The current threshold is set to 6.
```
```kotlin
115     return calculatedVersion
116 }
117 
118 internal fun calculateAdditionalVersionData(
!!!                                            ^ error
119     clean: Boolean = true,
120     checkClean: Boolean = true,
121     lastCommitInCurrentBranch: String?,

```

### complexity, TooManyFunctions (4)

Too many functions inside a/an file/class/object/interface always indicate a violation of the single responsibility principle. Maybe the file/class/object/interface wants to manage too many things at once. Extract functionality which clearly belongs together.

[Documentation](https://detekt.dev/docs/rules/complexity#toomanyfunctions)

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/git/GitCache.kt:26:16
```
Class 'GitCache' with '13' functions detected. Defined threshold inside classes is set to '11'
```
```kotlin
23 
24 private var gitCache: GitCache? = null
25 
26 internal class GitCache
!!                ^ error
27 private constructor(
28     private val gitDir: File,
29     maxCount: Provider<Int>? = null,

```

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/git/GitRefGitExtensions.kt:1:1
```
File '/home/runner/work/semver-gradle-plugin/semver-gradle-plugin/semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/git/GitRefGitExtensions.kt' with '11' functions detected. Defined threshold inside files is set to '11'
```
```kotlin
1 package com.javiersc.semver.project.gradle.plugin.internal.git
! ^ error
2 
3 import com.javiersc.semver.Version
4 import org.eclipse.jgit.api.Git

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:26:16
```
Class 'MultiProjectExampleTest' with '16' functions detected. Defined threshold inside classes is set to '11'
```
```kotlin
23  * - `library-nine` uses no prefix
24  * - `library-ten` uses no prefix
25  */
26 internal class MultiProjectExampleTest : GradleTestKitTest() {
!!                ^ error
27 
28     @Test
29     fun `multi project`() {

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:12:16
```
Class 'OneProjectExampleTest' with '13' functions detected. Defined threshold inside classes is set to '11'
```
```kotlin
9  import org.eclipse.jgit.api.Git
10 import org.gradle.testkit.runner.GradleRunner
11 
12 internal class OneProjectExampleTest : GradleTestKitTest() {
!!                ^ error
13 
14     @Test
15     fun `one project`() {

```

### exceptions, SwallowedException (1)

The caught exception is swallowed. The original exception could be lost.

[Documentation](https://detekt.dev/docs/rules/exceptions#swallowedexception)

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/services/GitBuildService.kt:111:14
```
The caught exception is swallowed. The original exception could be lost.
```
```kotlin
108 internal fun Git.hasCommits(): Boolean =
109     try {
110         commitsInCurrentBranchRevCommit.isNotEmpty()
111     } catch (exception: NoHeadException) {
!!!              ^ error
112         false
113     }
114 

```

### naming, FunctionNaming (47)

Function names should follow the naming convention set in the configuration.

[Documentation](https://detekt.dev/docs/rules/naming#functionnaming)

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GitHubVariablesTest.kt:41:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
38     }
39 
40     @Test
41     fun `setting GITHUB_ENV`() {
!!         ^ error
42         gradleTestKitTest("github-variables") {
43             setEnvironmentVariables()
44 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GitHubVariablesTest.kt:106:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
103     }
104 
105     @Test
106     fun `setting GITHUB_OUTPUT`() {
!!!         ^ error
107         gradleTestKitTest("github-variables") {
108             setEnvironmentVariables()
109 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GradleFeaturesTest.kt:14:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
11 class GradleFeaturesTest : GradleTestKitTest() {
12 
13     @Test
14     fun `android configuration cache clean v1_0_0`() {
!!         ^ error
15         gradleTestKitTest("gradle-features/android configuration cache clean v1_0_0") {
16             beforeTest()
17             testConfigurationCache(expectTaskOutcome = TaskOutcome.SUCCESS)

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GradleFeaturesTest.kt:22:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
19     }
20 
21     @Test
22     fun `android build cache clean v1_0_0`() {
!!         ^ error
23         gradleTestKitTest("gradle-features/android build cache clean v1_0_0") {
24             beforeTest()
25             testBuildCache()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GradleFeaturesTest.kt:30:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
27     }
28 
29     @Test
30     fun `build cache clean v1_0_0`() {
!!         ^ error
31         gradleTestKitTest("gradle-features/build cache clean v1_0_0") {
32             beforeTest()
33             testBuildCache()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GradleFeaturesTest.kt:38:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
35     }
36 
37     @Test
38     fun `configuration cache clean v1_0_0`() {
!!         ^ error
39         gradleTestKitTest("gradle-features/configuration cache clean v1_0_0") {
40             beforeTest()
41             testConfigurationCache(expectTaskOutcome = TaskOutcome.SUCCESS)

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GradleFeaturesTest.kt:46:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
43     }
44 
45     @Test
46     fun `project isolation clean v1_0_0`() {
!!         ^ error
47         gradleTestKitTest("gradle-features/project isolation clean v1_0_0") { beforeTest() }
48     }
49 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/MassiveTagsInSameCommitTest.kt:11:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
8  
9      @Test
10     @Suppress("ComplexMethod")
11     fun `massive tags in same commit`() {
!!         ^ error
12         gradleTestKitTest("examples/one-project") {
13             Git.init().setDirectory(projectDir).call()
14 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/PropertiesTest.kt:31:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
28     }
29 
30     @Test
31     fun `stage and scope`() {
!!         ^ error
32         runPropertyTestsBasedOnResourceDirectory("stage+scope")
33     }
34 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/PushSemverTagTaskTest.kt:18:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
15 class PushSemverTagTaskTest : GradleTestKitTest() {
16 
17     @Test
18     fun `push tag`() {
!!         ^ error
19         gradleTestKitTest("push-tag") {
20             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
21 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/VersionBuildDirTest.kt:12:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
9  internal class VersionBuildDirTest : GradleTestKitTest() {
10 
11     @Test
12     fun `clean v1_0_0`() {
!!         ^ error
13         gradleTestKitTest("version-build-dir/clean v1_0_0") {
14             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
15             git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/VersionBuildDirTest.kt:24:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
21     }
22 
23     @Test
24     fun `clean v1_0_0 configuration phase`() {
!!         ^ error
25         gradleTestKitTest("version-build-dir/clean v1_0_0 configuration phase") {
26             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
27             git.tag().setObjectId(git.headRevCommitInBranch).setName("v1.0.0").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/VersionBuildDirTest.kt:38:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
35     }
36 
37     @Test
38     fun `clean without tag in current commit - hash`() {
!!         ^ error
39         gradleTestKitTest("version-build-dir/clean-with-no-tag-current-commit (hash)") {
40             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
41 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/VersionBuildDirTest.kt:49:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
46     }
47 
48     @Test
49     fun `no clean without tag in current commit - dirty`() {
!!         ^ error
50         gradleTestKitTest("version-build-dir/no-clean-with-no-tag-current-commit (dirty)") {
51             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
52             projectDir.resolve("new-2.txt").createNewFile()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/VersionBuildDirTest.kt:73:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
70     }
71 
72     @Test
73     fun `no clean createSemverTag should fail`() {
!!         ^ error
74         gradleTestKitTest("version-build-dir/no-clean-with-no-tag-current-commit (dirty)") {
75             projectDir.generateInitialCommitAddVersionTagAndAddNewCommit()
76             projectDir.resolve("new-2.txt").createNewFile()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:29:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
26 internal class MultiProjectExampleTest : GradleTestKitTest() {
27 
28     @Test
29     fun `multi project`() {
!!         ^ error
30         gradleTestKitTest("examples/multi-project") {
31             `0_ Initial repo state`()
32             `1_ Run gradlew assemble`()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:49:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
46         }
47     }
48 
49     private fun GradleRunner.`0_ Initial repo state`() {
!!                              ^ error
50         Git.init().setDirectory(projectDir).call()
51         projectDir.createGitIgnore()
52         git.add().addFilepattern(".").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:68:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
65         git.add().addFilepattern(".").call()
66     }
67 
68     private fun GradleRunner.`1_ Run gradlew assemble`() {
!!                              ^ error
69         gradlew("assemble")
70 
71         projectDirByName("library-one-a").assertVersion("a", "1.0.0", Insignificant.Dirty)

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:83:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
80         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Dirty)
81     }
82 
83     private fun GradleRunner.`2_ Create a new file in library-one-a and run gradlew assemble`() {
!!                              ^ error
84         projectDirByName("library-one-a").resolve("new2.txt").createNewFile()
85 
86         gradlew("assemble")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:101:10
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
98      }
99  
100     private fun GradleRunner
101         .`3_ Add the new file to git and commit it, then run gradlew createSemverTag with tagPrefix=a`() {
!!!          ^ error
102         git.add().addFilepattern(".").call()
103         git.commit().setMessage("Add new2 to library-one-a").call()
104 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:119:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
116         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Hash)
117     }
118 
119     private fun GradleRunner.`4_ Run gradlew assemble`() {
!!!                              ^ error
120         gradlew("assemble")
121 
122         projectDirByName("library-one-a").assertVersion("a", "1.0.1")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:135:10
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
132     }
133 
134     private fun GradleRunner
135         .`5_ Create, add and commit a new file in library-one-a, then run gradlew assemble`() {
!!!          ^ error
136         projectDirByName("library-one-a").resolve("new5.txt").createNewFile()
137         git.add().addFilepattern(".").call()
138         git.commit().setMessage("Add new5 to library-one-a").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:154:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
151         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Hash)
152     }
153 
154     private fun GradleRunner.`6_ Run gradlew createSemverTag tagPrefix=a`() {
!!!                              ^ error
155         gradlew("createSemverTag", "-Psemver.tagPrefix=a")
156 
157         projectDirByName("library-one-a").assertVersion("a", "1.0.2")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:170:10
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
167     }
168 
169     private fun GradleRunner
170         .`7_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=alpha tagPrefix=a`() {
!!!          ^ error
171         projectDirByName("library-one-a").resolve("new7.txt").createNewFile()
172         git.add().addFilepattern(".").call()
173         git.commit().setMessage("Add new7 to library-one-a").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:189:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
186         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Hash)
187     }
188 
189     private fun GradleRunner.`8_ Run gradlew createSemverTag stage=beta tagPrefix=a`() {
!!!                              ^ error
190         gradlew("createSemverTag", "-Psemver.stage=beta", "-Psemver.tagPrefix=a")
191 
192         projectDirByName("library-one-a").assertVersion("a", "1.0.3-beta.1")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:205:10
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
202     }
203 
204     private fun GradleRunner
205         .`9_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=final tagPrefix=a`() {
!!!          ^ error
206         projectDirByName("library-one-a").resolve("new9.txt").createNewFile()
207         git.add().addFilepattern(".").call()
208         git.commit().setMessage("Add new9 to library-one-a").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:225:10
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
222     }
223 
224     private fun GradleRunner
225         .`10_ Run gradlew createSemverTag stage=final scope=major tagPrefix=a`() {
!!!          ^ error
226         gradlew(
227             "createSemverTag",
228             "-Psemver.stage=final",

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:245:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
242         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Hash)
243     }
244 
245     private fun GradleRunner.`11_ Run gradlew printSemver stage=snapshot tagPrefix=a`() {
!!!                              ^ error
246         gradlew("printSemver", "-Psemver.stage=snapshot", "-Psemver.tagPrefix=a")
247 
248         projectDirByName("library-one-a").assertVersion("a", "2.0.1-SNAPSHOT")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:260:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
257         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Hash)
258     }
259 
260     private fun GradleRunner.`12_ Run gradlew createSemverTag scope=minor tagPrefix=b`() {
!!!                              ^ error
261         gradlew("createSemverTag", "-Psemver.scope=minor", "-Psemver.tagPrefix=b")
262 
263         projectDirByName("library-one-a").assertVersion("a", "2.0.0")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:275:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
272         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Hash)
273     }
274 
275     private fun GradleRunner.`13_ Run gradlew createSemverTag stage=rc tagPrefix=c`() {
!!!                              ^ error
276         gradlew("createSemverTag", "-Psemver.stage=rc", "-Psemver.tagPrefix=c")
277 
278         projectDirByName("library-one-a").assertVersion("a", "2.0.0")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:290:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
287         projectDirByName("library-ten").assertVersion("", "1.0.0", Insignificant.Hash)
288     }
289 
290     private fun GradleRunner.`14_ Run gradlew createSemverTag stage=dev`() {
!!!                              ^ error
291         gradlew("createSemverTag", "-Psemver.stage=dev")
292 
293         projectDirByName("library-one-a").assertVersion("a", "2.0.0")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:15:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
12 internal class OneProjectExampleTest : GradleTestKitTest() {
13 
14     @Test
15     fun `one project`() {
!!         ^ error
16         gradleTestKitTest("examples/one-project") {
17             `0_ Initial repo state`()
18             `1_ Run gradlew assemble`()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:32:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
29         }
30     }
31 
32     private fun GradleRunner.`0_ Initial repo state`() {
!!                              ^ error
33         Git.init().setDirectory(projectDir).call()
34         projectDir.createGitIgnore()
35         git.add().addFilepattern(".").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:48:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
45         git.commit().setMessage("Add plugin").call()
46     }
47 
48     private fun GradleRunner.`1_ Run gradlew assemble`() {
!!                              ^ error
49         gradlew("assemble", "-Psemver.tagPrefix=v")
50 
51         projectDir.assertVersion("v", "0.1.0", Insignificant.Hash)

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:54:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
51         projectDir.assertVersion("v", "0.1.0", Insignificant.Hash)
52     }
53 
54     private fun GradleRunner.`2_ Create a new file and run gradlew assemble`() {
!!                              ^ error
55         projectDir.resolve("new2.txt").createNewFile()
56 
57         gradlew("assemble", "-Psemver.tagPrefix=v")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:63:10
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
60     }
61 
62     private fun GradleRunner
63         .`3_ Add the new file to git, commit it, and run gradlew createSemverTag`() {
!!          ^ error
64         git.add().addFilepattern(".").call()
65         git.commit().setMessage("Add new2").call()
66 

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:72:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
69         projectDir.assertVersion("v", "0.1.0")
70     }
71 
72     private fun GradleRunner.`4_ Run gradlew assemble`() {
!!                              ^ error
73         gradlew("assemble", "-Psemver.tagPrefix=v")
74 
75         projectDir.assertVersion("v", "0.1.0")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:78:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
75         projectDir.assertVersion("v", "0.1.0")
76     }
77 
78     private fun GradleRunner.`5_ Create and add new file and run`() {
!!                              ^ error
79         projectDir.resolve("new5.txt").createNewFile()
80         git.add().addFilepattern(".").call()
81         git.commit().setMessage("Add new5").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:87:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
84         projectDir.assertVersion("v", "0.1.0", Insignificant.Hash)
85     }
86 
87     private fun GradleRunner.`6_ Run gradlew createSemverTag`() {
!!                              ^ error
88         gradlew("createSemverTag", "-Psemver.tagPrefix=v")
89 
90         projectDir.assertVersion("v", "0.1.1")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:94:10
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
91     }
92 
93     private fun GradleRunner
94         .`7_ Create and add to git a new file, then run gradlew createSemverTag stage=alpha`() {
!!          ^ error
95         projectDir.resolve("new7.txt").createNewFile()
96         git.add().addFilepattern(".").call()
97         git.commit().setMessage("Add new7").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:103:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
100         projectDir.assertVersion("v", "0.1.2-alpha.1")
101     }
102 
103     private fun GradleRunner.`8_ Run gradlew createSemverTag stage=beta`() {
!!!                              ^ error
104         gradlew("createSemverTag", "-Psemver.stage=beta", "-Psemver.tagPrefix=v")
105 
106         projectDir.assertVersion("v", "0.1.2-beta.1")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:109:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
106         projectDir.assertVersion("v", "0.1.2-beta.1")
107     }
108 
109     private fun GradleRunner.`9_ Run gradlew createSemverTag stage=final`() {
!!!                              ^ error
110         gradlew("createSemverTag", "-Psemver.stage=final", "-Psemver.tagPrefix=v")
111 
112         projectDir.assertVersion("v", "0.1.2")

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:115:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
112         projectDir.assertVersion("v", "0.1.2")
113     }
114 
115     private fun GradleRunner.`10_ Run gradlew createSemverTag stage=final scope=major`() {
!!!                              ^ error
116         gradlew(
117             "createSemverTag",
118             "-Psemver.stage=final",

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/OneProjectExampleTest.kt:126:30
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
123         projectDir.assertVersion("v", "1.0.0")
124     }
125 
126     private fun GradleRunner.`11_ Run gradlew createSemverTag stage=snapshot`() {
!!!                              ^ error
127         gradlew("createSemverTag", "-Psemver.stage=snapshot", "-Psemver.tagPrefix=v")
128 
129         projectDir.assertVersion("v", "1.0.1-SNAPSHOT")

```

* semver-project-gradle-plugin/testIntegration/kotlin/com/javiersc/semver/project/gradle/plugin/SemverIntegrationTest.kt:20:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
17 class SemverIntegrationTest : GradleProjectTest() {
18 
19     @Test
20     fun `given a project which has no git when it builds then it only register semver extension`() {
!!         ^ error
21         gradleProjectTest {
22             pluginManager.apply(SemverProjectPlugin::class)
23             extensions.findByName("semver").shouldNotBeNull()

```

* semver-project-gradle-plugin/testIntegration/kotlin/com/javiersc/semver/project/gradle/plugin/SemverIntegrationTest.kt:30:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
27     }
28 
29     @Test
30     fun `given a project which has commits when it builds then it register semver extension`() {
!!         ^ error
31         val beforeCommitTimestamp: Instant = Instant.now()
32         gradleProjectTest {
33             projectDir.resolve("last-tag.txt").apply {

```

* semver-settings-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/settings/gradle/plugin/MultiProjectTest.kt:11:9
```
Function names should match the pattern: [a-z][a-zA-Z0-9]*
```
```kotlin
8  internal class MultiProjectTest : GradleTestKitTest() {
9  
10     @Test
11     fun `multi project test`() {
!!         ^ error
12         gradleTestKitTest("multi-project") {
13             val git = Git.init().setDirectory(projectDir).call()
14             git.add().addFilepattern(".").call()

```

### naming, TopLevelPropertyNaming (1)

Top level property names should follow the naming convention set in the projects configuration.

[Documentation](https://detekt.dev/docs/rules/naming#toplevelpropertynaming)

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/SemverProperty.kt:7:20
```
Top level constant names should match the pattern: [A-Z][_A-Z0-9]*
```
```kotlin
4  import org.gradle.api.Project
5  import org.gradle.api.provider.Provider
6  
7  internal const val DefaultTagPrefix = ""
!                     ^ error
8  
9  internal val Project.projectTagPrefixProperty: Provider<String>
10     get() = getSemverProperty(SemverProperty.ProjectTagPrefix)

```

### naming, VariableNaming (2)

Variable names should follow the naming convention set in the projects configuration.

[Documentation](https://detekt.dev/docs/rules/naming#variablenaming)

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GradleFeaturesTest.kt:84:13
```
Variable names should match the pattern: [a-z][A-Za-z0-9]*
```
```kotlin
81         build()
82         projectDir.assertVersion("v", "0.9.0", Hash)
83 
84         val LAST_0_9_0_HASH_1 = projectDir.resolve("build/semver/version.txt").readLines()[1]
!!             ^ error
85         projectDir
86             .resolve("expect-version.txt")
87             .writeText(

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/GradleFeaturesTest.kt:100:13
```
Variable names should match the pattern: [a-z][A-Za-z0-9]*
```
```kotlin
97          git.commit().setMessage("Change expect-version again").call()
98  
99          build()
100         val LAST_0_9_0_HASH_2 = projectDir.resolve("build/semver/version.txt").readLines()[1]
!!!             ^ error
101 
102         LAST_0_9_0_HASH_1.shouldNotBe(LAST_0_9_0_HASH_2)
103 

```

### style, ForbiddenComment (1)

Flags a forbidden comment.

[Documentation](https://detekt.dev/docs/rules/style#forbiddencomment)

* semver-project-gradle-plugin/main/kotlin/com/javiersc/semver/project/gradle/plugin/internal/git/GitCache.kt:215:13
```
This comment contains 'TODO:' that has been defined as forbidden in detekt.
```
```kotlin
212 
213         internal operator fun invoke(gitDir: File, maxCount: Provider<Int>? = null): GitCache {
214             // val cache: GitCache? = gitCache
215             // TODO: improve in-memory cache as `cache.shouldRefresh()` is flaky
!!!             ^ error
216             // if (cache == null || cache.shouldRefresh() || true) {
217             gitCache = GitCache(gitDir, maxCount)
218             // }

```

### style, MaxLineLength (4)

Line detected, which is longer than the defined maximum line length in the code style.

[Documentation](https://detekt.dev/docs/rules/style#maxlinelength)

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:38:13
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
35             `4_ Run gradlew assemble`()
36             `5_ Create, add and commit a new file in library-one-a, then run gradlew assemble`()
37             `6_ Run gradlew createSemverTag tagPrefix=a`()
38             `7_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=alpha tagPrefix=a`()
!!             ^ error
39             `8_ Run gradlew createSemverTag stage=beta tagPrefix=a`()
40             `9_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=final tagPrefix=a`()
41             `10_ Run gradlew createSemverTag stage=final scope=major tagPrefix=a`()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:40:13
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
37             `6_ Run gradlew createSemverTag tagPrefix=a`()
38             `7_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=alpha tagPrefix=a`()
39             `8_ Run gradlew createSemverTag stage=beta tagPrefix=a`()
40             `9_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=final tagPrefix=a`()
!!             ^ error
41             `10_ Run gradlew createSemverTag stage=final scope=major tagPrefix=a`()
42             `11_ Run gradlew printSemver stage=snapshot tagPrefix=a`()
43             `12_ Run gradlew createSemverTag scope=minor tagPrefix=b`()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:170:9
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
167     }
168 
169     private fun GradleRunner
170         .`7_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=alpha tagPrefix=a`() {
!!!         ^ error
171         projectDirByName("library-one-a").resolve("new7.txt").createNewFile()
172         git.add().addFilepattern(".").call()
173         git.commit().setMessage("Add new7 to library-one-a").call()

```

* semver-project-gradle-plugin/testFunctional/kotlin/com/javiersc/semver/project/gradle/plugin/examples/MultiProjectExampleTest.kt:205:9
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
202     }
203 
204     private fun GradleRunner
205         .`9_ Create, add to git and commit a new file in library-one-a, then run gradlew createSemverTag stage=final tagPrefix=a`() {
!!!         ^ error
206         projectDirByName("library-one-a").resolve("new9.txt").createNewFile()
207         git.add().addFilepattern(".").call()
208         git.commit().setMessage("Add new9 to library-one-a").call()

```

generated with [detekt version 1.22.0](https://detekt.dev/) on 2023-03-11 14:03:51 UTC
