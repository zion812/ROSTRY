package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.LikesDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.UserInterestDao
import com.rio.rostry.data.database.entity.UserInterestEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker for user personalization.
 * 
 * Analyzes user behavior and engagement patterns to update interest profiles:
 * - Extracts breed preferences from owned/liked products
 * - Infers price range preferences from purchase/browsing history
 * - Updates user interest weights based on recent activity
 * 
 * Runs every 6 hours when network is connected.
 */
@HiltWorker
class PersonalizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val currentUserProvider: CurrentUserProvider,
    private val sessionManager: SessionManager,
    private val productDao: ProductDao,
    private val likesDao: LikesDao,
    private val userInterestDao: UserInterestDao
) : CoroutineWorker(appContext, params) {
    
    override suspend fun doWork(): Result {
        val userId = currentUserProvider.userIdOrNull()
            ?: return Result.success() // No user logged in
        
        Timber.d("PersonalizationWorker: Starting personalization for user $userId")
        
        return try {
            val now = System.currentTimeMillis()
            
            // 1. Extract breed preferences from owned products
            val breedInterests = extractBreedPreferences(userId)
            
            // 2. Extract category preferences
            val categoryInterests = extractCategoryPreferences(userId)
            
            // 3. Calculate engagement patterns
            val engagementScore = calculateEngagementScore(userId)
            
            // 4. Update user interests in database
            updateUserInterests(userId, breedInterests, categoryInterests, now)
            
            Timber.d("PersonalizationWorker: Completed for $userId. " +
                "Breeds: ${breedInterests.size}, Categories: ${categoryInterests.size}, " +
                "Engagement: $engagementScore")
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "PersonalizationWorker: Failed for $userId")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
    
    /**
     * Extract breed preferences from user's owned products.
     * Breeds with more products get higher weight.
     */
    private suspend fun extractBreedPreferences(userId: String): Map<String, Double> {
        val products = productDao.getProductsBySeller(userId).first()
        
        if (products.isEmpty()) {
            return emptyMap()
        }
        
        // Count products per breed
        val breedCounts = products
            .filter { !it.breed.isNullOrBlank() }
            .groupBy { it.breed!! }
            .mapValues { it.value.size }
        
        val maxCount = breedCounts.values.maxOrNull()?.toDouble() ?: 1.0
        
        // Normalize to 0.0-1.0 weight
        return breedCounts.mapValues { (_, count) ->
            (count.toDouble() / maxCount).coerceIn(0.1, 1.0)
        }
    }
    
    /**
     * Extract category preferences from user's products.
     * Categories are things like: CHICKEN, EGG, CHICK, etc.
     */
    private suspend fun extractCategoryPreferences(userId: String): Map<String, Double> {
        val products = productDao.getProductsBySeller(userId).first()
        
        if (products.isEmpty()) {
            return emptyMap()
        }
        
        // Count products per category
        val categoryCounts = products
            .filter { !it.category.isNullOrBlank() }
            .groupBy { it.category!! }
            .mapValues { it.value.size }
        
        val maxCount = categoryCounts.values.maxOrNull()?.toDouble() ?: 1.0
        
        // Normalize to 0.0-1.0 weight
        return categoryCounts.mapValues { (_, count) ->
            (count.toDouble() / maxCount).coerceIn(0.1, 1.0)
        }
    }
    
    /**
     * Calculate overall engagement score based on likes given.
     * Higher engagement = more active user.
     */
    private suspend fun calculateEngagementScore(userId: String): Double {
        val likesGiven = likesDao.countByUser(userId)
        
        // Normalize: 0 likes = 0.0, 100+ likes = 1.0
        return (likesGiven / 100.0).coerceIn(0.0, 1.0)
    }
    
    /**
     * Update user interests in the database.
     * Uses upsert to avoid duplicates.
     */
    private suspend fun updateUserInterests(
        userId: String,
        breedInterests: Map<String, Double>,
        categoryInterests: Map<String, Double>,
        timestamp: Long
    ) {
        // Upsert breed interests
        breedInterests.forEach { (breed, weight) ->
            userInterestDao.upsert(
                UserInterestEntity(
                    interestId = "$userId:BREED:$breed",
                    userId = userId,
                    category = "BREED",
                    value = breed,
                    weight = weight,
                    updatedAt = timestamp
                )
            )
        }
        
        // Upsert category interests
        categoryInterests.forEach { (category, weight) ->
            userInterestDao.upsert(
                UserInterestEntity(
                    interestId = "$userId:CATEGORY:$category",
                    userId = userId,
                    category = "CATEGORY",
                    value = category,
                    weight = weight,
                    updatedAt = timestamp
                )
            )
        }
    }

    companion object {
        private const val WORK_NAME = "personalization_refresh"
        private const val REPEAT_INTERVAL_HOURS = 6L
        
        /**
         * Schedule the personalization worker to run every 6 hours.
         * Requires network connection for potential future cloud sync.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val request = PeriodicWorkRequestBuilder<PersonalizationWorker>(
                REPEAT_INTERVAL_HOURS, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .addTag("personalization_worker")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
