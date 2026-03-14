package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.domain.monitoring.repository.ProfitabilityRepository
import com.rio.rostry.core.model.ProfitabilitySummary
import com.rio.rostry.core.model.AssetROI
import com.rio.rostry.core.model.MonthlyProfit
import com.rio.rostry.core.model.BreedROI
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.database.dao.ExpenseDao
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ProductDao
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.firstOrNull

/**
 * Implementation of ProfitabilityRepository.
 * 
 * Provides financial analytics including revenue, expenses, ROI calculations,
 * and profitability trends for farm operations.
 */
@Singleton
class ProfitabilityRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val orderDao: OrderDao,
    private val farmAssetDao: FarmAssetDao,
    private val productDao: ProductDao,
    private val auth: FirebaseAuth
) : ProfitabilityRepository {

    override suspend fun getProfitabilitySummary(startDate: Long, endDate: Long): Result<ProfitabilitySummary> {
        val farmerId = auth.currentUser?.uid 
            ?: return Result.Error(Exception("Not Authenticated"))
        
        return try {
            val totalExpenses = expenseDao.getTotalInRange(farmerId, startDate, endDate)
            val totalRevenue = orderDao.sumDeliveredForSellerBetween(farmerId, startDate, endDate)
            val netProfit = totalRevenue - totalExpenses
            val margin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0

            Result.Success(
                ProfitabilitySummary(
                    totalRevenue = totalRevenue,
                    totalExpenses = totalExpenses,
                    netProfit = netProfit,
                    profitMarginPercent = margin
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Error calculating profitability")
            Result.Error(e)
        }
    }

    override suspend fun getAssetROI(assetId: String): Result<AssetROI> {
        return try {
            val asset = farmAssetDao.findById(assetId) 
                ?: return Result.Error(Exception("Asset not found"))
            
            val totalExpenses = expenseDao.getTotalForAsset(assetId)
            val orders = orderDao.getOrdersForProduct(assetId)
            val totalRevenue = orders.filter { it.status == "DELIVERED" }.sumOf { it.totalAmount }
            val netProfit = totalRevenue - totalExpenses
            val roi = if (totalExpenses > 0) (netProfit / totalExpenses) * 100 else 0.0

            Result.Success(
                AssetROI(
                    assetId = asset.assetId,
                    assetName = asset.name,
                    revenue = totalRevenue,
                    expenses = totalExpenses,
                    netProfit = netProfit,
                    roiPercent = roi
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Error calculating Asset ROI")
            Result.Error(e)
        }
    }

    override suspend fun getMonthlyTrends(months: Int): Result<List<MonthlyProfit>> {
        val farmerId = auth.currentUser?.uid 
            ?: return Result.Error(Exception("Not Authenticated"))
        
        val trends = mutableListOf<MonthlyProfit>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        return try {
            for (i in 0 until months) {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis

                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val end = calendar.timeInMillis

                val label = dateFormat.format(start)
                val expenses = expenseDao.getTotalInRange(farmerId, start, end)
                val revenue = orderDao.sumDeliveredForSellerBetween(farmerId, start, end)

                trends.add(
                    MonthlyProfit(
                        monthLabel = label,
                        yearMonth = start,
                        revenue = revenue,
                        expenses = expenses
                    )
                )

                calendar.timeInMillis = start
                calendar.add(Calendar.MONTH, -1)
            }
            Result.Success(trends.reversed())
        } catch (e: Exception) {
            Timber.e(e, "Error calculating trends")
            Result.Error(e)
        }
    }

    override suspend fun getExpenseBreakdown(startDate: Long, endDate: Long): Result<Map<String, Double>> {
        val farmerId = auth.currentUser?.uid 
            ?: return Result.Error(Exception("Not Authenticated"))
        
        return try {
            val expensesFlow = expenseDao.observeForDateRange(farmerId, startDate, endDate)
            val expenses = expensesFlow.firstOrNull() ?: emptyList()
            
            val breakdown = expenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { e -> e.amount } }
            
            Result.Success(breakdown)
        } catch (e: Exception) {
            Timber.e(e, "Error calculating expense breakdown")
            Result.Error(e)
        }
    }

    override suspend fun getProfitableBreeds(startDate: Long, endDate: Long): Result<List<BreedROI>> {
        val farmerId = auth.currentUser?.uid 
            ?: return Result.Error(Exception("Not Authenticated"))
        
        return try {
            // Get all products/breeds for this farmer
            val products = productDao.getActiveBySellerList(farmerId)
            
            // Group products by breed
            val breedGroups = products.groupBy { it.breed ?: "Unknown" }
            
            val breedROIs = breedGroups.map { (breed, breedProducts) ->
                var totalRevenue = 0.0
                var totalExpenses = 0.0
                var totalQuantity = 0.0
                var soldCount = 0
                
                breedProducts.forEach { product ->
                    totalQuantity += product.quantity
                    
                    // Get expenses for this product
                    val productExpenses = expenseDao.getTotalForAsset(product.productId)
                    totalExpenses += productExpenses
                    
                    // Get orders for this product
                    val orders = orderDao.getOrdersForProduct(product.productId)
                    val productRevenue = orders
                        .filter { it.status == "DELIVERED" }
                        .sumOf { it.totalAmount }
                    totalRevenue += productRevenue
                    soldCount += orders.count { it.status == "DELIVERED" }
                }
                
                val netProfit = totalRevenue - totalExpenses
                val roi = if (totalExpenses > 0) (netProfit / totalExpenses) * 100 else 0.0
                val avgSellingPrice = if (soldCount > 0) totalRevenue / soldCount else 0.0
                
                BreedROI(
                    breed = breed,
                    totalRevenue = totalRevenue,
                    totalExpenses = totalExpenses,
                    netProfit = netProfit,
                    roiPercent = roi,
                    unitsSold = soldCount,
                    averageSellingPrice = avgSellingPrice
                )
            }
            
            // Sort by ROI descending
            val sortedROIs = breedROIs.sortedByDescending { it.roiPercent }
            Result.Success(sortedROIs)
        } catch (e: Exception) {
            Timber.e(e, "Error calculating breed profitability")
            Result.Error(e)
        }
    }
}
