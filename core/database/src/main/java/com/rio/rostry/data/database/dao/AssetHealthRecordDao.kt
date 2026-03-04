package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.AssetHealthRecordEntity

@Dao
interface AssetHealthRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AssetHealthRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AssetHealthRecordEntity>)

    @Query("SELECT * FROM asset_health_records WHERE assetId = :assetId ORDER BY createdAt DESC")
    suspend fun getRecordsForAsset(assetId: String): List<AssetHealthRecordEntity>

    @Query("SELECT * FROM asset_health_records WHERE recordId = :recordId")
    suspend fun getRecordById(recordId: String): AssetHealthRecordEntity?
}
