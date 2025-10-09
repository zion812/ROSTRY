package com.rio.rostry.ui.traceability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.rio.rostry.utils.export.PdfExporter
import com.rio.rostry.utils.export.PdfExporter.TableSection
import com.rio.rostry.utils.QrUtils
import kotlinx.coroutines.flow.first

@HiltViewModel
class TraceabilityViewModel @Inject constructor(
    private val traceRepo: TraceabilityRepository,
    private val breedingDao: BreedingRecordDao,
    private val growthDao: GrowthRecordDao,
    private val vaccinationDao: VaccinationRecordDao,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val rootId: String = "",
        val layersUp: Map<Int, List<String>> = emptyMap(),
        val layersDown: Map<Int, List<String>> = emptyMap(),
        val edges: List<Pair<String, String>> = emptyList(),
        val transferChain: List<Any> = emptyList(),
        val error: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(productId: String, maxDepth: Int = 5) {
        _state.value = _state.value.copy(loading = true, rootId = productId, error = null)
        viewModelScope.launch {
            val ancRes = traceRepo.ancestors(productId, maxDepth)
            val descRes = traceRepo.descendants(productId, maxDepth)
            val chainRes = traceRepo.getTransferChain(productId)
            val anc = if (ancRes is Resource.Success) ancRes.data ?: emptyMap() else emptyMap()
            val desc = if (descRes is Resource.Success) descRes.data ?: emptyMap() else emptyMap()
            val chain = if (chainRes is Resource.Success) chainRes.data ?: emptyList() else emptyList()

            // Build edge list: for all nodes encountered, query breeding records by parent and map to child edges
            val nodes = buildSet {
                add(productId)
                anc.values.forEach { addAll(it) }
                desc.values.forEach { addAll(it) }
            }
            val edges = mutableListOf<Pair<String, String>>()
            for (n in nodes) {
                val recs = breedingDao.recordsByParent(n)
                recs.forEach { r -> edges += (r.parentId to r.childId); edges += (r.partnerId to r.childId) }
            }
            _state.value = _state.value.copy(loading = false, layersUp = anc, layersDown = desc, edges = edges, transferChain = chain)
        }
    }

    suspend fun generateLineageProof(productId: String): android.net.Uri {
        // Ensure state is up-to-date for given productId
        if (_state.value.rootId != productId) {
            load(productId)
        }

        // Gather data
        val up = _state.value.layersUp.toSortedMap()
        val down = _state.value.layersDown.toSortedMap()
        val directParents = up[1].orEmpty()
        val directOffspring = down[1].orEmpty()

        val transfersRes = traceRepo.getTransferChain(productId)
        val transfers = if (transfersRes is com.rio.rostry.utils.Resource.Success) transfersRes.data ?: emptyList() else emptyList()

        val vaccinations = vaccinationDao.observeForProduct(productId).first()
        val growth = growthDao.observeForProduct(productId).first()

        // Sections
        val lineageRows = mutableListOf<List<String>>()
        up.forEach { (level, ids) -> lineageRows += listOf("-${level}", ids.size.toString()) }
        down.forEach { (level, ids) -> lineageRows += listOf("+${level}", ids.size.toString()) }
        val lineageSection = TableSection(
            title = "Lineage Overview",
            headers = listOf("Level", "Count"),
            rows = lineageRows
        )

        val relativesRows = mutableListOf<List<String>>()
        relativesRows += listOf("Parents", directParents.joinToString(", ").ifBlank { "-" })
        relativesRows += listOf("Offspring", directOffspring.joinToString(", ").ifBlank { "-" })
        val relativesSection = TableSection(
            title = "Direct Relatives",
            headers = listOf("Type", "IDs"),
            rows = relativesRows
        )

        val vaccRows = vaccinations.map { v ->
            listOf(
                v.vaccineType,
                v.supplier ?: "-",
                v.batchCode ?: "-",
                formatDate(v.scheduledAt),
                v.administeredAt?.let { formatDate(it) } ?: "-"
            )
        }
        val vaccSection = TableSection(
            title = "Vaccination History",
            headers = listOf("Vaccine", "Supplier", "Batch", "Scheduled", "Administered"),
            rows = vaccRows
        )

        val growthRows = growth.map { g ->
            listOf(
                "Week ${g.week}",
                g.weightGrams?.toString() ?: "-",
                g.heightCm?.toString() ?: "-",
                g.healthStatus ?: "-",
                formatDate(g.createdAt)
            )
        }
        val growthSection = TableSection(
            title = "Growth History",
            headers = listOf("Week", "Weight(g)", "Height(cm)", "Health", "Recorded"),
            rows = growthRows
        )

        val transferRows = transfers.map { t ->
            // Use reflection-safe map if generic type; otherwise assume it has fields
            val from = t.toString().let { it } // fallback string
            listOf(
                // Try to parse known fields via when/cast if available; else present string
                t.javaClass.simpleName,
                // placeholders if the chain item is a data class with fields
                // Integrations can adjust TraceabilityRepository to return a typed model
                from,
                // timestamps if available in toString
                "-"
            )
        }
        val transferSection = TableSection(
            title = "Transfer History",
            headers = listOf("Type", "Details", "Timestamp"),
            rows = transferRows
        )

        // Cover QR (deep link)
        val qr = QrUtils.generateQr("rostry://product/${productId}")

        val sections = listOf(lineageSection, relativesSection, vaccSection, growthSection, transferSection)
        return PdfExporter.writeReport(
            context = appContext,
            fileName = "lineage_${productId}.pdf",
            docTitle = "Lineage Proof for ${productId}",
            coverBitmap = qr,
            sections = sections
        )
    }

    private fun formatDate(ts: Long): String = try {
        java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(ts))
    } catch (_: Exception) { ts.toString() }
}
