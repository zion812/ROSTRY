plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.onboarding"
}

dependencies {
    // Domain
    implementation(project(":domain:account"))
    implementation(project(":domain:monitoring"))

    // Core (beyond baseline)
    implementation(project(":core:database"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)

    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.timber)
}
