plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.asset.management"
}

dependencies {
    // Domain
    implementation(project(":domain:farm"))
}
