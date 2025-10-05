import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    // Firebase Performance Monitoring plugin (applies with BOM/dependency)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp")
    // Dokka for module-level API documentation
    id("org.jetbrains.dokka")
}
android {
    namespace = "com.rio.rostry"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rio.rostry"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.rio.rostry.HiltTestRunner"
        // Expose Google Maps API key via BuildConfig for Places/Maps initialization
        buildConfigField("String", "MAPS_API_KEY", "\"${project.findProperty("MAPS_API_KEY") ?: "your_api_key_here"}\"")
        // Provide manifest placeholder used by AndroidManifest meta-data value
        manifestPlaceholders += mapOf(
            "MAPS_API_KEY" to (project.findProperty("MAPS_API_KEY") ?: "your_api_key_here")
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Enabled minification for ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{LICENSE*,NOTICE*,AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.firebase.crashlytics)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Biometric
    implementation(libs.androidx.biometric)

    // DataStore for session management
    implementation(libs.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Navigation with Hilt
    implementation(libs.hilt.navigation.compose)

    // Hilt AndroidX extensions (for WorkManager)
    implementation(libs.hilt.ext)
    ksp(libs.hilt.ext.compiler)

    // Room
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation("androidx.room:room-paging:2.6.1")

    // Encrypted database support
    implementation(libs.sqlcipher)
    implementation(libs.sqlite.ktx)
    implementation(libs.security.crypto)

    // Gson for converters and JSON
    implementation(libs.gson)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Firebase
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.functions.ktx)
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:22.1.2")

    // Google Maps Platform
    implementation(libs.google.maps.android)
    implementation(libs.google.places.new)

    // Firebase App Check (Debug Provider for development)
    implementation(libs.firebase.appcheck)
    debugImplementation(libs.firebase.appcheck.debug)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Timber
    implementation(libs.timber)

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.paging:paging-compose:3.3.2")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Image compression
    implementation("id.zelory:compressor:3.0.1")

    // Media playback
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")

    // Google Play Services - Location
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Firebase Performance Monitoring
    implementation("com.google.firebase:firebase-perf-ktx:21.0.1")

    // LeakCanary (debug)
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    // ViewModel for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation(libs.mockk)

    // AndroidTest
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")

    // Misc
    implementation("com.google.zxing:core:3.5.2")
    implementation("androidx.exifinterface:exifinterface:1.3.7")

    // Compose tooling (debug)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
// Dokka: enrich module docs with external links for Kotlin and Android APIs
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        // Kotlin stdlib docs
        externalDocumentationLink {
            url.set(URL("https://kotlinlang.org/api/kotlin/"))
            packageListUrl.set(URL("https://kotlinlang.org/api/kotlin/package-list"))
        }
        // Android framework docs
        externalDocumentationLink {
            url.set(URL("https://developer.android.com/reference/"))
            packageListUrl.set(URL("https://developer.android.com/reference/android/package-list"))
        }
        // AndroidX docs
        externalDocumentationLink {
            url.set(URL("https://developer.android.com/reference/"))
            packageListUrl.set(URL("https://developer.android.com/reference/androidx/package-list"))
        }

        // Optional: link source to GitHub (uncomment and set correct repo URL)
        // sourceLink {
        //     localDirectory.set(file("src"))
        //     remoteUrl.set(URL("https://github.com/<org>/<repo>/tree/main/app/src"))
        //     remoteLineSuffix.set("#L")
        // }
    }
}
