package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class EnthusiastFlockViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val breedingDao: BreedingRecordDao,
    private val lifecycleDao: LifecycleEventDao,
    currentUserProvider: CurrentUserProvider,
) : ViewModel() {

    data class Alert(val title: String, val description: String, val severity: String = "WARN")

    data class UiState(
        val activeBirds: Int = 0,
        val breedingPairs: Int = 0,
        val chicks: Int = 0,
        val vaccinationsDue: Int = 0,
        val inQuarantine: Int = 0,
        val recentMortality: Int = 0,
        val alerts: List<Alert> = emptyList(),
    )

    private val uid = currentUserProvider.userIdOrNull()

    // Products owned by current user
    private val productsFlow = if (!uid.isNullOrBlank()) productDao.getProductsBySeller(uid) else MutableStateFlow(emptyList())

    // Build alerts and counts from available DAOs
    val state: StateFlow<UiState> = productsFlow.flatMapLatest { products ->
        // Derive counts from product fields
        val activeBirds = products.count { !it.isDeleted }
        val chicks = products.count { (it.birthDate ?: Long.MIN_VALUE) >= (System.currentTimeMillis() - 35L * 24 * 60 * 60 * 1000) }

        // Breeding pairs: unique (parent, partner) observed across user's products as children
        viewModelScope.launch { /* no-op, placeholder to respect scope */ }
        val childIds = products.map { it.productId }.toSet()
        // Query breeding records per parent (suspend). We'll approximate by scanning parents referenced by children
        // For simplicity, we count unique pairs that produced any child of the user
        var pairs = 0
        val seen = mutableSetOf<Pair<String, String>>()
        for (child in childIds) {
            val recs = breedingDao.recordsByChild(child)
            for (r in recs) {
                val key = if (r.parentId <= r.partnerId) r.parentId to r.partnerId else r.partnerId to r.parentId
                if (seen.add(key)) pairs++
            }
        }

        // Vaccination due and recent mortality from lifecycle events (best-effort):
        // We'll look at last 30 days events for each product and infer counts.
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000
        var vaccinationsDue = 0
        var recentMortality = 0
        val alerts = mutableListOf<Alert>()
        // Observe events per product once (suspending range not available for all time); use range by weeks is not practical here.
        // We'll just count any events with type markers in notes/stage heuristics from observeForProduct snapshots.
        for (p in products) {
            // Snapshot stream once using range approximation (best-effort):
            // If your DAO has better endpoints, wire them later.
            val events = lifecycleDao.range(p.productId, 0, 9999)
            events.filter { it.timestamp >= thirtyDaysAgo }.forEach { ev ->
                when (ev.type) {
                    "VACCINATION" -> if ((ev.timestamp ?: 0L) >= thirtyDaysAgo) vaccinationsDue++
                    "MORTALITY" -> recentMortality++
                }
                if (ev.notes?.contains("critical", ignoreCase = true) == true) {
                    alerts += Alert(title = "Critical Alert", description = "${p.name.ifBlank { p.productId }}: ${ev.notes}", severity = "CRITICAL")
                }
            }
        }

        // Quarantine not wired in DB: keep zero by default
        val inQuarantine = 0

        MutableStateFlow(
            UiState(
                activeBirds = activeBirds,
                breedingPairs = pairs,
                chicks = chicks,
                vaccinationsDue = vaccinationsDue,
                inQuarantine = inQuarantine,
                recentMortality = recentMortality,
                alerts = alerts.take(5)
            )
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())
}
