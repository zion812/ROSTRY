package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.IoTDataEntity
import com.rio.rostry.data.database.entity.SensorType

@Dao
interface IoTDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(data: IoTDataEntity)

    @Query("SELECT * FROM iot_data WHERE deviceId = :deviceId AND sensorType = :sensorType ORDER BY readingAt DESC LIMIT 1")
    suspend fun getLatestByDeviceAndSensor(deviceId: String, sensorType: SensorType): IoTDataEntity?

    @Query("SELECT * FROM iot_data WHERE deviceId = :deviceId AND sensorType = :sensorType ORDER BY readingAt DESC LIMIT :count")
    suspend fun getRecentByDeviceAndSensor(deviceId: String, sensorType: SensorType, count: Int): List<IoTDataEntity>

    @Query("SELECT * FROM iot_data WHERE productId = :productId AND sensorType = :sensorType ORDER BY readingAt DESC LIMIT 1")
    suspend fun getLatestByProductAndSensor(productId: String, sensorType: SensorType): IoTDataEntity?

    @Query("SELECT * FROM iot_data WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun fetchUpdatedIoTData(since: Long, limit: Int = 500): List<IoTDataEntity>

    @Query("SELECT * FROM iot_data WHERE dirty = 1 LIMIT :limit")
    suspend fun getDirty(limit: Int = 100): List<IoTDataEntity>

    @Query("UPDATE iot_data SET dirty = 0, syncedAt = :syncedAt WHERE dataId = :dataId")
    suspend fun clearDirty(dataId: String, syncedAt: Long)
}