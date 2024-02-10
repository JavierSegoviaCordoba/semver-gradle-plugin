hubdle {
    config {
        analysis()
        coverage()
        documentation {
            api()
        }
        explicitApi()
        languageSettings {
            experimentalCoroutinesApi()
        }
        publishing()
    }
    kotlin {
        jvm {
            features {
                coroutines()
            }
            test {
                dependencies {
                    implementation(hubdle.kotest.property)
                }
            }
        }
    }
}
