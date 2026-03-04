plugins {
    id("rostry.android.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.rio.rostry.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))

    // Room (api so :app can access AppDatabase, DAOs, entities)
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)

    // Gson for type converters
    implementation(libs.gson)

    // Firebase (UserEntity uses @Exclude, @PropertyName)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)

    // Timber (logging in entity converters)
    implementation(libs.timber)

    // Paging (DAOs that return PagingSource)
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.room:room-paging:2.7.0")
}
