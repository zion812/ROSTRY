package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SensorType {
    TEMPERATURE, HUMIDITY, AMMONIA, LIGHT, WATER_FLOW, FEED_LEVEL, MOTION
}

@Entity(tableName = "iot_data")
data class IoTDataEntity(
    @PrimaryKey
    val dataId: String,
    val deviceId: String,
    val sensorType: SensorType,
    val value: Double,
    val unit: String,
    val timestamp: Long,
    val metadataJson: String? = null
)
