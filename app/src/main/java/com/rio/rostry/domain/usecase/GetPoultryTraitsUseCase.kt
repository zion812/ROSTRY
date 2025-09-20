package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.GeneticTraitDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.local.PoultryTraitDao
import com.rio.rostry.data.model.GeneticTrait
import com.rio.rostry.data.model.PoultryTrait
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPoultryTraitsUseCase @Inject constructor(
    private val poultryTraitDao: PoultryTraitDao,
    private val geneticTraitDao: GeneticTraitDao
) {
    suspend operator fun invoke(poultryId: String): List<GeneticTrait> {
        val poultryTraits = poultryTraitDao.getTraitsByPoultryId(poultryId).first()
        val traitIds = poultryTraits.map { it.traitId }
        
        if (traitIds.isEmpty()) return emptyList()
        
        // In a real implementation, we would batch fetch these traits
        // For simplicity, we'll fetch them individually
        return traitIds.mapNotNull { traitId ->
            geneticTraitDao.getGeneticTraitById(traitId)
        }
    }
}