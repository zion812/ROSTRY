package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FamilyTreeDao
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface FamilyTreeRepository {
    fun getForProduct(productId: String): Flow<List<FamilyTreeEntity>>
    suspend fun upsert(node: FamilyTreeEntity)
}

@Singleton
class FamilyTreeRepositoryImpl @Inject constructor(
    private val dao: FamilyTreeDao
) : FamilyTreeRepository {
    override fun getForProduct(productId: String): Flow<List<FamilyTreeEntity>> = dao.getForProduct(productId)

    override suspend fun upsert(node: FamilyTreeEntity) {
        dao.upsert(node.copy(updatedAt = System.currentTimeMillis()))
    }
}
