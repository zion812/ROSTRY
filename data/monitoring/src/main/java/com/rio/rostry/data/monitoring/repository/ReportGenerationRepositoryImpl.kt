package com.rio.rostry.data.monitoring.repository

import android.content.Context
import android.net.Uri
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.domain.monitoring.repository.ReportGenerationRepository
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.monitoring.service.PdfExportService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class for monthly report.
 */
data class MonthlyReportData(
    val farmerId: String,
    val farmerName: String,
    val month: Int,
    val year: Int,
    val reportDate: Long = System.currentTimeMillis(),
    val totalEggsCollected: Int = 0,
    val avgDailyEggs: Double = 0.0,
    val eggProductionTrend: String = "STABLE",
    val totalMortality: Int = 0,
    val mortalityRate: Double = 0.0,
    val mortalityCauses: Map<String, Int> = emptyMap(),
    val totalExpenses: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val totalFeedCost: Double = 0.0,
    val totalVaccineCost: Double = 0.0,
    val totalBirds: Int = 0,
    val topPerformingPens: List<TopPerformerData> = emptyList(),
    val avgWeightGain: Double = 0.0,
    val avgFCR: Double = 0.0,
    val vaccinationsAdministered: Int = 0,
    val vaccinationsDue: Int = 0,
    val vaccinationComplianceRate: Double = 0.0,
    val batchesHatched: Int = 0,
    val avgHatchabilityRate: Double = 0.0
)

data class TopPerformerData(
    val assetId: String,
    val name: String,
    val metric: String,
    val value: Double
)

@Singleton
class ReportGenerationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val pdfExportService: PdfExportService
) : ReportGenerationRepository {

    override suspend fun generateMonthlyReport(
        farmerId: String,
        farmerName: String,
        month: Int,
        year: Int
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1)
            val endDate = calendar.timeInMillis

            val reportData = collectReportData(farmerId, farmerName, month, year, startDate, endDate)
            val fileName = "ROSTRY_Monthly_Report_${year}_${month.toString().padStart(2, '0')}.pdf"
            val sections = buildReportSections(reportData)
            val uri = pdfExportService.writeReport(
                fileName = fileName,
                docTitle = "Monthly Farm Report - ${getMonthName(month)} $year",
                sections = sections
            )
            Result.Success(uri)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun collectReportData(
        farmerId: String, farmerName: String, month: Int, year: Int, startDate: Long, endDate: Long
    ): MonthlyReportData {
        val assets = database.farmAssetDao().getAssetsByFarmer(farmerId).first()
        val totalBirds = assets.sumOf { it.quantity.toInt() }
        val dailyLogs = database.dailyLogDao().observeForFarmerBetween(farmerId, startDate, endDate).first()
        val activityLogs = database.farmActivityLogDao().observeForFarmerBetween(farmerId, startDate, endDate).first()
        val expensesByCategory = activityLogs
            .filter { val amt = it.amountInr; amt != null && amt > 0 }
            .groupBy { it.category ?: "Other" }
            .mapValues { entry -> entry.value.sumOf { it.amountInr ?: 0.0 } }
        val totalExpenses = expensesByCategory.values.sum()
        val mortalityCount = database.mortalityRecordDao().countForFarmerBetween(farmerId, startDate, endDate)
        val mortalityRate = if (totalBirds > 0) (mortalityCount.toDouble() / totalBirds) * 100 else 0.0
        val vaccinationsAdministered = database.vaccinationRecordDao().countAdministeredBetweenForFarmer(farmerId, startDate, endDate)
        val vaccinationsDue = database.vaccinationRecordDao().countDueForFarmer(farmerId, startDate, endDate)
        val totalVaccinations = vaccinationsAdministered + vaccinationsDue
        val complianceRate = if (totalVaccinations > 0) (vaccinationsAdministered.toDouble() / totalVaccinations) * 100 else 100.0

        return MonthlyReportData(
            farmerId = farmerId, farmerName = farmerName, month = month, year = year,
            totalBirds = totalBirds, totalExpenses = totalExpenses, expensesByCategory = expensesByCategory,
            totalFeedCost = activityLogs.filter { it.activityType == "FEED" }.sumOf { it.amountInr ?: 0.0 },
            totalVaccineCost = activityLogs.filter { it.activityType == "MEDICATION" }.sumOf { it.amountInr ?: 0.0 },
            totalMortality = mortalityCount, mortalityRate = mortalityRate,
            vaccinationsAdministered = vaccinationsAdministered, vaccinationsDue = vaccinationsDue,
            vaccinationComplianceRate = complianceRate
        )
    }

    private fun buildReportSections(data: MonthlyReportData): List<PdfExportService.TableSection> {
        val sections = mutableListOf<PdfExportService.TableSection>()
        sections.add(PdfExportService.TableSection(
            title = "Executive Summary", headers = listOf("Metric", "Value"), rows = listOf(
                listOf("Total Birds", data.totalBirds.toString()),
                listOf("Total Expenses", "₹${String.format("%.2f", data.totalExpenses)}"),
                listOf("Mortality Rate", "${String.format("%.1f", data.mortalityRate)}%"),
                listOf("Vaccination Compliance", "${String.format("%.1f", data.vaccinationComplianceRate)}%")
            )
        ))
        if (data.expensesByCategory.isNotEmpty()) {
            sections.add(PdfExportService.TableSection(
                title = "Expenses by Category", headers = listOf("Category", "Amount (₹)"),
                rows = data.expensesByCategory.map { (cat, amt) -> listOf(cat, String.format("%.2f", amt)) }
            ))
        }
        sections.add(PdfExportService.TableSection(
            title = "Mortality Summary", headers = listOf("Metric", "Value"), rows = listOf(
                listOf("Total Deaths", data.totalMortality.toString()),
                listOf("Mortality Rate", "${String.format("%.2f", data.mortalityRate)}%")
            )
        ))
        sections.add(PdfExportService.TableSection(
            title = "Vaccination Summary", headers = listOf("Status", "Count"), rows = listOf(
                listOf("Administered", data.vaccinationsAdministered.toString()),
                listOf("Pending/Due", data.vaccinationsDue.toString()),
                listOf("Compliance Rate", "${String.format("%.1f", data.vaccinationComplianceRate)}%")
            )
        ))
        return sections
    }

    private fun getMonthName(month: Int): String = when (month) {
        1 -> "January"; 2 -> "February"; 3 -> "March"; 4 -> "April"; 5 -> "May"
        6 -> "June"; 7 -> "July"; 8 -> "August"; 9 -> "September"; 10 -> "October"
        11 -> "November"; 12 -> "December"; else -> "Unknown"
    }
}
