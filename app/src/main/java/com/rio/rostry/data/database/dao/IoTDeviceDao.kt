package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.IoTDeviceEntity

@Dao
interface IoTDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(device: IoTDeviceEntity)

    @Query("SELECT * FROM iot_devices WHERE deviceId = :deviceId")
    suspend fun getById(deviceId: String): IoTDeviceEntity?

    @Query("SELECT * FROM iot_devices WHERE farmerId = :farmerId")
    suspend fun getDevicesByFarmer(farmerId: String): List<IoTDeviceEntity>

    @Query("SELECT * FROM iot_devices WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun fetchUpdatedIoTDevices(since: Long, limit: Int = 500): List<IoTDeviceEntity>

    @Query("SELECT * FROM iot_devices WHERE dirty = 1 LIMIT :limit")
    suspend fun getDirty(limit: Int = 100): List<IoTDeviceEntity>

    @Query("UPDATE iot_devices SET dirty = 0, syncedAt = :syncedAt WHERE deviceId = :deviceId")
    suspend fun clearDirty(deviceId: String, syncedAt: Long)
}