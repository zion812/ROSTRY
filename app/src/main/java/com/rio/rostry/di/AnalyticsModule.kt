package com.rio.rostry.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepositoryImpl
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTrackerImpl
import com.rio.rostry.utils.analytics.AuthAnalyticsTracker
import com.rio.rostry.utils.analytics.AuthAnalyticsTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsBindingsModule {
    @Binds
    @Singleton
    abstract fun bindGeneralAnalyticsTracker(impl: GeneralAnalyticsTrackerImpl): GeneralAnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindAuthAnalyticsTracker(impl: AuthAnalyticsTrackerImpl): AuthAnalyticsTracker
}

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
    
    @Provides
    @Singleton

    fun provideAnalyticsRepository(
        analyticsDao: AnalyticsDao,
        firebaseAnalytics: FirebaseAnalytics,
        taskDao: TaskDao,
        dailyLogDao: DailyLogDao,
        vaccinationRecordDao: VaccinationRecordDao,
        productDao: com.rio.rostry.data.database.dao.ProductDao,
        hatchingBatchDao: com.rio.rostry.data.database.dao.HatchingBatchDao
    ): AnalyticsRepository {
        return AnalyticsRepositoryImpl(
            analyticsDao,
            firebaseAnalytics,
            taskDao,
            dailyLogDao,
            vaccinationRecordDao,
            productDao,
            hatchingBatchDao
        )
    }
}
