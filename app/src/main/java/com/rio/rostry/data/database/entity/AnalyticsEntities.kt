package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "analytics_daily",
    indices = [Index(value=["userId","dateKey"], unique = true), Index("role")]
)
data class AnalyticsDailyEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val role: String, // GENERAL, FARMER, ENTHUSIAST
    val dateKey: String, // YYYY-MM-DD
    val salesRevenue: Double,
    val ordersCount: Int,
    val productViews: Int,
    val likesCount: Int,
    val commentsCount: Int,
    val transfersCount: Int,
    val breedingSuccessRate: Double,
    val engagementScore: Double,
    val createdAt: Long,
)

@Entity(tableName = "reports", indices = [Index("userId"), Index("periodStart"), Index("type")])
data class ReportEntity(
    @PrimaryKey val reportId: String,
    val userId: String,
    val type: String, // DAILY, WEEKLY, MONTHLY, CUSTOM
    val periodStart: Long,
    val periodEnd: Long,
    val format: String, // CSV, PDF
    val uri: String?, // file uri
    val createdAt: Long,
)
