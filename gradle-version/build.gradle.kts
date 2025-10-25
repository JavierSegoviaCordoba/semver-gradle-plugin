hubdle {
    config {
        analysis()
        coverage()
        documentation { //
            api()
        }
        explicitApi()
        languageSettings { //
            experimentalCoroutinesApi()
        }
        publishing()
    }
    kotlin {
        jvm {
            features {
                coroutines()
                jvmVersion(JavaVersion.VERSION_11)
                kotest()
            }
            test {
                dependencies { //
                    implementation(hubdle.kotest.property)
                }
            }
        }
    }
}
