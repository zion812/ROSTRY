package com.rio.rostry.domain.breeding

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.BirdPurpose
import com.rio.rostry.domain.model.LifecycleStage
import java.util.UUID

/**
 * Represents a concrete, simulated offspring from a breeding pair.
 * Unlike [TraitPrediction], this class holds specific values "rolled" from the probability distributions.
 */
data class SimulatedOffspring(
    val gender: String, // "Male" or "Female"
    val color: String,
    val breed: String,
    val traits: Map<String, Float>, // Trait Name -> Value
    val bvi: Float,
    val quality: String // "Show", "Breeder", "Pet"
) {
    fun toTemporaryProduct(): ProductEntity {
        return ProductEntity(
            productId = UUID.randomUUID().toString(),
            name = "Simulated ${if (gender == "Male") "Cockerel" else "Pullet"}",
            gender = gender,
            color = color,
            breed = breed,
            stage = LifecycleStage.CHICK,
            ageWeeks = 0,
            birthDate = System.currentTimeMillis(), // Now
            raisingPurpose = BirdPurpose.BREEDING.name,
            lifecycleStatus = "Simulated",
            status = "private",
            metadataJson = "{}" // TODO: Add specific trait metadata if needed for renderer
        )
    }
}
