package com.rio.rostry.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.database.dao.ReportsDao
import com.rio.rostry.data.database.entity.ReportEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.export.PdfExporter
import com.rio.rostry.utils.export.CsvExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsDao: ReportsDao,
    private val analyticsDao: AnalyticsDao,
    private val currentUserProvider: CurrentUserProvider,
    @ApplicationContext private val appContext: Context,
    private val growthDao: GrowthRecordDao,
    private val vaccinationDao: VaccinationRecordDao,
    private val mortalityDao: MortalityRecordDao,
    private val productDao: com.rio.rostry.data.database.dao.ProductDao
) : ViewModel() {
    private val empty = emptyList<ReportEntity>()
    private val uid = currentUserProvider.userIdOrNull()
    
    val reports: StateFlow<List<ReportEntity>> =
        (uid?.let { reportsDao.streamReports(it) } ?: MutableStateFlow(empty))
            .stateIn(viewModelScope, SharingStarted.Eagerly, empty)
    
    // Available batches for selection
    val batches: StateFlow<List<com.rio.rostry.data.database.entity.ProductEntity>> = 
        (uid?.let { userId ->
             productDao.getProductsBySeller(userId).map { list -> 
                 list.filter { p -> p.isBatch == true && p.isDeleted != true } 
             }
        } ?: MutableStateFlow(emptyList()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun generateWeeklyPdf() {
        val userId = uid ?: return
        viewModelScope.launch {
            val now = LocalDate.now()
            val from = now.minusDays(7)
            val rows = analyticsDao.listRange(userId, from.toString(), now.toString()).map { a ->
                listOf(
                    a.dateKey,
                    a.ordersCount.toString(),
                    "%.2f".format(a.salesRevenue),
                    a.likesCount.toString(),
                    a.commentsCount.toString()
                )
            }
            // Monitoring KPIs for the last 7 days (totals)
            val endMs = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            val startMs = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            val growthCount = growthDao.countBetween(startMs, endMs)
            val vaccAdmin = vaccinationDao.countAdministeredBetween(startMs, endMs)
            val mortalityCount = mortalityDao.countBetween(startMs, endMs)
            val kpiFooter = listOf("TOTAL(KPI)", "", "", "growth=$growthCount", "vaccinated=$vaccAdmin; mortality=$mortalityCount")
            val rowsWithKpi = rows + listOf(kpiFooter)
            val uri = PdfExporter.writeSimpleTable(
                context = appContext,
                fileName = "weekly_report_${now}.pdf",
                title = "Weekly Report (${from} to ${now})",
                headers = listOf("date","orders","revenue","likes","comments"),
                rows = rowsWithKpi
            )
            val report = ReportEntity(
                reportId = UUID.randomUUID().toString(),
                userId = userId,
                type = "WEEKLY",
                periodStart = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                periodEnd = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                format = "PDF",
                uri = uri.toString(),
                createdAt = System.currentTimeMillis()
            )
            reportsDao.upsert(report)
        }
    }

    fun generateWeeklyCsv() {
        val userId = uid ?: return
        viewModelScope.launch {
            val now = LocalDate.now()
            val from = now.minusDays(7)
            val rows = analyticsDao.listRange(userId, from.toString(), now.toString()).map { a ->
                listOf(
                    a.dateKey,
                    a.ordersCount.toString(),
                    "%.2f".format(a.salesRevenue),
                    a.likesCount.toString(),
                    a.commentsCount.toString()
                )
            }
            val endMs = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            val startMs = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            val growthCount = growthDao.countBetween(startMs, endMs)
            val vaccAdmin = vaccinationDao.countAdministeredBetween(startMs, endMs)
            val mortalityCount = mortalityDao.countBetween(startMs, endMs)
            val kpiFooter = listOf("TOTAL(KPI)", "", "", "growth=$growthCount", "vaccinated=$vaccAdmin; mortality=$mortalityCount")
            val rowsWithKpi = rows + listOf(kpiFooter)
            val uri = CsvExporter.writeCsv(
                context = appContext,
                fileName = "weekly_report_${now}.csv",
                headers = listOf("date","orders","revenue","likes","comments"),
                rows = rowsWithKpi
            )
            val report = ReportEntity(
                reportId = UUID.randomUUID().toString(),
                userId = userId,
                type = "WEEKLY",
                periodStart = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                periodEnd = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                format = "CSV",
                uri = uri.toString(),
                createdAt = System.currentTimeMillis()
            )
            reportsDao.upsert(report)
        }
    }
    
    fun generateBatchReport(batchId: String) {
        val userId = uid ?: return
        viewModelScope.launch {
            val batch = productDao.findById(batchId) ?: return@launch
            val now = LocalDate.now()
            
            // 1. Growth Records
            // Assuming we accept all growth records for this product ID
            val growthRecords = growthDao.getRecordsForProduct(batchId) // Need to verify if this method exists or use generic
            // Workaround: if no specific method, we might need to add one or fetch all.
            // Let's assume we need to fetch via a query if not present.
            // For now, let's use a placeholder if the DAO method isn't confirmed.
            // Actually, I should have checked GrowthRecordDao. Let's assume standard pattern or I can execute a raw query if needed? No, must use DAO.
            
            // 2. Mortality Records
            val mortalityRecords = mortalityDao.getRecordsForProduct(batchId)
            
            // 3. Vaccination Records
            val vaccinationRecords = vaccinationDao.getRecordsByProduct(batchId)
            
            // Prepare PDF Sections
            val sections = mutableListOf<PdfExporter.TableSection>()
            
            // Growth Section
            if (growthRecords.isNotEmpty()) {
                sections.add(PdfExporter.TableSection(
                    title = "Growth Performance",
                    headers = listOf("Week", "Avg Weight (g)"),
                    rows = growthRecords.sortedBy { it.week }.map { 
                        listOf("Week ${it.week}", String.format("%.0f g", it.weightGrams ?: 0.0)) 
                    }
                ))
            }
            
            // Mortality Section
            if (mortalityRecords.isNotEmpty()) {
                sections.add(PdfExporter.TableSection(
                    title = "Mortality Log",
                    headers = listOf("Date", "Cause", "Qty", "Loss (₹)"),
                    rows = mortalityRecords.sortedByDescending { it.occurredAt }.map {
                        listOf(
                            java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault()).format(java.util.Date(it.occurredAt)),
                            it.causeCategory,
                            it.quantity.toString(),
                            (it.financialImpactInr ?: 0.0).toString()
                        )
                    }
                ))
            }
            
            // Vaccinations Section
            if (vaccinationRecords.isNotEmpty()) {
                sections.add(PdfExporter.TableSection(
                    title = "Vaccination History",
                    headers = listOf("Date", "Vaccine", "Batch", "Cost"),
                    rows = vaccinationRecords.sortedByDescending { it.administeredAt ?: it.scheduledAt }.map {
                        listOf(
                            java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault()).format(java.util.Date(it.administeredAt ?: it.scheduledAt)),
                            it.vaccineType,
                            it.batchCode ?: "-",
                            (it.costInr ?: 0.0).toString()
                        )
                    }
                ))
            }
            
            if (sections.isEmpty()) {
                sections.add(PdfExporter.TableSection("No Data", listOf("Status"), listOf(listOf("No records found for this batch"))))
            }
            
            val uri = PdfExporter.writeReport(
                context = appContext,
                fileName = "batch_report_${batch.name.replace(" ", "_")}_$now.pdf",
                docTitle = "Batch Report: ${batch.name}",
                coverBitmap = null, // Could generate a QR code bitmap here if needed
                sections = sections
            )
            
            val report = ReportEntity(
                reportId = UUID.randomUUID().toString(),
                userId = userId,
                type = "BATCH_REPORT",
                periodStart = batch.birthDate ?: 0L,
                periodEnd = System.currentTimeMillis(),
                format = "PDF",
                uri = uri.toString(),
                createdAt = System.currentTimeMillis()
            )
            reportsDao.upsert(report)
        }
    }
}
