plugins {
    id("rostry.android.feature")
}

android {
    namespace = "com.rio.rostry.feature.farm.profile"
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.code.gson:gson:2.10.1")
    // Domain
    implementation(project(":domain:farm"))
    implementation(project(":domain:account"))
}
