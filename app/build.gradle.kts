import java.net.URL
import java.net.URI
import java.util.Properties

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
    // JaCoCo for unit test coverage reports
    id("jacoco")
}

// Export Room schemas for migration testing (KSP)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val mapsApiKeyProvider = providers.gradleProperty("MAPS_API_KEY")
    .orElse(providers.provider { localProps.getProperty("MAPS_API_KEY") ?: "" })

// Separate key for Maps JavaScript (WebView) usage; can be a browser key.
// Falls back to MAPS_API_KEY if MAPS_JS_API_KEY is not defined.
val mapsJsApiKeyProvider = providers.gradleProperty("MAPS_JS_API_KEY")
    .orElse(providers.provider { localProps.getProperty("MAPS_JS_API_KEY") ?: mapsApiKeyProvider.get() })

// Pin JaCoCo tool version to avoid instrumentation incompatibilities
jacoco {
    toolVersion = "0.8.10"
}

// ---- Version bump automation ----
tasks.register("bumpVersionCode") {
    group = "versioning"
    description = "Automatically increments versionCode in app/build.gradle.kts"
    doLast {
        val buildFile = file("build.gradle.kts")
        val content = buildFile.readText()
        val regex = Regex("versionCode\\s*=\\s*(\\d+)")
        val match = regex.find(content) ?: throw GradleException("versionCode not found in app/build.gradle.kts")
        val current = match.groupValues[1].toInt()
        val updated = content.replace(regex, "versionCode = ${current + 1}")
        buildFile.writeText(updated)
        println("Bumped versionCode: $current -> ${current + 1}")
    }
}

// Hook version bump before assembleRelease without relying on afterEvaluate
tasks.matching { it.name == "assembleRelease" }.all {
    dependsOn("bumpVersionCode")
}
android {
    namespace = "com.rio.rostry"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rio.rostry"
        minSdk = 24
        targetSdk = 36
        versionCode = 9
        versionName = "1.0"

        testInstrumentationRunner = "com.rio.rostry.HiltTestRunner"
        // Pass App Check debug secret from CI environment for instrumentation tests
        testInstrumentationRunnerArguments["firebaseAppCheckDebugSecret"] =
            System.getenv("APP_CHECK_DEBUG_TOKEN_FROM_CI") ?: ""
        // Feature flags
        buildConfigField("boolean", "FEATURE_QR_CAMERA", "true")
    }
    
    // Enable ABI splits to generate separate APKs per architecture
    // This reduces APK size by ~30-40% per architecture
    // Temporarily disabled - uncomment and verify AGP version compatibility
    /*
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false // Set to true if you need a universal APK for testing
        }
    }
    */

    buildTypes {
        release {
            isMinifyEnabled = true // Enabled minification for ProGuard
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Upload Crashlytics mapping files on release builds
            firebaseCrashlytics {
                mappingFileUploadEnabled = true
            }
            val releaseKey = mapsApiKeyProvider.get()
            if (releaseKey.isBlank() || releaseKey == "your_api_key_here" || releaseKey == "<set me>" || releaseKey == "debug-placeholder") {
                throw GradleException("MAPS_API_KEY is missing. Please check local.properties.template and docs/api-keys-setup.md for instructions on how to configure this key.")
            }
            buildConfigField("String", "MAPS_API_KEY", "\"$releaseKey\"")
            manifestPlaceholders["MAPS_API_KEY"] = releaseKey
            // Use a dedicated JS key if provided; otherwise fall back to the Android key.
            val releaseJsKey = mapsJsApiKeyProvider.orElse(releaseKey).get()
            buildConfigField("String", "MAPS_JS_API_KEY", "\"$releaseJsKey\"")
            buildConfigField("boolean", "PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING", "false")
        }
        debug {
            // Disable runtime coverage to avoid JaCoCo bytecode instrumentation interfering with Compose at runtime
            // You can still generate unit test coverage via the jacocoTestReport task below
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
            // Allow a safe debug fallback to avoid build breaks during local development
            val debugKey = mapsApiKeyProvider.orElse("debug-placeholder").get()
            buildConfigField("String", "MAPS_API_KEY", "\"$debugKey\"")
            manifestPlaceholders["MAPS_API_KEY"] = debugKey
            // Dedicated JS key for debug WebView; falls back to MAPS_API_KEY if not set.
            val debugJsKey = mapsJsApiKeyProvider.orElse(debugKey).get()
            buildConfigField("String", "MAPS_JS_API_KEY", "\"$debugJsKey\"")
            buildConfigField("boolean", "PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = false // Disable if not using Java 8+ APIs on older devices
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

    sourceSets {
        getByName("androidTest") {
            assets.srcDirs(files("$projectDir/schemas"))
        }
    }
    androidResources {
        localeFilters.addAll(listOf("en"))
    }
}

// ---- Coverage reporting (JaCoCo) ----
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val javaClasses = fileTree("${layout.buildDirectory.get().asFile}/intermediates/javac/debug/classes") {
        exclude(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            // Exclude Compose internals and Kotlin stdlib from coverage
            "androidx/compose/**",
            "kotlin/**"
        )
    }
    val kotlinClasses = fileTree("${layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
        exclude(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            // Exclude Compose internals and Kotlin stdlib from coverage
            "androidx/compose/**",
            "kotlin/**"
        )
    }
    classDirectories.setFrom(files(javaClasses, kotlinClasses))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree("${layout.buildDirectory.get().asFile}") { include("**/jacoco/testDebugUnitTest.exec", "**/jacoco/testDebugUnitTest.exec*", "**/jacoco.exec") })
}

