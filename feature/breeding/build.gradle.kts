plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.breeding"
}

dependencies {
    // Domain
    implementation(project(":domain:farm"))
    implementation(project(":domain:monitoring"))
}
