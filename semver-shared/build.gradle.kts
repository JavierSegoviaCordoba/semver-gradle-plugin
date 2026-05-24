hubdle {
    config {
        analysis()
        coverage()
        documentation { //
            api()
        }
        explicitApi()
        publishing {
            gradlePortal { //
                isEnabled = false
            }
        }
    }
    kotlin {
        jvm {
            features { //
                jvmVersion(JavaVersion.VERSION_17)
                kotest()
            }

            main {
                dependencies {
                    api(projects.gradleVersion)
                    implementation(hubdle.eclipse.jgit)
                    compileOnly(gradleKotlinDsl())
                }
            }

            testFixtures { dependencies { api(hubdle.eclipse.jgit) } }
        }
    }

    gradle {
        plugin { //
        }
    }
}
