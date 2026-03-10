plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.domain.service"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":domain:social"))
    implementation(libs.javax.inject)
}
