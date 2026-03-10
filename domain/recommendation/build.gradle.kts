plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.domain.recommendation"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.javax.inject)
}
