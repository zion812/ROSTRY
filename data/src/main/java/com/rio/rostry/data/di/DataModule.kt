package com.rio.rostry.data.di

import android.content.Context
import androidx.room.Room
import com.rio.rostry.data.local.db.AppDatabase
import com.rio.rostry.data.local.db.OutboxDao
import com.rio.rostry.data.repository.OutboxRepositoryImpl
import com.rio.rostry.domain.repository.OutboxRepository
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
}
