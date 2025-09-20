package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.GeneticTraitDao
import com.rio.rostry.data.model.GeneticTrait
import javax.inject.Inject

class GetGeneticTraitByIdUseCase @Inject constructor(
    private val geneticTraitDao: GeneticTraitDao
) {
    suspend operator fun invoke(id: String): GeneticTrait? {
        return geneticTraitDao.getGeneticTraitById(id)
    }
}