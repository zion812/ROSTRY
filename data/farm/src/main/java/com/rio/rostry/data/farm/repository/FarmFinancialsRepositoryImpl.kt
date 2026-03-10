package com.rio.rostry.data.farm.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.FarmFinancials
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.farm.repository.FarmFinancialsRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FarmFinancialsRepository using Firebase Firestore.
 */
@Singleton
class FarmFinancialsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmFinancialsRepository {

    private val financialsCollection = firestore.collection("farm_financials")

    override suspend fun getFarmFinancials(farmerId: String): Result<FarmFinancials?> {
        return try {
            val document = financialsCollection.document(farmerId).get().await()
            val financials = document.toObject(FarmFinancials::class.java)
            Result.Success(financials)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateFarmFinancials(financials: FarmFinancials): Result<Unit> {
        return try {
            financialsCollection.document(financials.farmerId).set(financials).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getFinancialSummary(farmId: String): Result<Map<String, Double>> {
        return try {
            val document = financialsCollection.document(farmId).get().await()
            val financials = document.toObject(FarmFinancials::class.java)
            val summary = mapOf(
                "totalRevenue" to (financials?.totalRevenue ?: 0.0),
                "totalExpenses" to (financials?.totalExpenses ?: 0.0),
                "netProfit" to ((financials?.totalRevenue ?: 0.0) - (financials?.totalExpenses ?: 0.0))
            )
            Result.Success(summary)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
