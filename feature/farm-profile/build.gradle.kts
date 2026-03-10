plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.farm.profile"
}

dependencies {
    // Domain
    implementation(project(":domain:farm"))
    implementation(project(":domain:account"))
}
