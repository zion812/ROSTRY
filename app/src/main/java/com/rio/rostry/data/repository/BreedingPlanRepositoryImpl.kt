package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.BreedingPlanDao
import com.rio.rostry.data.database.entity.BreedingPlanEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.session.UserSessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedingPlanRepositoryImpl @Inject constructor(
    private val dao: BreedingPlanDao,
    private val sessionManager: UserSessionManager
) : BreedingPlanRepository {

    override fun getAllPlans(): Flow<Resource<List<BreedingPlanEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val user = sessionManager.currentUser.first()
            if (user != null) {
                dao.getAllPlans(user.uid).collect { plans ->
                    emit(Resource.Success(plans))
                }
            } else {
                emit(Resource.Error("User not logged in"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun savePlan(plan: BreedingPlanEntity): Resource<Unit> {
        return try {
            dao.insertPlan(plan)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save plan")
        }
    }

    override suspend fun deletePlan(plan: BreedingPlanEntity): Resource<Unit> {
        return try {
            dao.deletePlan(plan)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete plan")
        }
    }
}
