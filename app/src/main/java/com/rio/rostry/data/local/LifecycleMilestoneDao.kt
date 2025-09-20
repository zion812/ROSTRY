package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.LifecycleMilestone
import kotlinx.coroutines.flow.Flow

@Dao
interface LifecycleMilestoneDao {
    @Query("SELECT * FROM lifecycle_milestones WHERE poultryId = :poultryId ORDER BY weekNumber ASC")
    fun getMilestonesByPoultryId(poultryId: String): Flow<List<LifecycleMilestone>>

    @Query("SELECT * FROM lifecycle_milestones WHERE poultryId = :poultryId AND isCompleted = 0 ORDER BY weekNumber ASC")
    fun getPendingMilestonesByPoultryId(poultryId: String): Flow<List<LifecycleMilestone>>

    @Query("SELECT * FROM lifecycle_milestones WHERE isCompleted = 0 AND alertDate <= :currentDate AND isAlertSent = 0")
    fun getDueMilestoneAlerts(currentDate: Long): Flow<List<LifecycleMilestone>>

    @Query("SELECT * FROM lifecycle_milestones WHERE id = :id")
    suspend fun getMilestoneById(id: String): LifecycleMilestone?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: LifecycleMilestone)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<LifecycleMilestone>)

    @Update
    suspend fun updateMilestone(milestone: LifecycleMilestone)

    @Delete
    suspend fun deleteMilestone(milestone: LifecycleMilestone)
}