package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.BirdTraitRecordDao
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.entity.BirdTraitRecordEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import javax.inject.Inject
import javax.inject.Singleton

data class GeneticPotentialResult(
    val lineageStrength: Int, // 0-100 score based on documented & quality ancestors
    val traitPotential: Map<String, Float>, // Trait -> Score (0.0 - 1.0)
    val notableAncestors: List<NotableAncestor>,
    val dataQuality: String // "Excellent", "Good", "Fair", "Insufficient"
)

data class NotableAncestor(
    val name: String,
    val relation: String, // e.g., "Grandsire"
    val achievements: List<String>,
    val traitHighlights: Map<String, String> // key trait -> value
)

@Singleton
class GeneticPotentialService @Inject constructor(
    private val pedigreeRepository: PedigreeRepository,
    private val traitRecordDao: BirdTraitRecordDao,
    private val showRecordDao: ShowRecordDao
) {

    /**
     * Calculates the genetic potential of a bird based on its lineage.
     * Uses REAL trait records and show records instead of name-based heuristics.
     */
    suspend fun analyzeLineage(birdId: String): GeneticPotentialResult {
        // Fetch 3 generations of pedigree
        val pedigreeResult = pedigreeRepository.getFullPedigree(birdId, 3)
        val tree = pedigreeResult.data ?: return GeneticPotentialResult(
            lineageStrength = 0,
            traitPotential = emptyMap(),
            notableAncestors = emptyList(),
            dataQuality = "Insufficient"
        )

        val ancestors = flattenAncestorsWithRelation(tree, "Self")

        var totalScore = 0f
        var scoredAncestors = 0
        val notableList = mutableListOf<NotableAncestor>()

        // Aggregate real trait scores across generations with decay
        val traitAccumulator = mutableMapOf<String, MutableList<Pair<Float, Float>>>() // trait -> (value, weight)

        for ((node, relation) in ancestors) {
            if (relation == "Self") continue

            // Generation weight: parents = 1.0, grandparents = 0.5, great-grandparents = 0.25
            val genWeight = when {
                !relation.contains(" of ") -> 1.0f      // Direct parent
                relation.count { it == ' ' } <= 3 -> 0.5f // Grandparent
                else -> 0.25f                             // Great-grandparent
            }

            // Pull real trait data for this ancestor
            val ancestorTraits = traitRecordDao.getByBird(node.bird.id)
            val ancestorWins = showRecordDao.countWins(node.bird.id)
            val ancestorShows = showRecordDao.countTotal(node.bird.id)

            // Score this ancestor
            var ancestorScore = 0f
            val achievements = mutableListOf<String>()
            val traitHighlights = mutableMapOf<String, String>()

            // 1. Trait data contribution
            if (ancestorTraits.isNotEmpty()) {
                val traitScore = (ancestorTraits.size.toFloat() / 20f).coerceIn(0f, 0.5f)
                ancestorScore += traitScore

                // Add trait values to accumulator with generation weight
                ancestorTraits.forEach { record ->
                    val numVal = record.traitValue.toFloatOrNull()
                    if (numVal != null) {
                        val list = traitAccumulator.getOrPut(record.traitName) { mutableListOf() }
                        list.add(numVal to genWeight)

                        // Track highlights for notable ancestors
                        when (record.traitUnit) {
                            "score_1_10" -> if (numVal >= 8) traitHighlights[record.traitName] = "${numVal.toInt()}/10"
                            "grams", "g" -> if (numVal >= 3000) traitHighlights[record.traitName] = "${numVal.toInt()}g"
                            else -> {} // skip for highlights
                        }
                    }
                }

                achievements.add("${ancestorTraits.map { it.traitName }.distinct().size} traits recorded")
            }

            // 2. Show performance contribution
            if (ancestorShows > 0) {
                val winRate = ancestorWins.toFloat() / ancestorShows
                ancestorScore += (winRate * 0.5f)

                if (ancestorWins > 0) achievements.add("$ancestorWins wins in $ancestorShows shows")
                if (winRate >= 0.5f) achievements.add("Elite competitor (${(winRate * 100).toInt()}% win rate)")
            }

            // 3. Pedigree documentation
            if (node.sire != null || node.dam != null) {
                ancestorScore += 0.1f // Bonus for documented lineage
            }

            totalScore += ancestorScore * genWeight
            scoredAncestors++

            // Mark as notable if they have significant data
            if (achievements.isNotEmpty()) {
                notableList.add(
                    NotableAncestor(
                        name = node.bird.name,
                        relation = simplifyRelation(relation),
                        achievements = achievements,
                        traitHighlights = traitHighlights
                    )
                )
            }
        }

        // Compute lineage strength (0-100)
        val maxPossible = (1.0f + 1.0f + 0.5f + 0.5f + 0.5f + 0.5f) // 2 parents + 4 grandparents max
        val lineageStrength = if (scoredAncestors > 0) {
            ((totalScore / maxPossible) * 100).toInt().coerceIn(0, 100)
        } else 0

        // Compute weighted trait potential from accumulated data
        val traitPotential = traitAccumulator.mapValues { (_, weightedValues) ->
            if (weightedValues.isEmpty()) return@mapValues 0.5f

            var weightedSum = 0f
            var totalWeight = 0f
            weightedValues.forEach { (value, weight) ->
                weightedSum += value * weight
                totalWeight += weight
            }
            val avg = weightedSum / totalWeight

            // Normalize based on common ranges
            when {
                avg <= 10f -> (avg / 10f).coerceIn(0f, 1f) // Scores 1-10
                avg <= 100f -> (avg / 100f).coerceIn(0f, 1f) // Percentages
                else -> (avg / 5000f).coerceIn(0f, 1f) // Weights in grams
            }
        }

        val dataQuality = when {
            scoredAncestors >= 4 && traitAccumulator.size >= 5 -> "Excellent"
            scoredAncestors >= 2 && traitAccumulator.size >= 3 -> "Good"
            scoredAncestors >= 1 -> "Fair"
            else -> "Insufficient"
        }

        return GeneticPotentialResult(
            lineageStrength = lineageStrength,
            traitPotential = traitPotential,
            notableAncestors = notableList.sortedByDescending { it.achievements.size },
            dataQuality = dataQuality
        )
    }

    private fun flattenAncestorsWithRelation(
        tree: PedigreeTree,
        relation: String
    ): List<Pair<PedigreeTree, String>> {
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

    private fun simplifyRelation(relation: String): String {
        return when {
            relation == "Sire of Self" -> "Sire"
            relation == "Dam of Self" -> "Dam"
            relation.startsWith("Sire of Sire") -> "Paternal Grandsire"
            relation.startsWith("Dam of Sire") -> "Paternal Grandam"
            relation.startsWith("Sire of Dam") -> "Maternal Grandsire"
            relation.startsWith("Dam of Dam") -> "Maternal Grandam"
            relation.contains("Sire") && relation.count { it == 'S' || it == 'D' } > 2 -> "Great-Grandsire"
            relation.contains("Dam") && relation.count { it == 'S' || it == 'D' } > 2 -> "Great-Grandam"
            else -> relation
        }
    }
}
