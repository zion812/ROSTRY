plugins {
    id("rostry.android.library")
}

android {
    namespace = "com.rio.rostry.domain.social"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(libs.paging.runtime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)


    testImplementation(project(":core:testing"))
}
