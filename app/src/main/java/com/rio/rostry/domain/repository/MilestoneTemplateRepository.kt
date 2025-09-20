package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.MilestoneTemplate
import kotlinx.coroutines.flow.Flow

interface MilestoneTemplateRepository {
    fun getAllMilestoneTemplates(): Flow<List<MilestoneTemplate>>
    suspend fun getMilestoneTemplateById(id: String): MilestoneTemplate?
    suspend fun insertMilestoneTemplate(template: MilestoneTemplate)
    suspend fun updateMilestoneTemplate(template: MilestoneTemplate)
    suspend fun deleteMilestoneTemplate(template: MilestoneTemplate)
}