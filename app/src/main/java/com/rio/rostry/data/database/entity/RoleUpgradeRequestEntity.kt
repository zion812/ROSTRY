package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "role_upgrade_requests")
@TypeConverters(RoleUpgradeRequestEntity.DateConverter::class)
data class RoleUpgradeRequestEntity(
    @PrimaryKey val requestId: String = "",
    val userId: String,
    val currentRole: String,
    val requestedRole: String,
    val status: String = STATUS_PENDING, // PENDING, APPROVED, REJECTED
    val adminNotes: String? = null,
    val reviewedBy: String? = null,
    val reviewedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_REJECTED = "REJECTED"
        const val STATUS_CANCELLED = "CANCELLED"
    }
    
    class DateConverter {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? = date?.time
    }
}
