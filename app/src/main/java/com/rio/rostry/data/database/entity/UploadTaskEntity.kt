package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upload_tasks")
data class UploadTaskEntity(
    @PrimaryKey val taskId: String,
    val localPath: String,
    val remotePath: String,
    val status: String, // QUEUED, UPLOADING, SUCCESS, FAILED
    val progress: Int = 0,
    val retries: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val error: String? = null,
    val contextJson: String? = null // e.g. {"transferId":"...","type":"BEFORE"}
)
