package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.rio.rostry.ui.farmer.TrendFetcherCard
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import com.rio.rostry.data.database.entity.MortalityRecordEntity

@HiltViewModel
class FarmMonitoringViewModel @Inject constructor(
    private val currentUserProvider: CurrentUserProvider,
    private val growthRecordDao: GrowthRecordDao,
    private val breedingPairDao: BreedingPairDao,
    private val eggCollectionDao: EggCollectionDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val quarantineRecordDao: QuarantineRecordDao,
    private val mortalityRecordDao: MortalityRecordDao
) : ViewModel() {

    private val uid = currentUserProvider.userIdOrNull()

    private val now = System.currentTimeMillis()
    private val weekAgo = now - 7 * 24 * 3600 * 1000L

    val summary: StateFlow<MonitoringSummary> = if (uid == null) {
        MutableStateFlow(MonitoringSummary())
    } else {
        combine(
            listOf(
                // 0: Growth tracked in the last 7 days
                growthRecordDao.observeCountForFarmerBetween(uid, weekAgo, now),
                // 1: Active breeding pairs
                breedingPairDao.observeActive(uid).map { it.size },
                // 2: Eggs collected today from recent collections stream
                eggCollectionDao.observeRecentByFarmer(uid, 50).map { list ->
                    val today = LocalDate.now(ZoneId.systemDefault())
                    val start = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    val end = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
                    list.filter { it.collectedAt in start..end }.sumOf { it.eggsCollected }
                },
                // 3: Hatching active count
                hatchingBatchDao.observeActiveForFarmer(uid, now),
                // 4: Hatching due this week
                hatchingBatchDao.observeDueThisWeekForFarmer(uid, now, now + 7 * 24 * 3600 * 1000L),
                // 5: Vaccination due
                vaccinationRecordDao.observeDueForFarmer(uid, now, now + 7 * 24 * 3600 * 1000L),
                // 6: Vaccination overdue
                vaccinationRecordDao.observeOverdueForFarmer(uid, now),
                // 7: Quarantine active
                quarantineRecordDao.observeActiveForFarmer(uid),
                // 8: Mortality last 7 days
                mortalityRecordDao.observeCountForFarmerBetween(uid, weekAgo, now),
                // 9: Growth Records for Trend
                flow { emit(growthRecordDao.getRecordsForFarmerBetween(uid, weekAgo, now)) },
                // 10: Mortality Records for Trend
                flow { emit(mortalityRecordDao.getRecordsForFarmerBetween(uid, weekAgo, now)) }
            )
        ) { values ->
            val growthTracked = values[0] as Int
            val breedingPairs = values[1] as Int
            val eggsCollectedToday = values[2] as Int
            val incubatingBatches = values[3] as Int
            val hatchDueThisWeek = values[4] as Int
            val vaccinationDue = values[5] as Int
            val vaccinationOverdue = values[6] as Int
            val quarantineActive = values[7] as Int
            val mortalityLast7d = values[8] as Int
            
            // Calculate trends
            val growthRecords = values[9] as List<GrowthRecordEntity>
            val mortalityRecords = values[10] as List<MortalityRecordEntity>
            
            val growthTrend = calculateDailyTrend(growthRecords) { it.createdAt }
            val mortalityTrend = calculateDailyTrend(mortalityRecords) { it.occurredAt }

            val pendingAlerts = mutableListOf<String>()
            if (vaccinationOverdue > 0) pendingAlerts.add("$vaccinationOverdue vaccinations overdue")
            if (quarantineActive > 0) pendingAlerts.add("$quarantineActive birds in quarantine need attention")
            if (mortalityLast7d > 5) pendingAlerts.add("High mortality rate detected ($mortalityLast7d deaths in 7 days)")
            MonitoringSummary(
                growthTracked = growthTracked,
                breedingPairs = breedingPairs,
                eggsCollectedToday = eggsCollectedToday,
                incubatingBatches = incubatingBatches,
                hatchDueThisWeek = hatchDueThisWeek,
                broodingBatches = 0, // Not implemented in seeding
                vaccinationDue = vaccinationDue,
                vaccinationOverdue = vaccinationOverdue,
                quarantineActive = quarantineActive,
                mortalityLast7d = mortalityLast7d,
                pendingAlerts = pendingAlerts,
                weeklyCadence = emptyList(), // Not implemented
                growthHistory = growthTrend,
                mortalityHistory = mortalityTrend
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonitoringSummary())
    }
    
    private fun <T> calculateDailyTrend(items: List<T>, timeSelector: (T) -> Long): List<Float> {
        val trend = MutableList(7) { 0f }
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        
        // Map last 7 days to indices 0..6
        items.forEach { item ->
            val date = java.time.Instant.ofEpochMilli(timeSelector(item)).atZone(zoneId).toLocalDate()
            val daysAgo = java.time.temporal.ChronoUnit.DAYS.between(date, today).toInt()
            if (daysAgo in 0..6) {
                // index 6 is today, 0 is 6 days ago? Or vice versa?
                // Visual sparklines usually go left-to-right (past -> future).
                // So index 0 = 6 days ago, index 6 = today.
                val index = 6 - daysAgo
                if (index in 0..6) {
                    trend[index] += 1f
                }
            }
        }
        return trend
    }
}

@Immutable
data class MonitoringSummary(
    val growthTracked: Int = 0,
    val breedingPairs: Int = 0,
    val eggsCollectedToday: Int = 0,
    val incubatingBatches: Int = 0,
    val hatchDueThisWeek: Int = 0,
    val broodingBatches: Int = 0,
    val vaccinationDue: Int = 0,
    val vaccinationOverdue: Int = 0,
    val quarantineActive: Int = 0,
    val mortalityLast7d: Int = 0,
    val pendingAlerts: List<String> = emptyList(),
    val weeklyCadence: List<String> = emptyList(),
    val growthHistory: List<Float> = emptyList(),
    val mortalityHistory: List<Float> = emptyList()
)

@Composable
fun FarmMonitoringScreen(
    onOpenGrowth: () -> Unit,
    onOpenVaccination: () -> Unit,
    onOpenBreeding: () -> Unit,
    onOpenQuarantine: () -> Unit,
    onOpenMortality: () -> Unit,
    onOpenHatching: () -> Unit,
    onOpenPerformance: () -> Unit,
    vm: FarmMonitoringViewModel = hiltViewModel()
) {
    val summary by vm.summary.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with last updated
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Farm Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Updated now", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Alerts Priority Card (if alerts exist)
        if (summary.pendingAlerts.isNotEmpty()) {
            AlertsPriorityCard(
                alerts = summary.pendingAlerts,
                onOpenVaccination = onOpenVaccination,
                onOpenQuarantine = onOpenQuarantine,
                onOpenMortality = onOpenMortality
            )
        }

        // Quick Actions Grid
        QuickActionsGrid(
            summary = summary,
            onOpenGrowth = onOpenGrowth,
            onOpenVaccination = onOpenVaccination,
            onOpenMortality = onOpenMortality,
            onOpenQuarantine = onOpenQuarantine,
            onOpenHatching = onOpenHatching,
            onOpenPerformance = onOpenPerformance
        )

        // Metrics Summary Card (collapsible)
        MetricsSummaryCard(summary = summary)

        // Upcoming Tasks Card
        UpcomingTasksCard(
            summary = summary,
            onOpenVaccination = onOpenVaccination,
            onOpenHatching = onOpenHatching
        )
    }
}

