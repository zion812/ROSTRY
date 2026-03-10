package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.BreedingPlan
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for breeding plan management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface BreedingPlanRepository {
    /**
     * Get all breeding plans for the current user.
     */
    fun getAllPlans(): Flow<Result<List<BreedingPlan>>>
    
    /**
     * Save a breeding plan.
     */
    suspend fun savePlan(plan: BreedingPlan): Result<Unit>
    
    /**
     * Delete a breeding plan.
     */
    suspend fun deletePlan(plan: BreedingPlan): Result<Unit>
}

