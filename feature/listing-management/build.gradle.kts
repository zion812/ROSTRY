plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.listing.management"
}

dependencies {
    // Domain
    implementation(project(":domain:commerce"))
    implementation(project(":domain:farm"))
}
