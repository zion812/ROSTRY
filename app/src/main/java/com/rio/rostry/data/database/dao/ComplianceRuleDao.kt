package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.ComplianceRuleEntity

@Dao
interface ComplianceRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: ComplianceRuleEntity)

    @Query("SELECT * FROM compliance_rules WHERE jurisdiction = :jurisdiction AND isActive = 1")
    suspend fun getActiveRulesByJurisdiction(jurisdiction: String): List<ComplianceRuleEntity>

    @Query("SELECT * FROM compliance_rules WHERE ruleId = :ruleId")
    suspend fun getRuleById(ruleId: String): ComplianceRuleEntity?
}
