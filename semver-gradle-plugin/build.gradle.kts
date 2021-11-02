plugins {
    `kotlin-jvm`
    `java-gradle-plugin`
    `javiersc-kotlin-library`
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(libs.eclipse.jgit.eclipseJgit)
}
