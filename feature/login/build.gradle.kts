plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.login"
}

dependencies {
    // Domain
    implementation(project(":domain:account"))

    // Firebase Auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.ui.auth)
}
