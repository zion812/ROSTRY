package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "iot_devices")
data class IoTDeviceEntity(
    @PrimaryKey
    val deviceId: String,
    val farmerId: String,
    val name: String,
    val type: String, // e.g., "SENSOR", "CAMERA", "CONTROLLER"
    val location: String?, // e.g., "Coop A"
    val status: String, // "ONLINE", "OFFLINE", "ERROR"
    val lastHeartbeat: Long,
    val firmwareVersion: String?,
    val configJson: String?,
    val createdAt: Long = System.currentTimeMillis()
)
