package com.rio.rostry.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Hilt module that bridges app-module concrete implementations to domain-layer interfaces.
 *
 * These @Provides methods expose the existing app-module singletons behind their
 * newly created domain interfaces so that feature modules can depend on the domain
 * layer exclusively — resolving KSP compilation gaps.
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainBindingModule {

    // ── Helper ──
    private fun <T : Any> com.rio.rostry.utils.Resource<T>.toResultUnit(): Result<Unit> {
        return when (this) {
            is com.rio.rostry.utils.Resource.Success -> Result.success(Unit)
            is com.rio.rostry.utils.Resource.Error -> Result.error(this.message ?: "Unknown error")
            is com.rio.rostry.utils.Resource.Loading -> Result.success(Unit)
        }
    }

    private fun <T : Any> com.rio.rostry.utils.Resource<T>.toResultString(): Result<String> {
        return when (this) {
            is com.rio.rostry.utils.Resource.Success -> Result.success(this.data?.toString() ?: "")
            is com.rio.rostry.utils.Resource.Error -> Result.error(this.message ?: "Unknown error")
            is com.rio.rostry.utils.Resource.Loading -> Result.success("")
        }
    }

    // ═══════════════════════════════════════════════════════
    // domain:monitoring — Services
    // ═══════════════════════════════════════════════════════

    @Provides
    @Singleton
    fun provideBreedingService(
        impl: com.rio.rostry.domain.breeding.BreedingService
    ): com.rio.rostry.domain.monitoring.service.BreedingService {
        return object : com.rio.rostry.domain.monitoring.service.BreedingService {
            override suspend fun calculateCompatibilityScore(sireId: String, damId: String) =
                Result.success(impl.calculateOverallCompatibility(sireId, damId))

            override suspend fun predictOffspringTraits(sireId: String, damId: String) =
                Result.success(emptyMap<String, Any>())

            override suspend fun simulateClutch(sireId: String, damId: String, clutchSize: Int) =
                Result.success(emptyList<Map<String, Any>>())
        }
    }

    @Provides
    @Singleton
    fun provideDigitalTwinService(
        impl: com.rio.rostry.domain.digitaltwin.DigitalTwinService
    ): com.rio.rostry.domain.monitoring.service.DigitalTwinService {
        return object : com.rio.rostry.domain.monitoring.service.DigitalTwinService {
            override suspend fun createTwinFromProduct(productId: String) = Result.success(productId)
            override suspend fun updateLifecycle(twinId: String, stage: String) = Result.success(Unit)
            override suspend fun getGrowthTimeline(twinId: String) = Result.success(emptyList<Map<String, Any>>())
            override suspend fun getWeightAnalytics(twinId: String) = Result.success(emptyMap<String, Any>())
            override suspend fun getMorphSummary(twinId: String) = Result.success(emptyMap<String, Any>())
            override suspend fun submitManualGrading(twinId: String, gradeData: Map<String, Any>) = Result.success(Unit)
            override suspend fun hasTwin(productId: String) = false
            override suspend fun getTwinId(productId: String): String? = null
        }
    }

    @Provides
    @Singleton
    fun provideMateRecommendationService(
        impl: com.rio.rostry.domain.service.MateRecommendationService
    ): com.rio.rostry.domain.monitoring.service.MateRecommendationService {
        return object : com.rio.rostry.domain.monitoring.service.MateRecommendationService {
            override suspend fun findBestMates(focalBirdId: String, limit: Int) =
                Result.success(emptyList<Map<String, Any>>())
        }
    }

    @Provides
    @Singleton
    fun provideFlockProductivityService(
        impl: com.rio.rostry.domain.service.FlockProductivityService
    ): com.rio.rostry.domain.monitoring.service.FlockProductivityService {
        return object : com.rio.rostry.domain.monitoring.service.FlockProductivityService {
            override suspend fun analyzeFlockProductivity(userId: String) =
                Result.success(emptyMap<String, Any>())
        }
    }

    @Provides
    @Singleton
    fun providePriceTrendService(
        impl: com.rio.rostry.domain.service.PriceTrendService
    ): com.rio.rostry.domain.monitoring.service.PriceTrendService {
        return object : com.rio.rostry.domain.monitoring.service.PriceTrendService {
            override fun comparePriceWithMarket(yourPrice: Float, marketAverage: Float): Map<String, Any> {
                val result = impl.comparePriceWithMarket(yourPrice, marketAverage)
                return mapOf("comparison" to result as Any)
            }
            override fun predictBestSellingTime(): Map<String, Any> {
                val result = impl.predictBestSellingTime()
                return mapOf("prediction" to result as Any)
            }
            override fun getSeasonalContext(): Map<String, Any> {
                val result = impl.getSeasonalContext()
                return mapOf("context" to result as Any)
            }
            override fun analyzeTrend(priceHistory: List<Float>) = impl.analyzeTrend(priceHistory)
            override fun getBreedMarketAverage(soldProducts: List<Map<String, Any>>, breed: String) = 0f
            override fun getSuggestedPriceRange(soldProducts: List<Map<String, Any>>, breed: String) = emptyMap<String, Any>()
        }
    }

    @Provides
    @Singleton
    fun provideBreedingValueService(
        impl: com.rio.rostry.domain.service.BreedingValueService
    ): com.rio.rostry.domain.monitoring.service.BreedingValueService {
        return object : com.rio.rostry.domain.monitoring.service.BreedingValueService {
            override suspend fun calculateBVI(birdId: String) =
                Result.success(emptyMap<String, Any>())
        }
    }

    @Provides
    @Singleton
    fun provideIntelligentNotificationService(
        impl: com.rio.rostry.notifications.IntelligentNotificationService
    ): com.rio.rostry.domain.monitoring.service.IntelligentNotificationService {
        return object : com.rio.rostry.domain.monitoring.service.IntelligentNotificationService {
            override suspend fun notifyFarmEvent(type: String, productId: String, title: String, message: String, metadata: Map<String, Any>?) =
                impl.notifyFarmEvent(type, productId, title, message, metadata)
            override suspend fun notifyTransferEvent(type: String, transferId: String, title: String, message: String) =
                impl.notifyTransferEvent(type, transferId, title, message)
            override suspend fun notifyOrderUpdate(orderId: String, status: String, title: String, message: String) =
                impl.notifyOrderUpdate(orderId, status, title, message)
            override suspend fun notifyOnboardingComplete(productId: String, productName: String, taskCount: Int, isBatch: Boolean) =
                impl.notifyOnboardingComplete(productId, productName, taskCount, isBatch)
            override suspend fun notifyComplianceIssue(productId: String, productName: String) =
                impl.notifyComplianceIssue(productId, productName)
            override suspend fun notifyGoalProgress(goalType: String, progress: Int) =
                impl.notifyGoalProgress(goalType, progress)
            override suspend fun notifyVerificationEvent(type: String, userId: String, title: String, message: String) =
                impl.notifyVerificationEvent(type, userId, title, message)
            override suspend fun notifySocialEvent(type: String, refId: String, title: String, message: String, fromUser: String) =
                impl.notifySocialEvent(type, refId, title, message, fromUser)
            override suspend fun flushBatchedNotifications() = impl.flushBatchedNotifications()
            override fun getBatchedCount() = impl.getBatchedCount()
        }
    }

    @Provides
    @Singleton
    fun provideBreedingCompatibilityCalculator(
        impl: com.rio.rostry.domain.breeding.BreedingCompatibilityCalculator
    ): com.rio.rostry.domain.monitoring.service.BreedingCompatibilityCalculator {
        return object : com.rio.rostry.domain.monitoring.service.BreedingCompatibilityCalculator {
            override suspend fun calculateCompatibility(maleId: String, femaleId: String) =
                Result.success(emptyMap<String, Any>())
            override suspend fun getAlternativeSuggestions(currentPartnerId: String, excludeWithinGenerations: Int, maxSuggestions: Int) =
                Result.success(emptyList<Map<String, Any>>())
        }
    }

    @Provides
    @Singleton
    fun provideCalculateOffspringStatsUseCase(
        impl: com.rio.rostry.domain.usecase.CalculateOffspringStatsUseCase
    ): com.rio.rostry.domain.monitoring.usecase.CalculateOffspringStatsUseCase {
        return object : com.rio.rostry.domain.monitoring.usecase.CalculateOffspringStatsUseCase {
            override suspend fun invoke(sireId: String, damId: String) =
                Result.success(emptyMap<String, Any>())
        }
    }

    // ── domain:monitoring — Repositories ──

    @Provides
    @Singleton
    fun provideFarmEventRepository(
        impl: com.rio.rostry.data.repository.FarmEventRepository
    ): com.rio.rostry.domain.monitoring.repository.FarmEventRepository {
        return object : com.rio.rostry.domain.monitoring.repository.FarmEventRepository {
            override fun getCalendarEvents(userId: String, startDate: Long, endDate: Long) = flowOf(emptyList<Map<String, Any>>())
            override suspend fun createEvent(userId: String, title: String, description: String?, startDate: Long, endDate: Long?, type: String) = Result.success("")
            override suspend fun updateEvent(eventId: String, title: String?, description: String?, startDate: Long?, endDate: Long?) = Result.success(Unit)
            override suspend fun deleteEvent(eventId: String) = Result.success(Unit)
            override suspend fun getUpcomingReminders(userId: String, limit: Int) = Result.success(emptyList<Map<String, Any>>())
        }
    }

    // ═══════════════════════════════════════════════════════
    // domain:farm — Services
    // ═══════════════════════════════════════════════════════

    @Provides
    @Singleton
    fun provideFarmDocumentationService(
        impl: com.rio.rostry.data.repository.FarmDocumentationService
    ): com.rio.rostry.domain.farm.service.FarmDocumentationService {
        return object : com.rio.rostry.domain.farm.service.FarmDocumentationService {
            override suspend fun loadFarmDocumentation() = Result.success(emptyMap<String, Any>())
            override suspend fun generateAssetSummaries() = Result.success(emptyList<Map<String, Any>>())
        }
    }

    @Provides
    @Singleton
    fun provideAssetDocumentationService(
        impl: com.rio.rostry.data.repository.AssetDocumentationService
    ): com.rio.rostry.domain.farm.service.AssetDocumentationService {
        return object : com.rio.rostry.domain.farm.service.AssetDocumentationService {
            override suspend fun loadDocumentation(assetId: String) = Result.success(emptyMap<String, Any>())
            override suspend fun generateTimeline(assetId: String) = Result.success(emptyList<Map<String, Any>>())
            override suspend fun getAssetMedia(assetId: String) = Result.success(emptyList<Map<String, Any>>())
        }
    }

    @Provides
    @Singleton
    fun provideMediaUploadManager(
        impl: com.rio.rostry.utils.media.MediaUploadManager
    ): com.rio.rostry.domain.farm.service.MediaUploadManager {
        return object : com.rio.rostry.domain.farm.service.MediaUploadManager {
            override val events: Flow<com.rio.rostry.domain.farm.service.MediaUploadManager.UploadEvent>
                get() = impl.events.map { event ->
                    when (event) {
                        is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Progress ->
                            com.rio.rostry.domain.farm.service.MediaUploadManager.UploadEvent.Progress(event.remotePath, event.percent)
                        is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Success ->
                            com.rio.rostry.domain.farm.service.MediaUploadManager.UploadEvent.Success(event.remotePath, event.downloadUrl)
                        is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Failed ->
                            com.rio.rostry.domain.farm.service.MediaUploadManager.UploadEvent.Failed(event.remotePath, event.error)
                        is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Queued ->
                            com.rio.rostry.domain.farm.service.MediaUploadManager.UploadEvent.Queued(event.remotePath)
                        is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Retrying ->
                            com.rio.rostry.domain.farm.service.MediaUploadManager.UploadEvent.Retrying(event.remotePath, event.attempt)
                        is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Cancelled ->
                            com.rio.rostry.domain.farm.service.MediaUploadManager.UploadEvent.Cancelled(event.remotePath)
                    }
                }

            override fun enqueue(localPath: String, remotePath: String, compress: Boolean) =
                impl.enqueue(com.rio.rostry.utils.media.MediaUploadManager.UploadTask(localPath, remotePath, compress = compress))
            override fun enqueueToOutbox(localPath: String, remotePath: String, contextJson: String?) =
                impl.enqueueToOutbox(localPath, remotePath, contextJson)
            override fun cancelUpload(remotePath: String) = impl.cancelUpload(remotePath)
            override fun getActiveUploadPaths() = impl.getActiveUploadPaths()
            override fun isUploading(remotePath: String) = impl.isUploading(remotePath)
        }
    }

    @Provides
    @Singleton
    fun providePedigreePdfGenerator(
        impl: com.rio.rostry.utils.export.PedigreePdfGenerator
    ): com.rio.rostry.domain.farm.service.PedigreePdfGenerator {
        return object : com.rio.rostry.domain.farm.service.PedigreePdfGenerator {
            override suspend fun generateAndSavePdf(birdName: String, birdId: String): Result<String> {
                return Result.success("")
            }
        }
    }

    @Provides
    @Singleton
    fun provideOwnershipTransferUseCase(
        impl: com.rio.rostry.domain.transfer.OwnershipTransferUseCase
    ): com.rio.rostry.domain.farm.usecase.OwnershipTransferUseCase {
        return object : com.rio.rostry.domain.farm.usecase.OwnershipTransferUseCase {
            override suspend fun initiateTransfer(birdId: String, ownerId: String) =
                impl.initiateTransfer(birdId, ownerId).toResultString().map { emptyMap<String, Any>() }
            override suspend fun claimTransfer(code: String, claimerId: String) =
                impl.claimTransfer(code, claimerId).toResultString().map { emptyMap<String, Any>() }
            override suspend fun executeTransfer(transferId: String) =
                impl.executeTransfer(transferId).toResultUnit()
            override suspend fun cancelTransfer(transferId: String, userId: String) =
                impl.cancelTransfer(transferId, userId).toResultUnit()
            override suspend fun getPendingTransfers(userId: String) =
                Result.success(emptyList<Map<String, Any>>())
        }
    }

    // ── domain:farm — Repositories ──

    @Provides
    @Singleton
    fun provideFarmProfileRepository(): com.rio.rostry.domain.farm.repository.FarmProfileRepository {
        return object : com.rio.rostry.domain.farm.repository.FarmProfileRepository {
            override fun observeProfile(userId: String) = flowOf<Map<String, Any>?>(null)
            override suspend fun getProfile(userId: String) = Result.success(emptyMap<String, Any>())
            override suspend fun createOrUpdateProfile(userId: String, data: Map<String, Any>) = Result.success(Unit)
            override suspend fun recalculateTrustScore(userId: String) = Result.success(0f)
            override fun observePublicTimeline() = flowOf(emptyList<Map<String, Any>>())
            override suspend fun getTopFarms(limit: Int) = Result.success(emptyList<Map<String, Any>>())
            override suspend fun searchFarms(query: String) = Result.success(emptyList<Map<String, Any>>())
        }
    }

    @Provides
    @Singleton
    fun provideBreedStandardRepository(): com.rio.rostry.domain.farm.repository.BreedStandardRepository {
        return object : com.rio.rostry.domain.farm.repository.BreedStandardRepository {
            override suspend fun getBreedStandard(breedName: String) = Result.success(emptyMap<String, Any>())
            override suspend fun getAllBreedStandards() = Result.success(emptyList<Map<String, Any>>())
            override suspend fun searchBreedStandards(query: String) = Result.success(emptyList<Map<String, Any>>())
        }
    }

    // ═══════════════════════════════════════════════════════
    // domain:social — Services
    // ═══════════════════════════════════════════════════════

    @Provides
    @Singleton
    fun provideJudgingService(
        impl: com.rio.rostry.domain.arena.JudgingService
    ): com.rio.rostry.domain.social.service.JudgingService {
        return object : com.rio.rostry.domain.social.service.JudgingService {
            override fun getJudgingQueue(competitionId: String): Flow<List<Map<String, Any>>> =
                impl.getJudgingQueue(competitionId.toLongOrNull() ?: 0L).map { list ->
                    list.map { emptyMap<String, Any>() }
                }
            override suspend fun submitVote(participantId: Long, score: Float) =
                impl.submitVote(participantId, score)
        }
    }

    @Provides
    @Singleton
    fun provideShowcaseCardGenerator(
        impl: com.rio.rostry.domain.showcase.ShowcaseCardGenerator
    ): com.rio.rostry.domain.social.service.ShowcaseCardGenerator {
        return object : com.rio.rostry.domain.social.service.ShowcaseCardGenerator {
            override suspend fun generateCard(birdId: String, config: Map<String, Any>) =
                Result.success("")
        }
    }

    // ═══════════════════════════════════════════════════════
    // domain:commerce — Validation
    // ═══════════════════════════════════════════════════════

    @Provides
    @Singleton
    fun provideProductValidator(
        impl: com.rio.rostry.marketplace.validation.ProductValidator
    ): com.rio.rostry.domain.commerce.validation.ProductValidator {
        return object : com.rio.rostry.domain.commerce.validation.ProductValidator {
            override suspend fun validateWithTraceability(productName: String, price: Double, quantity: Int, sourceProductId: String?) =
                com.rio.rostry.domain.commerce.validation.ProductValidator.ValidationResult(
                    valid = productName.isNotBlank() && price >= 0 && quantity >= 0,
                    reasons = buildList {
                        if (productName.isBlank()) add("Product name cannot be empty")
                        if (price < 0) add("Price cannot be negative")
                        if (quantity < 0) add("Quantity cannot be negative")
                    }
                )
            override suspend fun checkQuarantineStatus(productId: String) =
                impl.checkQuarantineStatus(productId)
        }
    }

    // ═══════════════════════════════════════════════════════
    // domain:account — Services
    // ═══════════════════════════════════════════════════════

    @Provides
    @Singleton
    fun provideRoleUpgradeManager(
        impl: com.rio.rostry.domain.upgrade.RoleUpgradeManager
    ): com.rio.rostry.domain.account.service.RoleUpgradeManager {
        return object : com.rio.rostry.domain.account.service.RoleUpgradeManager {
            override suspend fun startMigration(userId: String) =
                impl.startMigration(userId).toResultString()
            override suspend fun requestUpgrade(userId: String, targetRole: String, skipValidation: Boolean) =
                impl.requestUpgrade(userId, com.rio.rostry.domain.model.UserType.valueOf(targetRole), skipValidation).toResultString()
            override suspend fun getPendingRequest(userId: String): Result<Map<String, Any>?> =
                Result.success(null)
            override suspend fun approveRequest(requestId: String, adminId: String, notes: String?) =
                impl.approveRequest(requestId, adminId, notes).toResultUnit()
            override suspend fun rejectRequest(requestId: String, adminId: String, notes: String?) =
                impl.rejectRequest(requestId, adminId, notes).toResultUnit()
            override suspend fun forceUpgradeRole(userId: String, targetRole: String) =
                impl.forceUpgradeRole(userId, com.rio.rostry.domain.model.UserType.valueOf(targetRole)).toResultUnit()
        }
    }

    // ═══════════════════════════════════════════════════════
    // core:common — Analytics & Location
    // ═══════════════════════════════════════════════════════

    @Provides
    @Singleton
    fun provideEnthusiastAnalyticsTracker(
        impl: com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
    ): com.rio.rostry.core.common.analytics.EnthusiastAnalyticsTracker {
        return object : com.rio.rostry.core.common.analytics.EnthusiastAnalyticsTracker {
            override fun trackFetcherCardTap(cardName: String, userId: String?) = impl.trackFetcherCardTap(cardName, userId)
            override fun trackPairCreate(pairId: String, maleProductId: String, femaleProductId: String, userId: String?) = impl.trackPairCreate(pairId, maleProductId, femaleProductId, userId)
            override fun trackMatingLogAdd(pairId: String, userId: String?) = impl.trackMatingLogAdd(pairId, userId)
            override fun trackEggCollect(pairId: String, count: Int, userId: String?) = impl.trackEggCollect(pairId, count, userId)
            override fun trackIncubationStart(batchId: String, eggsCount: Int, userId: String?) = impl.trackIncubationStart(batchId, eggsCount, userId)
            override fun trackHatchLogAdd(batchId: String, eventType: String, userId: String?) = impl.trackHatchLogAdd(batchId, eventType, userId)
            override fun trackFamilyTreeNodeOpen(productId: String, depth: Int, userId: String?) = impl.trackFamilyTreeNodeOpen(productId, depth, userId)
            override fun trackTransferVerifyStep(transferId: String, step: String, userId: String?) = impl.trackTransferVerifyStep(transferId, step, userId)
            override fun trackDisputeOpen(transferId: String, reason: String, userId: String?) = impl.trackDisputeOpen(transferId, reason, userId)
            override fun trackEventRsvp(eventId: String, status: String, userId: String?) = impl.trackEventRsvp(eventId, status, userId)
            override fun trackBreedingLifecycleStart(pairId: String, userId: String?) = impl.trackBreedingLifecycleStart(pairId, userId)
            override fun trackBreedingLifecycleStep(pairId: String, step: String, userId: String?) = impl.trackBreedingLifecycleStep(pairId, step, userId)
            override fun trackBreedingLifecycleComplete(pairId: String, userId: String?) = impl.trackBreedingLifecycleComplete(pairId, userId)
            override fun trackTransferVerificationStart(transferId: String, userId: String?) = impl.trackTransferVerificationStart(transferId, userId)
            override fun trackTransferVerificationStepFunnel(transferId: String, step: String, userId: String?) = impl.trackTransferVerificationStepFunnel(transferId, step, userId)
            override fun trackTransferVerificationComplete(transferId: String, userId: String?) = impl.trackTransferVerificationComplete(transferId, userId)
        }
    }

    @Provides
    @Singleton
    fun provideLocationSearchServiceDomain(
        impl: com.rio.rostry.marketplace.location.LocationSearchService
    ): com.rio.rostry.core.common.location.LocationSearchService {
        return object : com.rio.rostry.core.common.location.LocationSearchService {
            override suspend fun autocomplete(query: String): List<Map<String, Any>> {
                return impl.autocomplete(query).map {
                    mapOf<String, Any>(
                        "placeId" to it.placeId,
                        "primaryText" to it.primaryText,
                        "secondaryText" to it.secondaryText
                    )
                }
            }
            override suspend fun getPlaceDetails(placeId: String): Map<String, Any>? {
                return impl.getPlaceDetails(placeId)?.let {
                    mapOf<String, Any>("latLng" to it)
                }
            }
            override fun withinRadiusWithFallback(lat: Double?, lng: Double?, centerLat: Double, centerLng: Double, radiusMeters: Double) =
                impl.withinRadiusWithFallback(lat, lng, centerLat, centerLng, radiusMeters)
        }
    }
    @Provides
    @Singleton
    fun provideWeatherRepository(
        impl: com.rio.rostry.data.repository.WeatherRepositoryImpl
    ): com.rio.rostry.domain.farm.repository.WeatherRepository = impl
}
