package com.rio.rostry.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// DISABLED: Free Tier - Cloud Functions not available on Spark Plan
// import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    // DISABLED: Free Tier - Cloud Functions not available on Spark Plan
    // Re-enable when upgrading to Blaze Plan
    // @Provides
    // @Singleton
    // fun provideFirebaseFunctions(): FirebaseFunctions {
    //     return FirebaseFunctions.getInstance()
    // }

    // Add Retrofit, OkHttpClient, Moshi/Gson converters here if you plan to use a REST API
}

