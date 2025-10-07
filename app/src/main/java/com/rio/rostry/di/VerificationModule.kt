package com.rio.rostry.di

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.notifications.VerificationNotificationService
import com.rio.rostry.utils.images.ImageCompressor
import com.rio.rostry.utils.media.FirebaseStorageUploader
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.utils.validation.VerificationValidationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.inject.Named
import com.google.firebase.storage.FirebaseStorage

@Module
@InstallIn(SingletonComponent::class)
object VerificationModule {

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    @Named("verificationStorage")
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorageUploader(
        @Named("verificationStorage") firebaseStorage: FirebaseStorage,
        connectivityManager: ConnectivityManager,
        @ApplicationContext context: Context,
    ): FirebaseStorageUploader = FirebaseStorageUploader(firebaseStorage, ImageCompressor, connectivityManager, context)

    @Provides
    @Singleton
    fun provideVerificationValidationService(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
    ): VerificationValidationService = VerificationValidationService(context, userRepository)

    @Provides
    @Singleton
    fun provideVerificationNotificationService(
        firebaseMessaging: FirebaseMessaging,
        notificationDao: NotificationDao,
        @ApplicationContext context: Context,
        userRepository: UserRepository,
    ): VerificationNotificationService = VerificationNotificationService(firebaseMessaging, notificationDao, context, userRepository)
}
