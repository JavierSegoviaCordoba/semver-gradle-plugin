plugins {
    `kotlin-jvm`
    `java-gradle-plugin`
    `javiersc-kotlin-library`
    `javiersc-publish`
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(libs.eclipse.jgit.eclipseJgit)
}
