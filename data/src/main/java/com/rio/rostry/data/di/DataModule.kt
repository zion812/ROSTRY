package com.rio.rostry.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.local.db.AppDatabase
import com.rio.rostry.data.local.db.OutboxDao
import com.rio.rostry.data.auth.AuthRepositoryImpl
import com.rio.rostry.data.repository.OutboxRepositoryImpl
import com.rio.rostry.data.rbac.RbacServiceImpl
import com.rio.rostry.data.session.SessionManagerImpl
import com.rio.rostry.data.profile.UserProfileRepositoryImpl
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.repository.OutboxRepository
import com.rio.rostry.domain.rbac.RbacService
import com.rio.rostry.domain.session.SessionManager
import com.rio.rostry.domain.profile.UserProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "rostry.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideOutboxDao(db: AppDatabase): OutboxDao = db.outboxDao()

    @Provides
    @Singleton
    fun provideOutboxRepository(dao: OutboxDao): OutboxRepository = OutboxRepositoryImpl(dao)

    // Preferences for session management
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("rostry_session", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSessionManager(prefs: SharedPreferences): SessionManager = SessionManagerImpl(prefs)

    @Provides
    @Singleton
    fun provideRbacService(): RbacService = RbacServiceImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository = AuthRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        firestore: FirebaseFirestore,
    ): UserProfileRepository = UserProfileRepositoryImpl(firestore)
}

