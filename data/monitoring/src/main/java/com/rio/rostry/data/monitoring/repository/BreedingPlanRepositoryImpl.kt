package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.BreedingPlan
import com.rio.rostry.core.model.Result
import com.rio.rostry.data.database.dao.BreedingPlanDao
import com.rio.rostry.data.monitoring.mapper.BreedingPlanMapper
import com.rio.rostry.domain.monitoring.repository.BreedingPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lightweight implementation of BreedingPlanRepository using Room.
 */
@Singleton
class BreedingPlanRepositoryImpl @Inject constructor(
    private val dao: BreedingPlanDao
) : BreedingPlanRepository {

    override fun getAllPlans(): Flow<Result<List<BreedingPlan>>> = flow {
        try {
            // Module-safe fallback: use a default user scope until session abstraction is moved to domain.
            dao.getAllPlans("").collect { plans ->
                emit(Result.Success(plans.map { BreedingPlanMapper.toDomain(it) }))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun savePlan(plan: BreedingPlan): Result<Unit> {
        return try {
            dao.insertPlan(BreedingPlanMapper.toEntity(plan))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deletePlan(plan: BreedingPlan): Result<Unit> {
        return try {
            dao.deletePlan(BreedingPlanMapper.toEntity(plan))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
