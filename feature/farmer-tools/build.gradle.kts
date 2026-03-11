plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.rio.rostry.feature.farmer"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:account"))
    implementation(project(":domain:commerce"))
    implementation(project(":domain:farm"))
    implementation(project(":domain:monitoring"))
    implementation(project(":domain:social"))
    implementation(project(":domain:admin"))
    implementation(project(":data:account"))
    implementation(project(":data:commerce"))
    implementation(project(":data:farm"))
    implementation(project(":data:monitoring"))
    implementation(project(":data:social"))
    implementation(project(":data:admin"))

    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:database"))

    // Domain
    implementation(project(":domain:farm"))
    implementation(project(":domain:monitoring"))
    implementation(project(":domain:commerce"))
    implementation(project(":domain:account"))
    implementation(project(":domain:social"))

    // Data (temporary - should be removed after proper domain layer migration)
    implementation(project(":data:farm"))
    implementation(project(":data:monitoring"))
    implementation(project(":data:commerce"))
    implementation(project(":data:account"))
    implementation(project(":data:social"))

    // Temporary app module dependency removed to break circular dependency
    // TODO: Move shared screens (gallery, auction, error) to core modules
    // implementation(project(":app"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Timber for logging
    implementation(libs.timber)

    // Coil for image loading
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
