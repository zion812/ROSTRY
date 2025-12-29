package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.monitoring.HatchingRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import com.rio.rostry.utils.network.FeatureToggles
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

@HiltWorker
class PrefetchWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val imageLoader: ImageLoader,
    private val toggles: FeatureToggles,
    private val sessionManager: SessionManager,
    private val productRepository: ProductRepository,
    private val transferRepository: TransferRepository,
    private val userRepository: UserRepository,
    private val vaccinationRepository: VaccinationRepository,
    private val hatchingRepository: HatchingRepository,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            if (!toggles.prefetchAllowed()) return Result.success()

            val now = System.currentTimeMillis()
            val role = sessionManager.sessionRole().first()
            if (role == null) return Result.success()

            val lastAuthAt = sessionManager.lastAuthAt().first()
            if (lastAuthAt == null || (now - lastAuthAt) > 7 * 24 * 60 * 60 * 1000L) return Result.success()

            val userRes = userRepository.getCurrentUser().first()
            val user = when (userRes) {
                is Resource.Success -> userRes.data ?: return Result.success()
                is Resource.Error -> return Result.success()
                is Resource.Loading -> return Result.success()
            }
            val userId = user.userId

            flowAnalyticsTracker.trackEvent("prefetch_started", mapOf("role" to role.name))
            var prefetchedCount = 0

            when (role) {
                UserType.FARMER -> {
                    // Prefetch seller's products (marketplace drafts/pending tasks)
                    val products = productRepository.getProductsBySeller(userId).first().data ?: emptyList()
                    prefetchedCount += products.size.coerceAtMost(50)
                    products.take(50).forEach { product ->
                        product.imageUrls.forEach { url ->
                            val req = ImageRequest.Builder(applicationContext).data(url).build()
                            imageLoader.enqueue(req)
                        }
                    }

                    // Active transfers
                    val transfers = transferRepository.observeRecentActivity(userId).first()
                    prefetchedCount += transfers.size

                    // Today's monitoring schedules (vaccinations and hatching)
                    val vaccinations = vaccinationRepository.observeByFarmer(userId).first()
                    prefetchedCount += vaccinations.size
                    val batches = hatchingRepository.observeBatches().first()
                    prefetchedCount += batches.size
                }
                UserType.ENTHUSIAST -> {
                    // Breeding pairs (hatching batches)
                    val batches = hatchingRepository.observeBatches().first()
                    prefetchedCount += batches.size

                    // Favorite bloodlines/saved searches (simplified to recent products by category)
                    val categoryProducts = productRepository.getProductsByCategory("breeding").first().data ?: emptyList()
                    prefetchedCount += categoryProducts.size.coerceAtMost(50)
                    categoryProducts.take(50).forEach { product ->
                        product.imageUrls.forEach { url ->
                            val req = ImageRequest.Builder(applicationContext).data(url).build()
                            imageLoader.enqueue(req)
                        }
                    }
                }
                UserType.GENERAL -> {
                    // Nearby listings (using default location as placeholder)
                    val products = productRepository.filterNearby(0.0, 0.0, 100.0, limit = 50).data ?: emptyList()
                    prefetchedCount += products.size
                    products.forEach { product ->
                        product.imageUrls.forEach { url ->
                            val req = ImageRequest.Builder(applicationContext).data(url).build()
                            imageLoader.enqueue(req)
                        }
                    }

                    // Recommended products (simplified to additional nearby)
                    val recommendedProducts = productRepository.filterNearby(0.0, 0.0, 200.0, limit = 50).data ?: emptyList()
                    prefetchedCount += recommendedProducts.size.coerceAtMost(50)
                    recommendedProducts.take(50).forEach { product ->
                        product.imageUrls.forEach { url ->
                            val req = ImageRequest.Builder(applicationContext).data(url).build()
                            imageLoader.enqueue(req)
                        }
                    }
                }
                UserType.ADMIN -> {
                    // Admin might not need prefetch specifically, or can use GENERAL
                }
            }

            flowAnalyticsTracker.trackEvent("prefetch_completed", mapOf("role" to role.name, "count" to prefetchedCount))
            Result.success()
        } catch (e: Exception) {
            val role = sessionManager.sessionRole().first()?.name ?: "unknown"
            flowAnalyticsTracker.trackEvent("prefetch_failed", mapOf("role" to role, "error" to (e.message ?: "unknown")))
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "PrefetchWorkerDaily"
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .build()
            val req = PeriodicWorkRequestBuilder<PrefetchWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }
    }
}
