plugins {
    id("rostry.android.library")
    id("rostry.android.hilt")
}

android {
    namespace = "com.rio.rostry.data.farm"
}

dependencies {
    // Domain modules
    implementation(project(":domain:farm"))
    implementation(project(":domain:account"))
    implementation(project(":domain:commerce"))
    implementation(project(":domain:monitoring"))
    implementation(project(":domain:social"))
    implementation(project(":domain:admin"))

    // Core modules
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)

    implementation(libs.gson)
    implementation(libs.timber)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // CSV & PDF generation for reports
    implementation(libs.opencsv)

    // Testing
    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}


