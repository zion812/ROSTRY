package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.BreedingPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingPlanDao {
    @Query("SELECT * FROM breeding_plans WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getAllPlans(farmerId: String): Flow<List<BreedingPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: BreedingPlanEntity)

    @Delete
    suspend fun deletePlan(plan: BreedingPlanEntity)
    
    @Query("SELECT * FROM breeding_plans WHERE planId = :planId")
    suspend fun getPlanById(planId: String): BreedingPlanEntity?
}
