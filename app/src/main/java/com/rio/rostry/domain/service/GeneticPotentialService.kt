package com.rio.rostry.domain.service

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import javax.inject.Inject
import javax.inject.Singleton

data class GeneticPotentialResult(
    val lineageStrength: Int, // 0-100 score based on verified ancestors
    val traitPotential: Map<String, Float>, // Trait -> Score (0.0 - 1.0)
    val notableAncestors: List<NotableAncestor>
)

data class NotableAncestor(
    val name: String,
    val relation: String, // e.g., "Grandsire"
    val achievements: List<String>
)

@Singleton
class GeneticPotentialService @Inject constructor(
    private val pedigreeRepository: PedigreeRepository
) {

    /**
     * Calculates the genetic potential of a bird based on its lineage.
     * Analyzes ancestors for verified status, awards, and performance records.
     */
    suspend fun analyzeLineage(birdId: String): GeneticPotentialResult {
        // Fetch 3 generations of pedigree
        val pedigreeResult = pedigreeRepository.getFullPedigree(birdId, 3)
        val tree = pedigreeResult.data ?: return GeneticPotentialResult(0, emptyMap(), emptyList())

        val ancestors = flattenAncestorsWithRelation(tree, "Self")
        
        var verifiedCount = 0
        var totalAncestors = 0
        val notableList = mutableListOf<NotableAncestor>()
        
        // Mock traits for now - in real implementation, this would aggregate from ShowRecords
        val traitScores = mutableMapOf(
            "Size" to 0.5f,
            "Plumage" to 0.5f,
            "Form" to 0.5f,
            "Temperament" to 0.5f
        )

        ancestors.forEach { (node, relation) ->
            if (relation == "Self") return@forEach
            
            totalAncestors++
            
            // Assume we can check verification status from the bird entity (mocking logic here if field missing)
            // In a real app, we'd check node.bird.isVerified or similar
            if (node.bird.name.contains("Champion") || node.bird.name.contains("King")) {
                verifiedCount++
                traitScores["Size"] = (traitScores["Size"] ?: 0.5f) + 0.1f
                traitScores["Form"] = (traitScores["Form"] ?: 0.5f) + 0.1f
                
                notableList.add(
                    NotableAncestor(
                        name = node.bird.name,
                        relation = relation,
                        achievements = listOf("Champion Bloodline") // Placeholder
                    )
                )
            }
        }

        // Normalize scores
        val strengthScore = if (totalAncestors > 0) (verifiedCount.toFloat() / totalAncestors * 100).toInt() else 0
        
        val normalizedTraits = traitScores.mapValues { (_, score) ->
            score.coerceIn(0f, 1f)
        }

        return GeneticPotentialResult(
            lineageStrength = strengthScore,
            traitPotential = normalizedTraits,
            notableAncestors = notableList
        )
    }

    private fun flattenAncestorsWithRelation(tree: PedigreeTree, relation: String): List<Pair<PedigreeTree, String>> {
        val list = mutableListOf<Pair<PedigreeTree, String>>()
        list.add(tree to relation)
        
        tree.sire?.let { 
            list.addAll(flattenAncestorsWithRelation(it, "Sire of $relation"))
        }
        tree.dam?.let { 
            list.addAll(flattenAncestorsWithRelation(it, "Dam of $relation"))
        }
        return list
    }
}
