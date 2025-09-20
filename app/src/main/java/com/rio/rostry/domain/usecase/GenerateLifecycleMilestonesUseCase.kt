package com.rio.rostry.domain.usecase

import com.rio.rostry.data.model.LifecycleMilestone
import com.rio.rostry.data.model.Poultry
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.domain.model.MilestoneTemplate
import com.rio.rostry.domain.repository.MilestoneTemplateRepository
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class GenerateLifecycleMilestonesUseCase @Inject constructor(
    private val milestoneTemplateRepository: MilestoneTemplateRepository
) {
    suspend operator fun invoke(poultry: Poultry): List<LifecycleMilestone> {
        val templates = milestoneTemplateRepository.getAllMilestoneTemplates().first()
        val milestones = mutableListOf<LifecycleMilestone>()
        
        val currentDate = Date()
        val hatchDate = poultry.hatchDate.time
        val weekInMillis = 7 * 24 * 60 * 60 * 1000L
        
        for (template in templates) {
            val milestoneDate = Date(hatchDate + (template.weekNumber * weekInMillis))
            val alertDate = if (template.alertDaysBefore > 0) {
                Date(milestoneDate.time - (template.alertDaysBefore * 24 * 60 * 60 * 1000L))
            } else {
                null
            }
            
            val milestone = LifecycleMilestone(
                id = UUID.randomUUID().toString(),
                poultryId = poultry.id,
                weekNumber = template.weekNumber,
                stage = template.stage,
                title = template.title,
                description = template.description,
                date = milestoneDate,
                isCompleted = false,
                alertDate = alertDate,
                isAlertSent = false,
                createdAt = currentDate,
                updatedAt = currentDate
            )
            
            milestones.add(milestone)
        }
        
        return milestones
    }
}