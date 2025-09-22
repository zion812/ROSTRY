package com.rio.rostry.di

import android.content.Context
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.notif.TransferNotifier
import com.rio.rostry.utils.notif.TransferNotifierImpl
import com.rio.rostry.utils.notif.SocialNotifier
import com.rio.rostry.utils.notif.SocialNotifierImpl
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    // Add other application-wide singletons here later (e.g., SharedPreferences)

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideTransferNotifier(@ApplicationContext context: Context): TransferNotifier {
        return TransferNotifierImpl(context)
    }

    @Provides
    @Singleton
    fun provideSocialNotifier(@ApplicationContext context: Context): SocialNotifier {
        return SocialNotifierImpl(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
