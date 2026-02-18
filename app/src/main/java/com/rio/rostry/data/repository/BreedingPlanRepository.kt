package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.BreedingPlanEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BreedingPlanRepository {
    fun getAllPlans(): Flow<Resource<List<BreedingPlanEntity>>>
    suspend fun savePlan(plan: BreedingPlanEntity): Resource<Unit>
    suspend fun deletePlan(plan: BreedingPlanEntity): Resource<Unit>
}
