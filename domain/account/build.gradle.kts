plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.domain.account"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(project(":core:testing"))
}
