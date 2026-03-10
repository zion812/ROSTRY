plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.domain.farm"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(project(":core:testing"))
}
