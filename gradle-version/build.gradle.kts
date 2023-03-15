hubdle {
    config {
        analysis()
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
                    implementation(kotestProperty())
                }
            }
        }
    }
}
