plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.community"
}

dependencies {
    // Domain
    implementation(project(":domain:social"))
}
