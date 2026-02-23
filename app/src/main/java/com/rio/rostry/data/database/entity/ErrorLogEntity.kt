package com.rio.rostry.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for persisting error logs locally.
 */
@Entity(tableName = "error_logs")
data class ErrorLogEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(index = true)
    val timestamp: Long,

    @ColumnInfo(name = "user_id", index = true)
    val userId: String?,

    @ColumnInfo(name = "operation_name")
    val operationName: String,

    @ColumnInfo(name = "error_category", index = true)
    val errorCategory: String,

    @ColumnInfo(name = "error_message")
    val errorMessage: String,

    @ColumnInfo(name = "stack_trace")
    val stackTrace: String?,

    @ColumnInfo(name = "additional_data")
    val additionalData: String?,

    val reported: Boolean = false
)
