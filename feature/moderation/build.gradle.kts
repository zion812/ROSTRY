plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.moderation"
}

dependencies {
    // Domain
    implementation(project(":domain:admin"))
}
