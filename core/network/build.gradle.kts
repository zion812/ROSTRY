plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)

    // Timber
    implementation(libs.timber)

    // Coroutines
    implementation(libs.androidx.core.ktx)

    // DI
    implementation("javax.inject:javax.inject:1")
}
