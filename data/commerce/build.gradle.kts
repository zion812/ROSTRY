plugins {
    id("rostry.android.library")
    id("rostry.android.hilt")
}

android {
    namespace = "com.rio.rostry.data.commerce"
}

dependencies {
    // Domain module
    implementation(project(":domain:commerce"))

    // Core modules
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // JSON
    implementation(libs.gson)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
