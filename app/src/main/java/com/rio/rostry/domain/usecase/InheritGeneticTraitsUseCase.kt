package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.GeneticTraitDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.local.PoultryTraitDao
import com.rio.rostry.data.model.GeneticTrait
import com.rio.rostry.data.model.Poultry
import com.rio.rostry.data.model.PoultryTrait
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class InheritGeneticTraitsUseCase @Inject constructor(
    private val poultryDao: PoultryDao,
    private val poultryTraitDao: PoultryTraitDao,
    private val geneticTraitDao: GeneticTraitDao,
    private val getGeneticTraitByIdUseCase: GetGeneticTraitByIdUseCase
) {
    suspend operator fun invoke(
        childId: String,
        parentId1Id: String,
        parentId2Id: String
    ): Result<Unit> {
        return try {
            // Get both parents
            val parent1 = poultryDao.getPoultryById(parentId1Id)
            val parent2 = poultryDao.getPoultryById(parentId2Id)
            
            if (parent1 == null || parent2 == null) {
                return Result.failure(Exception("One or both parents not found"))
            }
            
            // Get traits from both parents
            val parent1Traits = getPoultryTraits(parentId1Id)
            val parent2Traits = getPoultryTraits(parentId2Id)
            
            // Combine traits with inheritance logic
            val inheritedTraits = combineTraits(parent1Traits, parent2Traits)
            
            // Save inherited traits to child
            val poultryTraits = inheritedTraits.map { (trait, isDominant) ->
                PoultryTrait(
                    poultryId = childId,
                    traitId = trait.id,
                    isDominant = isDominant,
                    createdAt = Date()
                )
            }
            
            // Insert all traits for the child
            if (poultryTraits.isNotEmpty()) {
                poultryTraitDao.insertPoultryTraits(poultryTraits)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun getPoultryTraits(poultryId: String): List<PoultryTrait> {
        return poultryTraitDao.getTraitsByPoultryId(poultryId).first()
    }
    
    private suspend fun combineTraits(
        parent1Traits: List<PoultryTrait>,
        parent2Traits: List<PoultryTrait>
    ): List<Pair<GeneticTrait, Boolean>> {
        // This is a simplified inheritance model
        // In reality, this would be much more complex based on genetics
        
        val traitMap = mutableMapOf<String, Pair<PoultryTrait, PoultryTrait?>>()
        
        // Add parent 1 traits
        parent1Traits.forEach { trait ->
            traitMap[trait.traitId] = trait to null
        }
        
        // Add parent 2 traits
        parent2Traits.forEach { trait ->
            if (traitMap.containsKey(trait.traitId)) {
                val existing = traitMap[trait.traitId]!!
                traitMap[trait.traitId] = existing.first to trait
            } else {
                traitMap[trait.traitId] = trait to null
            }
        }
        
        // Determine inherited traits
        val inheritedTraits = mutableListOf<Pair<GeneticTrait, Boolean>>()
        
        traitMap.forEach { (_, traitPair) ->
            val (trait1, trait2) = traitPair
            
            // Simplified inheritance logic:
            // If both parents have the trait, child gets it as dominant
            // If only one parent has it, child may or may not inherit based on chance
            if (trait2 != null) {
                // Both parents have the trait
                val geneticTrait = getGeneticTraitByIdUseCase(trait1.traitId)
                if (geneticTrait != null) {
                    inheritedTraits.add(geneticTrait to true)
                }
            } else {
                // Only one parent has the trait - 50% chance of inheritance
                if (Math.random() > 0.5) {
                    val geneticTrait = getGeneticTraitByIdUseCase(trait1.traitId)
                    if (geneticTrait != null) {
                        inheritedTraits.add(geneticTrait to trait1.isDominant)
                    }
                }
            }
        }
        
        return inheritedTraits
    }
}