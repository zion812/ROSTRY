package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.GeneticTraitDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.local.PoultryTraitDao
import com.rio.rostry.data.model.GeneticTrait
import com.rio.rostry.data.model.PoultryTrait
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class AddGeneticTraitToPoultryUseCase @Inject constructor(
    private val geneticTraitDao: GeneticTraitDao,
    private val poultryTraitDao: PoultryTraitDao
) {
    suspend operator fun invoke(poultryId: String, traitId: String, isDominant: Boolean = false): Result<Unit> {
        return try {
            val trait = geneticTraitDao.getGeneticTraitById(traitId)
            if (trait == null) {
                return Result.failure(Exception("Genetic trait not found"))
            }
            
            val poultryTrait = PoultryTrait(
                poultryId = poultryId,
                traitId = traitId,
                isDominant = isDominant,
                createdAt = Date()
            )
            
            poultryTraitDao.insertPoultryTrait(poultryTrait)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}