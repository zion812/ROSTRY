package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.rio.rostry.marketplace.location.LocationSearchService
import com.rio.rostry.marketplace.location.LocationService
import com.rio.rostry.utils.location.PlacesUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class FarmerLocationVerificationViewModel @Inject constructor(
    private val search: LocationSearchService,
) : ViewModel() {
    var query by mutableStateOf("")
        private set
    var suggestions by mutableStateOf<List<PlacesUtils.SimplePlace>>(emptyList())
        private set
    var selected by mutableStateOf<PlacesUtils.SimplePlace?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    private var debounceJob: Job? = null

    fun onQueryChange(new: String) {
        query = new
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(250)
            runCatching { search.autocomplete(new) }
                .onSuccess { suggestions = it }
                .onFailure { error = it.message }
        }
    }

    fun onSelect(placeId: String) {
        viewModelScope.launch {
            runCatching { search.getPlaceDetails(placeId) }
                .onSuccess { selected = it }
                .onFailure { error = it.message }
        }
    }
}

@Composable
fun FarmerLocationVerificationScreen(
    vm: FarmerLocationVerificationViewModel,
    onVerified: (lat: Double, lng: Double, address: String?) -> Unit,
    fallbackValidate: (lat: Double, lng: Double) -> Boolean = { _, _ -> true }
) {
    val query by remember { derivedStateOf { vm.query } }
    val suggestions by remember { derivedStateOf { vm.suggestions } }
    val selected by remember { derivedStateOf { vm.selected } }
    val error by remember { derivedStateOf { vm.error } }

    Column(Modifier.padding(16.dp)) {
        Text(text = "Farmer Location Verification", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = vm::onQueryChange,
            label = { Text("Search address or place") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        if (suggestions.isNotEmpty()) {
            suggestions.take(5).forEach { p ->
                ElevatedCard(onClick = { p.id?.let(vm::onSelect) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.name ?: p.formattedAddress ?: "Unknown")
                        if (p.formattedAddress != null) Text(p.formattedAddress!!, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        if (selected != null) {
            Spacer(Modifier.height(12.dp))
            Text("Selected: ${selected?.name ?: selected?.formattedAddress}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val lat = selected?.lat
                    val lng = selected?.lng
                    if (lat != null && lng != null) {
                        onVerified(lat, lng, selected?.formattedAddress)
                    }
                }) { Text("Verify") }
                OutlinedButton(onClick = { vm.onQueryChange("") }) { Text("Change") }
            }
        }
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text("Error: ${error}", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        Text("If Places is unavailable, you can proceed with manual coordinates.")
    }
}