// ---- APK size check ----
tasks.register("checkApkSize") {
    group = "verification"
    description = "Checks the release APK size and fails if it exceeds threshold"
    dependsOn("assembleRelease")
    doLast {
        val apk = file("build/outputs/apk/release/app-release.apk")
        if (apk.exists()) {
            val sizeMb = apk.length().toDouble() / (1024.0 * 1024.0)
            val thresholdMb = (project.findProperty("APK_SIZE_MB_THRESHOLD") as String?)?.toDoubleOrNull() ?: 50.0
            val sizeStr = "%.2f".format(sizeMb)
            println("APK size: $sizeStr MB (threshold $thresholdMb MB)")
            if (sizeMb > thresholdMb) {
                throw GradleException("APK too large: $sizeStr MB > $thresholdMb MB")
            }
        } else {
            println("Release APK not found at ${apk.path}. Run assembleRelease first.")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    
    // Firebase BoM - manages all Firebase library versions
    implementation(platform(libs.firebase.bom))
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
    // Room testing for migration tests
    androidTestImplementation("androidx.room:room-testing:2.6.1")

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
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:22.1.2")

    // Google Maps Platform
    implementation(libs.google.maps.android)
    implementation(libs.google.places.new)
    implementation(libs.google.maps.compose)

    // Firebase App Check
    implementation(libs.firebase.appcheck)
implementation(libs.firebase.appcheck.playintegrity)
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

    // Accompanist SwipeRefresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

    // Google Play Services - Location
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Firebase Performance Monitoring
    implementation("com.google.firebase:firebase-perf-ktx:21.0.1")

    // LeakCanary (debug)
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    // ViewModel for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation(libs.mockk)
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.23")
    testImplementation("com.tngtech.archunit:archunit-junit4:1.3.0")
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)

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
    // Firebase App Check debug testing (share debug provider library with androidTest)
    // Use the same version catalog entry as debugImplementation to keep versions aligned
    androidTestImplementation(libs.firebase.appcheck.debug)
    // Truth and Mockito for instrumentation tests
    androidTestImplementation("com.google.truth:truth:1.4.2")
    androidTestImplementation("org.mockito:mockito-core:5.12.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    // Misc
    implementation("com.google.zxing:core:3.5.2")
    implementation("androidx.exifinterface:exifinterface:1.3.7")

    // Compose tooling (debug)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // CameraX
    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
}
// Dokka: enrich module docs with external links for Kotlin and Android APIs
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        // Kotlin stdlib docs
        externalDocumentationLink {
            url.set(URI.create("https://kotlinlang.org/api/kotlin/").toURL())
            packageListUrl.set(URI.create("https://kotlinlang.org/api/kotlin/package-list").toURL())
        }
        // Android framework docs
        externalDocumentationLink {
            url.set(URI.create("https://developer.android.com/reference/").toURL())
            packageListUrl.set(URI.create("https://developer.android.com/reference/android/package-list").toURL())
        }
        // AndroidX docs
        externalDocumentationLink {
            url.set(URI.create("https://developer.android.com/reference/").toURL())
            packageListUrl.set(URI.create("https://developer.android.com/reference/androidx/package-list").toURL())
        }

        // Optional: link source to GitHub (uncomment and set correct repo URL)
        // sourceLink {
        //     localDirectory.set(file("src"))
        //     remoteUrl.set(URL("https://github.com/<org>/<repo>/tree/main/app/src"))
        //     remoteLineSuffix.set("#L")
        // }
    }
}
