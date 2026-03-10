import java.net.URL
import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // Dokka for module-level API documentation
    id("org.jetbrains.dokka")
    // JaCoCo for unit test coverage reports
    id("jacoco")
    id("kotlin-parcelize")
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
        multiDexEnabled = true

        testInstrumentationRunner = "com.rio.rostry.HiltTestRunner"
        // Pass App Check debug secret from CI environment for instrumentation tests
        testInstrumentationRunnerArguments["firebaseAppCheckDebugSecret"] =
            System.getenv("APP_CHECK_DEBUG_TOKEN_FROM_CI") ?: ""
        // Feature flags
        buildConfigField("boolean", "FEATURE_QR_CAMERA", "true")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
            val releaseJsKey = mapsJsApiKeyProvider.orElse(releaseKey).get()
            buildConfigField("String", "MAPS_JS_API_KEY", "\"$releaseJsKey\"")
            buildConfigField("boolean", "PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING", "false")
        }
        debug {
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
            val debugKey = mapsApiKeyProvider.orElse("debug-placeholder").get()
            buildConfigField("String", "MAPS_API_KEY", "\"$debugKey\"")
            manifestPlaceholders["MAPS_API_KEY"] = debugKey
            val debugJsKey = mapsJsApiKeyProvider.orElse(debugKey).get()
            buildConfigField("String", "MAPS_JS_API_KEY", "\"$debugJsKey\"")
            buildConfigField("boolean", "PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING", "true")
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = false
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
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

    lint {
        baseline = file("lint-baseline.xml")
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
            "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
            "androidx/compose/**", "kotlin/**"
        )
    }
    val kotlinClasses = fileTree("${layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
        exclude(
            "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
            "androidx/compose/**", "kotlin/**"
        )
    }
    classDirectories.setFrom(files(javaClasses, kotlinClasses))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree("${layout.buildDirectory.get().asFile}") {
        include("**/jacoco/testDebugUnitTest.exec", "**/jacoco/testDebugUnitTest.exec*", "**/jacoco.exec")
    })
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
    // ──────────────────────────────────────────────
    // Module Dependencies
    // ──────────────────────────────────────────────
    // Core
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:domain"))

    // Feature modules
    implementation(project(":feature:achievements"))
    implementation(project(":feature:leaderboard"))
    implementation(project(":feature:notifications"))
    implementation(project(":feature:events"))
    implementation(project(":feature:expert"))
    implementation(project(":feature:insights"))
    implementation(project(":feature:feedback"))
    implementation(project(":feature:support"))
    implementation(project(":feature:login"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:farm-dashboard"))
    implementation(project(":feature:asset-management"))
    implementation(project(":feature:listing-management"))
    
    // New feature modules (Phase 1 Migration)
    implementation(project(":feature:marketplace"))
    implementation(project(":feature:social-feed"))
    implementation(project(":feature:monitoring"))
    implementation(project(":feature:farmer-tools"))
    implementation(project(":feature:enthusiast-tools"))
    implementation(project(":feature:analytics"))
    implementation(project(":feature:transfers"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:general"))
    implementation(project(":feature:traceability"))

    // Data modules
    implementation(project(":data:account"))
    implementation(project(":data:commerce"))
    implementation(project(":data:farm"))
    implementation(project(":data:monitoring"))
    implementation(project(":data:social"))
    implementation(project(":data:admin"))

    // Domain modules
    implementation(project(":domain:account"))
    implementation(project(":domain:farm"))
    implementation(project(":domain:commerce"))
    implementation(project(":domain:monitoring"))
    implementation(project(":domain:social"))
    implementation(project(":domain:admin"))

    // ──────────────────────────────────────────────
    // AndroidX & Compose
    // ──────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ──────────────────────────────────────────────
    // DataStore
    // ──────────────────────────────────────────────
    implementation(libs.datastore.preferences)

    // ──────────────────────────────────────────────
    // Hilt (KSP)
    // ──────────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.ext)
    ksp(libs.hilt.ext.compiler)

    // ──────────────────────────────────────────────
    // Room
    // ──────────────────────────────────────────────
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.room.paging)
    androidTestImplementation(libs.room.testing)

    // ──────────────────────────────────────────────
    // Encrypted database support
    // ──────────────────────────────────────────────
    implementation(libs.sqlcipher)
    implementation(libs.sqlite.ktx)
    implementation(libs.security.crypto)

    // ──────────────────────────────────────────────
    // Serialization & Networking
    // ──────────────────────────────────────────────
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // ──────────────────────────────────────────────
    // Firebase
    // ──────────────────────────────────────────────
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.firebase.remote.config.ktx)
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)

    // ──────────────────────────────────────────────
    // Google Maps Platform
    // ──────────────────────────────────────────────
    implementation(libs.google.maps.android)
    implementation(libs.google.places.new)
    implementation(libs.google.maps.compose)
    implementation(libs.play.services.location)

    // ──────────────────────────────────────────────
    // WorkManager
    // ──────────────────────────────────────────────
    implementation(libs.work.runtime.ktx)

    // ──────────────────────────────────────────────
    // Utilities
    // ──────────────────────────────────────────────
    implementation(libs.timber)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.coil.compose)
    implementation(libs.compressor)
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.ui)
    implementation(libs.google.accompanist.swiperefresh)
    implementation(libs.zxing.core)
    implementation(libs.exifinterface)
    implementation(libs.opencsv)
    implementation(libs.barcode.scanning)

    // Vico Charts
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)

    // ──────────────────────────────────────────────
    // CameraX
    // ──────────────────────────────────────────────
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ──────────────────────────────────────────────
    // Debug tooling
    // ──────────────────────────────────────────────
    debugImplementation(libs.leakcanary)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ──────────────────────────────────────────────
    // Unit Testing
    // ──────────────────────────────────────────────
    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.archunit.junit4)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)

    // ──────────────────────────────────────────────
    // Android Instrumentation Testing
    // ──────────────────────────────────────────────
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.firebase.appcheck.debug)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.arch.core.testing)
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
    }
}
