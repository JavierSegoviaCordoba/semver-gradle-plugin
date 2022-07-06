buildscript {
    dependencies {
        classpath(libs.jetbrains.kotlin.kotlinGradlePlugin)
    }
}


plugins {
    alias(libs.plugins.javiersc.hubdle)
}

hubdle {
    config {
        analysis()
        binaryCompatibilityValidator()
        coverage()
        documentation {
            changelog()
            readme {
                badges()
            }
            site()
        }
        nexus()
    }
}
