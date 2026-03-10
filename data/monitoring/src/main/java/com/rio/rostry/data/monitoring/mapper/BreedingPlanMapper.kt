package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.BreedingPlan
import com.rio.rostry.data.database.entity.BreedingPlanEntity

/**
 * Mapper between BreedingPlanEntity and BreedingPlan domain model.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.4 - Data modules create mappers for entity ↔ model conversion
 */
object BreedingPlanMapper {
    
    fun toDomain(entity: BreedingPlanEntity): BreedingPlan {
        return BreedingPlan(
            id = entity.planId,
            farmerId = entity.farmerId,
            sireId = entity.sireId,
            sireName = entity.sireName,
            damId = entity.damId,
            damName = entity.damName,
            createdAt = entity.createdAt,
            note = entity.note,
            simulatedOffspringJson = entity.simulatedOffspringJson,
            status = entity.status,
            priority = entity.priority
        )
    }
    
    fun toEntity(model: BreedingPlan): BreedingPlanEntity {
        return BreedingPlanEntity(
            planId = model.id,
            farmerId = model.farmerId,
            sireId = model.sireId,
            sireName = model.sireName,
            damId = model.damId,
            damName = model.damName,
            createdAt = model.createdAt,
            note = model.note,
            simulatedOffspringJson = model.simulatedOffspringJson,
            status = model.status,
            priority = model.priority
        )
    }
}
