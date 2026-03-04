package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compliance_rules")
data class ComplianceRuleEntity(
    @PrimaryKey val ruleId: String,
    val jurisdiction: String,
    val ruleType: String, // VACCINATION, RECORD_KEEPING, REPORTING, QUARANTINE
    val assetTypes: String, // JSON array of applicable asset types
    val ruleData: String, // JSON rule definition
    val isActive: Boolean = true,
    val effectiveFrom: Long,
    val effectiveUntil: Long?,
    val severity: String, // INFO, WARNING, CRITICAL
    val description: String,
    val reminderDays: Int?, // Days before deadline to remind
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
