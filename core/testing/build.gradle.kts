plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.core.testing"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    api(project(":core:navigation"))
    
    // Testing frameworks
    api(libs.junit)
    api(libs.androidx.junit)
    api(libs.androidx.espresso.core)
    api(libs.androidx.test.core)
    api(libs.kotlinx.coroutines.test)
    
    // MockK for mocking
    api(libs.mockk.android)
    api(libs.mockk)
    
    // Robolectric for Android unit tests
    api(libs.robolectric)
}
