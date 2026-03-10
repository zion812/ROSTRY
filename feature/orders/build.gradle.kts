plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.orders"
}

dependencies {
    // Domain
    implementation(project(":domain:commerce"))
    implementation(project(":domain:farm"))
    implementation(project(":domain:account"))

    // Core (beyond baseline)
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    // Compose extras
    implementation(libs.androidx.compose.material.icons.extended)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Accompanist
    implementation(libs.google.accompanist.swiperefresh)

    // Utilities
    implementation(libs.timber)
    implementation(libs.gson)
}
