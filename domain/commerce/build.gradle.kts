plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.domain.commerce"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(project(":core:testing"))
}
