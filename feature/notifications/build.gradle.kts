plugins {
    id("rostry.android.library.compose")
}

android {
    namespace = "com.rio.rostry.feature.notifications"
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.code.gson:gson:2.10.1")
    // Domain
    implementation(project(":domain:account"))

    implementation(project(":core:common"))
    implementation(project(":domain:account"))
    implementation(project(":domain:commerce"))
    implementation(project(":domain:farm"))
    implementation(project(":domain:monitoring"))
    implementation(project(":domain:social"))
    implementation(project(":domain:admin"))
    implementation(project(":data:account"))
    implementation(project(":data:commerce"))
    implementation(project(":data:farm"))
    implementation(project(":data:monitoring"))
    implementation(project(":data:social"))
    implementation(project(":data:admin"))

    implementation(project(":core:navigation"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
}
