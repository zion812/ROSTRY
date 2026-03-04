package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.HubAssignmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for hub assignment operations.
 */
@Dao
interface HubAssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hubAssignment: HubAssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hubAssignments: List<HubAssignmentEntity>)

    @Update
    suspend fun update(hubAssignment: HubAssignmentEntity)

    @Query("SELECT * FROM hub_assignments WHERE product_id = :productId")
    suspend fun getByProductId(productId: String): HubAssignmentEntity?

    @Query("SELECT * FROM hub_assignments WHERE product_id = :productId")
    fun observeByProductId(productId: String): Flow<HubAssignmentEntity?>

    @Query("SELECT * FROM hub_assignments WHERE hub_id = :hubId")
    suspend fun getByHubId(hubId: String): List<HubAssignmentEntity>

    @Query("SELECT * FROM hub_assignments WHERE hub_id = :hubId")
    fun observeByHubId(hubId: String): Flow<List<HubAssignmentEntity>>

    @Query("SELECT COUNT(*) FROM hub_assignments WHERE hub_id = :hubId")
    suspend fun getHubLoadCount(hubId: String): Int

    @Query("DELETE FROM hub_assignments WHERE product_id = :productId")
    suspend fun deleteByProductId(productId: String)

    @Query("DELETE FROM hub_assignments")
    suspend fun deleteAll()
}
