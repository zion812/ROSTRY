plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.farm.dashboard"
}

dependencies {
    // Domain
    implementation(project(":domain:farm"))
    implementation(project(":domain:account"))
    implementation(project(":domain:commerce"))
    implementation(project(":domain:monitoring"))
    implementation(project(":domain:social"))

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

    // Vico Charts
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
}
