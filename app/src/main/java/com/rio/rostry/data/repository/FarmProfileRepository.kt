package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FarmProfileDao
import com.rio.rostry.data.database.dao.FarmTimelineEventDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.FarmProfileEntity
import com.rio.rostry.data.database.entity.FarmTimelineEventEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing public-facing farm profiles and timeline events.
 * Part of the "Glass Box" system - making farm operations transparent.
 */
@Singleton
class FarmProfileRepository @Inject constructor(
    private val farmProfileDao: FarmProfileDao,
    private val timelineEventDao: FarmTimelineEventDao,
    private val orderDao: OrderDao,
    private val vaccinationDao: VaccinationRecordDao
) {
    
    // ========================================
    // Profile Operations
    // ========================================
    
    fun observeProfile(farmerId: String): Flow<FarmProfileEntity?> =
        farmProfileDao.observeProfile(farmerId)
    
    suspend fun getProfile(farmerId: String): FarmProfileEntity? =
        farmProfileDao.findById(farmerId)
    
    suspend fun createOrUpdateProfile(profile: FarmProfileEntity): Resource<Unit> {
        return try {
            farmProfileDao.upsert(profile.copy(updatedAt = System.currentTimeMillis(), dirty = true))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save profile")
        }
    }
    
    suspend fun updateFarmInfo(
        farmerId: String,
        farmName: String,
        farmBio: String?,
        whatsappNumber: String?
    ): Resource<Unit> {
        return try {
            val existing = farmProfileDao.findById(farmerId)
            val profile = existing?.copy(
                farmName = farmName,
                farmBio = farmBio,
                whatsappNumber = whatsappNumber,
                updatedAt = System.currentTimeMillis(),
                dirty = true
            ) ?: FarmProfileEntity(
                farmerId = farmerId,
                farmName = farmName,
                farmBio = farmBio,
                whatsappNumber = whatsappNumber,
                memberSince = System.currentTimeMillis()
            )
            farmProfileDao.upsert(profile)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }
    
    // ========================================
    // Trust Score Calculation
    // ========================================
    
    suspend fun recalculateTrustScore(farmerId: String): Int {
        val now = System.currentTimeMillis()
        val profile = farmProfileDao.findById(farmerId) ?: return 0
        
        var score = 0
        
        // Vaccination Rate (30 points max)
        val vaccRate = profile.vaccinationRate ?: 0
        score += (vaccRate * 0.3).toInt()
        
        // Response Time (20 points max) - placeholder, would need message tracking
        score += when (profile.avgResponseTimeMinutes) {
            null -> 10 // No data, give middle score
            in 0..30 -> 20
            in 31..60 -> 15
            in 61..120 -> 10
            else -> 5
        }
        
        // Order Completion Rate (25 points max)
        val completedOrders = profile.totalOrdersCompleted
        score += minOf((completedOrders * 2.5).toInt(), 25)
        
        // Account Age (10 points max)
        val monthsActive = ((now - profile.memberSince) / (30L * 24 * 60 * 60 * 1000)).toInt()
        score += minOf(monthsActive, 10)
        
        // Timeline Activity (10 points max)
        val eventCount = timelineEventDao.countByType(farmerId, FarmTimelineEventEntity.TYPE_VACCINATION) +
                         timelineEventDao.countByType(farmerId, FarmTimelineEventEntity.TYPE_SANITATION)
        score += minOf(eventCount, 10)
        
        // Verification Bonus (5 points)
        if (profile.isVerified) score += 5
        
        val finalScore = minOf(score, 100)
        farmProfileDao.updateTrustScore(farmerId, finalScore, now)
        
        return finalScore
    }
    
    // ========================================
    // Timeline Operations
    // ========================================
    
    fun observePublicTimeline(farmerId: String, limit: Int = 50): Flow<List<FarmTimelineEventEntity>> =
        timelineEventDao.observePublicTimeline(farmerId, limit)
    
    fun observeFullTimeline(farmerId: String, limit: Int = 100): Flow<List<FarmTimelineEventEntity>> =
        timelineEventDao.observeFullTimeline(farmerId, limit)
    
    fun observeRecentEvents(farmerId: String): Flow<List<FarmTimelineEventEntity>> =
        timelineEventDao.getRecentEvents(farmerId)
    
    suspend fun createTimelineEvent(
        farmerId: String,
        eventType: String,
        title: String,
        description: String,
        iconType: String? = null,
        imageUrl: String? = null,
        sourceType: String? = null,
        sourceId: String? = null,
        trustPoints: Int = 0,
        isPublic: Boolean = true,
        isMilestone: Boolean = false
    ): Resource<String> {
        return try {
            // Prevent duplicates from same source
            if (sourceType != null && sourceId != null) {
                if (timelineEventDao.existsForSource(sourceType, sourceId)) {
                    return Resource.Success(sourceId) // Already exists
                }
            }
            
            val eventId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            
            val event = FarmTimelineEventEntity(
                eventId = eventId,
                farmerId = farmerId,
                eventType = eventType,
                title = title,
                description = description,
                iconType = iconType,
                imageUrl = imageUrl,
                sourceType = sourceType,
                sourceId = sourceId,
                trustPointsEarned = trustPoints,
                isPublic = isPublic,
                isMilestone = isMilestone,
                eventDate = now,
                createdAt = now
            )
            
            timelineEventDao.insert(event)
            
            // Update trust score if points earned
            if (trustPoints > 0) {
                recalculateTrustScore(farmerId)
            }
            
            Resource.Success(eventId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create event")
        }
    }
    
    suspend fun setEventVisibility(eventId: String, isPublic: Boolean): Resource<Unit> {
        return try {
            timelineEventDao.setEventVisibility(eventId, isPublic, System.currentTimeMillis())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update visibility")
        }
    }
    
    // ========================================
    // Privacy Settings
    // ========================================
    
    suspend fun updatePrivacySettings(
        farmerId: String,
        isPublic: Boolean,
        showTimeline: Boolean
    ): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            farmProfileDao.setProfileVisibility(farmerId, isPublic, now)
            farmProfileDao.setTimelineVisibility(farmerId, showTimeline, now)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update privacy")
        }
    }
    
    // ========================================
    // Discovery
    // ========================================
    
    fun getTopFarms(limit: Int = 20): Flow<List<FarmProfileEntity>> =
        farmProfileDao.getTopFarms(limit)
    
    fun getFarmsByProvince(province: String, limit: Int = 50): Flow<List<FarmProfileEntity>> =
        farmProfileDao.getFarmsByProvince(province, limit)
    
    suspend fun searchFarms(query: String, limit: Int = 20): List<FarmProfileEntity> =
        farmProfileDao.searchFarms(query, limit)
}
