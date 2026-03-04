plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.core.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:database"))

    // Coroutines
    implementation(libs.androidx.core.ktx)

    // Timber
    implementation(libs.timber)

    // Gson (for serialization domain logic)
    implementation(libs.gson)

    // DI (javax.inject for @Inject/@Singleton)
    implementation("javax.inject:javax.inject:1")
}
