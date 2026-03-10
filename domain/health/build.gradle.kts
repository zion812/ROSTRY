plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.domain.health"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":domain:monitoring"))
    implementation(libs.javax.inject)
}
