plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.onboarding"
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.gson)
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
