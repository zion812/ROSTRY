plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.core.common"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.timber)
    implementation(libs.datastore.preferences)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)

    // Hilt
    implementation(libs.hilt.android)
    // No kapt here usually if it's just implementation, but if we need to generate code...
    // Actually, ConnectivityManager just needs @Inject. 
    // If it's used in :app, :app will do the final wiring.
}

