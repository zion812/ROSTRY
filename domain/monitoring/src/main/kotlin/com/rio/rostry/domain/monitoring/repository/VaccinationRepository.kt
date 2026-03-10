package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.VaccinationRecord
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for vaccination record management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface VaccinationRepository {
    /**
     * Observe vaccination records for a specific product.
     */
    fun observe(productId: String): Flow<List<VaccinationRecord>>
    
    /**
     * Observe vaccination records by farmer.
     */
    fun observeByFarmer(farmerId: String): Flow<List<VaccinationRecord>>
    
    /**
     * Insert or update a vaccination record.
     */
    suspend fun upsert(record: VaccinationRecord)
    
    /**
     * Get vaccination records with due reminders before the specified time.
     */
    suspend fun dueReminders(byTime: Long): List<VaccinationRecord>
}
