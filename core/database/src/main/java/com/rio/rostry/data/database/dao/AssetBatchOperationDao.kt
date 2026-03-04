package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.AssetBatchOperationEntity

@Dao
interface AssetBatchOperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: AssetBatchOperationEntity)

    @Update
    suspend fun update(operation: AssetBatchOperationEntity)

    @Query("SELECT * FROM asset_batch_operations WHERE operationId = :operationId")
    suspend fun getOperationById(operationId: String): AssetBatchOperationEntity?

    @Query("SELECT * FROM asset_batch_operations WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    suspend fun getOperationsByFarmer(farmerId: String): List<AssetBatchOperationEntity>
}