@Composable
private fun AlertsPriorityCard(
    alerts: List<String>,
    onOpenVaccination: () -> Unit,
    onOpenQuarantine: () -> Unit,
    onOpenMortality: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Alert",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    "⚠️ Urgent Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            alerts.take(3).forEach { alert ->
                AlertRow(
                    text = alert,
                    onAction = {
                        when {
                            alert.contains("vaccination", ignoreCase = true) -> onOpenVaccination()
                            alert.contains("quarantine", ignoreCase = true) -> onOpenQuarantine()
                            alert.contains("mortality", ignoreCase = true) -> onOpenMortality()
                            else -> { }
                        }
                    }
                )
            }
            if (alerts.size > 3) {
                Text(
                    "+ ${alerts.size - 3} more alerts",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun AlertRow(text: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        TextButton(
            onClick = onAction,
            modifier = Modifier.height(32.dp)
        ) {
            Text("View", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun QuickActionsGrid(
    summary: MonitoringSummary,
    onOpenGrowth: () -> Unit,
    onOpenVaccination: () -> Unit,
    onOpenMortality: () -> Unit,
    onOpenQuarantine: () -> Unit,
    onOpenHatching: () -> Unit,
    onOpenPerformance: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TrendFetcherCard(
                title = "Growth",
                count = "${summary.growthTracked} tracked",
                trendData = summary.growthHistory,
                icon = Icons.Filled.TrendingUp,
                color = MaterialTheme.colorScheme.primary,
                onClick = onOpenGrowth,
                modifier = Modifier.weight(1f)
            )
            // Use QuickActionCard for Vaccination as it doesn't have a trend yet (mostly future scheduled)
            QuickActionCard(
                icon = Icons.Filled.Vaccines,
                label = "Log Vaccination",
                count = summary.vaccinationDue,
                onClick = onOpenVaccination,
                modifier = Modifier.weight(1f),
                urgent = summary.vaccinationOverdue > 0
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TrendFetcherCard(
                title = "Mortality",
                count = "${summary.mortalityLast7d} deaths",
                trendData = summary.mortalityHistory,
                icon = Icons.Filled.Report,
                color = MaterialTheme.colorScheme.error,
                onClick = onOpenMortality,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Filled.MedicalServices,
                label = "Check Quarantine",
                count = summary.quarantineActive,
                onClick = onOpenQuarantine,
                modifier = Modifier.weight(1f),
                urgent = summary.quarantineActive > 0
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Filled.Egg,
                label = "View Hatching",
                count = summary.incubatingBatches,
                onClick = onOpenHatching,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Filled.Assessment,
                label = "Performance",
                count = 0,
                onClick = onOpenPerformance,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    urgent: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (urgent) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = if (urgent) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (urgent) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
            )
            if (count > 0) {
                Text(
                    text = "$count tracked",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (urgent) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetricsSummaryCard(summary: MonitoringSummary) {
    var expanded by remember { mutableStateOf(false) }
    
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Farm Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricRow("Growth Tracked", summary.growthTracked.toString())
                    MetricRow("Breeding Pairs", summary.breedingPairs.toString())
                    MetricRow("Eggs Today", summary.eggsCollectedToday.toString())
                    MetricRow("Incubating Batches", summary.incubatingBatches.toString())
                    MetricRow("Hatch Due (7d)", summary.hatchDueThisWeek.toString())
                    MetricRow("Vaccination Due", summary.vaccinationDue.toString(), urgent = summary.vaccinationOverdue > 0)
                    MetricRow("Quarantine Active", summary.quarantineActive.toString(), urgent = summary.quarantineActive > 0)
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, urgent: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (urgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun UpcomingTasksCard(
    summary: MonitoringSummary,
    onOpenVaccination: () -> Unit,
    onOpenHatching: () -> Unit
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Upcoming Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            
            if (summary.vaccinationDue > 0) {
                TaskRow(
                    icon = Icons.Filled.Vaccines,
                    title = "Vaccinations Due",
                    subtitle = "${summary.vaccinationDue} birds need vaccination",
                    onClick = onOpenVaccination
                )
            }
            
            if (summary.hatchDueThisWeek > 0) {
                TaskRow(
                    icon = Icons.Filled.Egg,
                    title = "Hatching Expected",
                    subtitle = "${summary.hatchDueThisWeek} batches due this week",
                    onClick = onOpenHatching
                )
            }
            
            if (summary.vaccinationDue == 0 && summary.hatchDueThisWeek == 0) {
                com.rio.rostry.ui.components.EmptyState(
                    title = "No upcoming tasks",
                    subtitle = "You're all caught up",
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun TaskRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        TextButton(onClick = onClick) {
            Text("View")
        }
    }
}
