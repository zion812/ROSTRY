package com.rio.rostry.di

import android.content.Context
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.DeliveryHubDao
import com.rio.rostry.data.database.dao.HubAssignmentDao
import com.rio.rostry.data.database.dao.KycVerificationDao
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ExpenseDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ProductVerificationDraftDao
import com.rio.rostry.data.database.dao.ProfitabilityMetricsDao
import com.rio.rostry.data.database.dao.TransferAnalyticsDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.dao.VerificationRequestDao
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.domain.breeding.BreedingCompatibilityCalculator
import com.rio.rostry.domain.breeding.EnhancedBreedingSystem
import com.rio.rostry.domain.breeding.GeneticEngine
import com.rio.rostry.domain.manager.ConfigurationManager
import com.rio.rostry.domain.manager.DegradationManager
import com.rio.rostry.domain.verification.VerificationSystem
import com.rio.rostry.domain.verification.VerificationSystemImpl
import com.rio.rostry.marketplace.intelligence.DisputeManager
import com.rio.rostry.marketplace.intelligence.HubAssignmentService
import com.rio.rostry.marketplace.intelligence.RecommendationEngine
import com.rio.rostry.domain.analytics.AnalyticsEngineImpl
import com.rio.rostry.domain.service.GracefulDegradationService
import com.rio.rostry.domain.transfer.TransferAnalyticsService
import com.rio.rostry.notifications.NotificationTriggerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DI Module for production-readiness features:
 * - Verification System
 * - Marketplace Intelligence (Recommendations, Hub Assignment, Disputes)
 * - Analytics Engine
 * - Transfer Analytics
 * - Graceful Degradation
 * - Notification Triggers
 * - Enhanced Breeding System
 */
@Module
@InstallIn(SingletonComponent::class)
object ProductionReadinessModule {

    // ─── Verification System ────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideVerificationSystem(
        productDraftDao: ProductVerificationDraftDao,
        kycDao: KycVerificationDao,
        verificationRequestDao: VerificationRequestDao,
        userDao: UserDao,
        auditLogDao: com.rio.rostry.data.database.dao.AuditLogDao,
        userRepository: com.rio.rostry.data.repository.UserRepository,
        notificationService: com.rio.rostry.notifications.IntelligentNotificationService,
        gson: Gson
    ): VerificationSystem {
        return VerificationSystemImpl(productDraftDao, kycDao, verificationRequestDao, userDao, auditLogDao, userRepository, notificationService, gson)
    }

    // ─── Marketplace Intelligence ───────────────────────────────────────

    @Provides
    @Singleton
    fun provideRecommendationEngine(
        productDao: ProductDao,
        orderDao: OrderDao
    ): RecommendationEngine {
        return RecommendationEngine(productDao, orderDao)
    }

    @Provides
    @Singleton
    fun provideHubAssignmentService(
        hubDao: DeliveryHubDao,
        hubAssignmentDao: HubAssignmentDao,
        configurationManager: ConfigurationManager
    ): HubAssignmentService {
        return HubAssignmentService(hubDao, hubAssignmentDao, configurationManager)
    }

    @Provides
    @Singleton
    fun provideDisputeManager(
        orderDao: OrderDao
    ): DisputeManager {
        return DisputeManager(orderDao)
    }

    // ─── Analytics ──────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideAnalyticsEngine(
        orderDao: OrderDao,
        expenseDao: ExpenseDao,
        profitabilityDao: ProfitabilityMetricsDao,
        @ApplicationContext context: Context
    ): AnalyticsEngineImpl {
        return AnalyticsEngineImpl(orderDao, expenseDao, profitabilityDao, context)
    }

    @Provides
    @Singleton
    fun provideTransferAnalyticsService(
        transferAnalyticsDao: TransferAnalyticsDao,
        @ApplicationContext context: Context
    ): TransferAnalyticsService {
        return TransferAnalyticsService(transferAnalyticsDao, context)
    }

    // ─── Resilience ─────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideGracefulDegradationService(
        degradationManager: DegradationManager,
        productDao: ProductDao,
        profitabilityDao: ProfitabilityMetricsDao
    ): GracefulDegradationService {
        return GracefulDegradationService(degradationManager, productDao, profitabilityDao)
    }

    // ─── Notifications ──────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideNotificationTriggerService(
        notificationDao: NotificationDao,
        degradationManager: DegradationManager
    ): NotificationTriggerService {
        return NotificationTriggerService(notificationDao, degradationManager)
    }

    // ─── Enhanced Breeding ──────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideEnhancedBreedingSystem(
        compatibilityCalculator: BreedingCompatibilityCalculator,
        geneticEngine: GeneticEngine,
        productDao: ProductDao,
        productRepository: ProductRepository
    ): EnhancedBreedingSystem {
        return EnhancedBreedingSystem(compatibilityCalculator, geneticEngine, productDao, productRepository)
    }
}
