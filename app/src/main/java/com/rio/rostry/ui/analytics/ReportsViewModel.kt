package com.rio.rostry.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.ReportsDao
import com.rio.rostry.data.database.entity.ReportEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.export.PdfExporter
import com.rio.rostry.utils.export.CsvExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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
) : ViewModel() {
    private val empty = emptyList<ReportEntity>()
    private val uid = currentUserProvider.userIdOrNull()
    val reports: StateFlow<List<ReportEntity>> =
        (uid?.let { reportsDao.streamReports(it) } ?: MutableStateFlow(empty))
            .stateIn(viewModelScope, SharingStarted.Eagerly, empty)

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
            val uri = PdfExporter.writeSimpleTable(
                context = appContext,
                fileName = "weekly_report_${now}.pdf",
                title = "Weekly Report (${from} to ${now})",
                headers = listOf("date","orders","revenue","likes","comments"),
                rows = rows
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
            val uri = CsvExporter.writeCsv(
                context = appContext,
                fileName = "weekly_report_${now}.csv",
                headers = listOf("date","orders","revenue","likes","comments"),
                rows = rows
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
}
