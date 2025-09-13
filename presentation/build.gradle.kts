plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.rio.rostry.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    buildTypes {
        release {
            buildConfigField("boolean", "DEV_SHORTCUTS_ENABLED", "false")
        }
        debug {
            buildConfigField("boolean", "DEV_SHORTCUTS_ENABLED", "true")
        }
    }
}

dependencies {
    api(project(":domain"))
    implementation(project(":core"))
    implementation(project(":data"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.biometric)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Firebase Auth for PhoneAuthHelper
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)

    // CameraX for QR scanning
    val cameraXVersion = "1.3.4"
    implementation("androidx.camera:camera-core:$cameraXVersion")
    implementation("androidx.camera:camera-camera2:$cameraXVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraXVersion")
    implementation("androidx.camera:camera-view:$cameraXVersion")
    // ML Kit barcode scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    // CameraX dependency requires ListenableFuture
    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    implementation("com.google.guava:guava:31.1-android")

    // QR Code generation
    implementation("com.google.zxing:core:3.5.1")

    // Unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation(libs.kotlinx.coroutines.test)
}
