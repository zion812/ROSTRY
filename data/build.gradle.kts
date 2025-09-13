plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
}

kapt {
    correctErrorTypes = true
}

android {
    namespace = "com.rio.rostry.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    api(project(":domain"))
    implementation(project(":core"))

    // Kotlin + AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.work.runtime.ktx)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    kapt(libs.room.compiler)

    // SQLCipher for Room encryption
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    // AndroidX Security Crypto for encrypted key storage
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Hilt Work for @HiltWorker in this module
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    // Firebase (BOM-managed)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.functions.ktx)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Firebase Task await support
    implementation(libs.kotlinx.coroutines.play.services)

    // Logging
    implementation(libs.timber)

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // Instrumentation tests (androidTest)
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
}
