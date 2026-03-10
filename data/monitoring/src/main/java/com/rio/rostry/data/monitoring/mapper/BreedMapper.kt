package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.Breed
import com.rio.rostry.data.database.entity.BreedEntity

/**
 * Mapper between BreedEntity and Breed domain model.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.4 - Data modules create mappers for entity ↔ model conversion
 */
object BreedMapper {
    
    fun toDomain(entity: BreedEntity): Breed {
        return Breed(
            id = entity.breedId,
            name = entity.name,
            description = entity.description,
            culinaryProfile = entity.culinaryProfile,
            farmingDifficulty = entity.farmingDifficulty,
            imageUrl = entity.imageUrl,
            tags = entity.tags
        )
    }
    
    fun toEntity(model: Breed): BreedEntity {
        return BreedEntity(
            breedId = model.id,
            name = model.name,
            description = model.description,
            culinaryProfile = model.culinaryProfile,
            farmingDifficulty = model.farmingDifficulty,
            imageUrl = model.imageUrl,
            tags = model.tags
        )
    }
}
