package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.ExpenseDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Per-bird and per-breeding-pair ROI analysis.
 * Calculates acquisition cost, lifetime expenses, revenue from offspring sales,
 * and returns a net ROI figure.
 */
@Singleton
class BreedingROIService @Inject constructor(
    private val productDao: ProductDao,
    private val expenseDao: ExpenseDao,
    private val showRecordDao: ShowRecordDao,
    private val breedingValueService: BreedingValueService
) {

    data class BirdROI(
        val bird: ProductEntity,
        val acquisitionCost: Double,       // Bird's listed price (purchase cost)
        val lifetimeExpenses: Double,       // Expenses linked to this asset
        val offspringCount: Int,
        val offspringTotalValue: Double,    // Sum of all offspring sale prices
        val offspringSold: Int,             // Offspring with status "sold" or "available"
        val showWins: Int,
        val showTotal: Int,
        val estimatedShowValue: Double,     // Proxy: wins × avg offspring value or fixed premium
        val totalRevenue: Double,           // offspringTotalValue + showValue
        val totalCost: Double,             // acquisitionCost + lifetimeExpenses
        val netProfit: Double,             // totalRevenue - totalCost
        val roiPercent: Double,            // (netProfit / totalCost) × 100
        val bvi: Float,                    // Breeding Value Index
        val costPerChick: Double,          // totalCost / offspringCount (or 0 if no offspring)
        val revenuePerChick: Double,       // offspringTotalValue / offspringCount
        val profitabilityRating: String    // "Highly Profitable", "Profitable", "Break-Even", "Loss-Making"
    )

    data class PairingROI(
        val sire: ProductEntity,
        val dam: ProductEntity,
        val combinedCost: Double,
        val sharedOffspringCount: Int,
        val sharedOffspringValue: Double,
        val costPerChick: Double,
        val estimatedROI: Double,
        val recommendation: String
    )

    /**
     * Calculate full ROI analysis for a single bird
     */
    suspend fun analyzeBirdROI(productId: String): BirdROI {
        val bird = productDao.findById(productId)
            ?: throw IllegalArgumentException("Bird not found: $productId")

        val acquisitionCost = bird.price.coerceAtLeast(0.0)
        val lifetimeExpenses = try { expenseDao.getTotalForAsset(productId) } catch (_: Exception) { 0.0 }

        // Offspring analysis
        val offspring = productDao.getOffspring(productId)
        val offspringCount = offspring.size
        val offspringTotalValue = offspring.sumOf { it.price.coerceAtLeast(0.0) }
        val offspringSold = offspring.count { it.status?.lowercase() in listOf("sold", "available") }

        // Show performance
        val showWins = try { showRecordDao.countWins(productId) } catch (_: Exception) { 0 }
        val showTotal = try { showRecordDao.countTotal(productId) } catch (_: Exception) { 0 }

        // Estimated show value: winning birds command a premium
        // Approximate: each win adds value proportional to average offspring price
        val avgOffspringPrice = if (offspringCount > 0) offspringTotalValue / offspringCount else bird.price * 0.5
        val estimatedShowValue = showWins * avgOffspringPrice * 0.3  // 30% premium per win

        val totalRevenue = offspringTotalValue + estimatedShowValue
        val totalCost = (acquisitionCost + lifetimeExpenses).coerceAtLeast(1.0) // prevent div/0

        val netProfit = totalRevenue - totalCost
        val roiPercent = (netProfit / totalCost) * 100

        // BVI
        val bvi = try { breedingValueService.calculateBVI(productId).bvi } catch (_: Exception) { 0f }

        val costPerChick = if (offspringCount > 0) totalCost / offspringCount else 0.0
        val revenuePerChick = if (offspringCount > 0) offspringTotalValue / offspringCount else 0.0

        val profitabilityRating = when {
            roiPercent >= 50 -> "Highly Profitable"
            roiPercent >= 10 -> "Profitable"
            roiPercent >= -5 -> "Break-Even"
            else -> "Loss-Making"
        }

        return BirdROI(
            bird = bird,
            acquisitionCost = acquisitionCost,
            lifetimeExpenses = lifetimeExpenses,
            offspringCount = offspringCount,
            offspringTotalValue = offspringTotalValue,
            offspringSold = offspringSold,
            showWins = showWins,
            showTotal = showTotal,
            estimatedShowValue = estimatedShowValue,
            totalRevenue = totalRevenue,
            totalCost = totalCost,
            netProfit = netProfit,
            roiPercent = roiPercent,
            bvi = bvi,
            costPerChick = costPerChick,
            revenuePerChick = revenuePerChick,
            profitabilityRating = profitabilityRating
        )
    }

    /**
     * Analyze ROI for a specific sire-dam pairing based on shared offspring
     */
    suspend fun analyzePairingROI(sireId: String, damId: String): PairingROI {
        val sire = productDao.findById(sireId)
            ?: throw IllegalArgumentException("Sire not found")
        val dam = productDao.findById(damId)
            ?: throw IllegalArgumentException("Dam not found")

        val sireOffspring = productDao.getOffspring(sireId).map { it.productId }.toSet()
        val damOffspring = productDao.getOffspring(damId).map { it.productId }.toSet()
        val sharedIds = sireOffspring.intersect(damOffspring)

        val sharedOffspring = sharedIds.mapNotNull { productDao.findById(it) }
        val sharedValue = sharedOffspring.sumOf { it.price.coerceAtLeast(0.0) }

        val combinedCost = sire.price + dam.price +
                (try { expenseDao.getTotalForAsset(sireId) } catch (_: Exception) { 0.0 }) +
                (try { expenseDao.getTotalForAsset(damId) } catch (_: Exception) { 0.0 })

        val costPerChick = if (sharedOffspring.isNotEmpty()) combinedCost / sharedOffspring.size else 0.0
        val estimatedROI = if (combinedCost > 0) ((sharedValue - combinedCost) / combinedCost) * 100 else 0.0

        val recommendation = when {
            estimatedROI >= 50 && sharedOffspring.size >= 3 -> "Star Pairing — continue breeding"
            estimatedROI >= 20 -> "Good pairing — profitable so far"
            sharedOffspring.isEmpty() -> "No shared offspring yet — too early to evaluate"
            estimatedROI >= 0 -> "Average — consider genetic improvements"
            else -> "Underperforming — evaluate alternatives"
        }

        return PairingROI(
            sire = sire,
            dam = dam,
            combinedCost = combinedCost,
            sharedOffspringCount = sharedOffspring.size,
            sharedOffspringValue = sharedValue,
            costPerChick = costPerChick,
            estimatedROI = estimatedROI,
            recommendation = recommendation
        )
    }
}
