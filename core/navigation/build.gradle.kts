plugins {
    id("rostry.android.library")
    id("rostry.android.library.compose")
}

android {
    namespace = "com.rio.rostry.core.navigation"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:common"))

    // Navigation Compose
    api(libs.androidx.navigation.compose)

    // Coroutines (using version from Kotlin)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Testing
    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
}
