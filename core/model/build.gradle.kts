plugins {
    id("rostry.android.library")
    id("kotlin-parcelize")
}

android {
    namespace = "com.rio.rostry.core.model"
}

dependencies {
    // Gson (serialization annotations in some models)
    implementation(libs.gson)

    // Firebase Firestore (ServerTimestamp annotation in VerificationSubmission)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)

    // Compose UI primitives used in domain models (Color in BirdAppearance, Offset in DigitalFarmModels)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
}
