package com.rio.rostry.ui.farmer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.ui.components.SyncStatusBadge
import com.rio.rostry.ui.components.ConflictNotification

// Legacy data classes for compatibility
data class ListingForm(
    val category: Category,
    val traceability: Traceability,
    val ageGroup: AgeGroup,
    val title: String,
    val priceType: PriceType,
    val price: Double?,
    val auctionStartPrice: Double?,
    val availableFrom: String,
    val availableTo: String,
    val healthRecordUri: String?,
    val birthDateMillis: Long? = null,
    val birthPlace: String? = null,
    val vaccinationRecords: String? = null,
    val parentInfo: String? = null,
    val weightGrams: Double? = null,
    val photoUris: List<String> = emptyList(),
    val videoUris: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null
)

enum class Category { Meat, Adoption }
enum class Traceability { Traceable, NonTraceable }
enum class AgeGroup { Chick, Grower, Adult, Senior }
enum class PriceType { Fixed, Auction }

@Composable
fun FarmerCreateScreen(
    viewModel: FarmerCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    prefillProductId: String? = null,
    pairId: String? = null
) {
    val uiState by viewModel.ui.collectAsStateWithLifecycle()
    val wizardState = uiState.wizardState
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Media pickers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> viewModel.addMedia("photo", uris.map { it.toString() }) }
    
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> viewModel.addMedia("video", uris.map { it.toString() }) }
    
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> viewModel.addMedia("audio", uris.map { it.toString() }) }
    
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> viewModel.addMedia("document", uris.map { it.toString() }) }
    
    // Load farm monitoring data if prefillProductId is provided
    LaunchedEffect(prefillProductId) {
        viewModel.loadPrefillData(prefillProductId)
    }
    
    // Load breeding pair context if pairId is provided (enhances listing with breeding stats)
    LaunchedEffect(pairId) {
        viewModel.loadBreedingContext(pairId)
    }
    
    LaunchedEffect(uiState.successProductId) {
        if (uiState.successProductId != null) {
            snackbarHostState.showSnackbar("Listing published successfully")
        }
    }
    
    // Show loading indicator when prefill is in progress
    if (prefillProductId != null && uiState.wizardState.basicInfo.title.isBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CircularProgressIndicator()
                Text("Loading farm data...", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }
    
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
    Column(Modifier.fillMaxSize().padding(padding)) {
        // Conflict notification for listings
        uiState.conflictDetails?.let { c ->
            ConflictNotification(
                conflict = c,
                onDismiss = { viewModel.dismissConflict() },
                onViewDetails = { viewModel.viewConflictDetails(c.entityId) }
            )
        }
        // Enhanced error banner with icon variations
        if (uiState.error != null) {
            val errorMessage = uiState.error!!
            val icon = when {
                errorMessage.contains("quarantine", ignoreCase = true) ||
                errorMessage.contains("deceased", ignoreCase = true) ||
                errorMessage.contains("transferred", ignoreCase = true) -> Icons.Filled.Block
                errorMessage.contains("vaccination", ignoreCase = true) ||
                errorMessage.contains("health log", ignoreCase = true) ||
                errorMessage.contains("daily log", ignoreCase = true) ||
                errorMessage.contains("growth", ignoreCase = true) -> Icons.Filled.Warning
                else -> Icons.Filled.Error
            }
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Column(Modifier.weight(1f)) {
                        Text(
                            errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
            
            // Actionable error cards
            if (errorMessage.contains("quarantine", ignoreCase = true)) {
                ActionCard("Go to Quarantine Management", Routes.MONITORING_QUARANTINE)
            }
            if (errorMessage.contains("vaccination", ignoreCase = true)) {
                ActionCard("Add Vaccination Record", Routes.MONITORING_VACCINATION)
            }
            if (errorMessage.contains("health log", ignoreCase = true) || errorMessage.contains("daily log", ignoreCase = true)) {
                ActionCard("Add Daily Log", Routes.MONITORING_DAILY_LOG)
            }
            if (errorMessage.contains("growth", ignoreCase = true)) {
                ActionCard("Add Growth Record", Routes.MONITORING_GROWTH)
            }
        }
        
        // Show prefill info banner if data was loaded
        if (prefillProductId != null && uiState.error == null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                    Text(
                        "Pre-filled from your farm monitoring data. Review and edit as needed.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // Add pending listings banner
        if (uiState.pendingListingsCount > 0) {
            Card { Text("${uiState.pendingListingsCount} listings pending sync") }
        }
        
        WizardProgressIndicator(wizardState.currentStep)
        
        Box(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            when (wizardState.currentStep) {
                FarmerCreateViewModel.WizardStep.BASICS -> BasicInfoStep(
                    state = wizardState.basicInfo,
                    errors = wizardState.validationErrors,
                    onUpdate = viewModel::updateBasicInfo
                )
                FarmerCreateViewModel.WizardStep.DETAILS -> DetailsStep(
                    category = wizardState.basicInfo.category,
                    traceability = wizardState.basicInfo.traceability,
                    ageGroup = wizardState.basicInfo.ageGroup,
                    state = wizardState.detailsInfo,
                    onUpdate = viewModel::updateDetails,
                    onAutoDetectLocation = viewModel::autoDetectLocation,
                    isDetectingLocation = uiState.isSubmitting
                )
                FarmerCreateViewModel.WizardStep.MEDIA -> MediaStep(
                    state = wizardState.mediaInfo,
                    onAddMedia = viewModel::addMedia,
                    onRemoveMedia = viewModel::removeMedia,
                    photoPickerLauncher = photoPickerLauncher,
                    videoPickerLauncher = videoPickerLauncher,
                    audioPickerLauncher = audioPickerLauncher,
                    documentPickerLauncher = documentPickerLauncher
                )
                FarmerCreateViewModel.WizardStep.REVIEW -> ReviewStep(
                    basicInfo = wizardState.basicInfo,
                    detailsInfo = wizardState.detailsInfo,
                    mediaInfo = wizardState.mediaInfo,
                    validationStatus = uiState.validationStatus, // Assuming added to uiState
                    uiState = uiState
                )
            }
        }
        
        var showConfirm by rememberSaveable { mutableStateOf(false) }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Publish Listing?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Title: ${wizardState.basicInfo.title}")
                        Text("Category: ${wizardState.basicInfo.category}")
                        if (wizardState.basicInfo.priceType == PriceType.Fixed) {
                            Text("Price: ₹${wizardState.basicInfo.price}")
                        } else {
                            Text("Auction start: ₹${wizardState.basicInfo.auctionStartPrice}")
                        }
                        Text("Traceability: ${wizardState.basicInfo.traceability}")
                        Text("Photos: ${wizardState.mediaInfo.photoUris.size} • Videos: ${wizardState.mediaInfo.videoUris.size}")
                        Text("Will sync when online", style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = false
                        viewModel.submitWizardListing()
                    }) { Text("Confirm") }
                },
                dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } }
            )
        }

        WizardNavigationButtons(
            currentStep = wizardState.currentStep,
            isSubmitting = uiState.isSubmitting,
            onBack = viewModel::previousStep,
            onNext = viewModel::nextStep,
            onSubmit = viewModel::submitWizardListing,
            onPublishRequest = { showConfirm = true },
            uiState = uiState
        )
    }
    }
}

@Composable
private fun ActionCard(label: String, route: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = { /* Navigate to route */ }) {
                Icon(Icons.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun WizardProgressIndicator(currentStep: FarmerCreateViewModel.WizardStep) {
    LinearProgressIndicator(
        progress = (currentStep.ordinal + 1) / 4f,
        modifier = Modifier.fillMaxWidth()
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Step ${currentStep.ordinal + 1} of 4: ${currentStep.name}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicInfoStep(
    state: FarmerCreateViewModel.BasicInfoState,
    errors: Map<String, String>,
    onUpdate: ((FarmerCreateViewModel.BasicInfoState) -> FarmerCreateViewModel.BasicInfoState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Basic Information", style = MaterialTheme.typography.titleLarge)
        
        // Category
        Text("Category", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.category == Category.Meat,
                onClick = { onUpdate { it.copy(category = Category.Meat) } },
                label = { Text("Meat") }
            )
            FilterChip(
                selected = state.category == Category.Adoption,
                onClick = { onUpdate { it.copy(category = Category.Adoption) } },
                label = { Text("Adoption") }
            )
        }
        
        // Traceability
        Text("Traceability", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.traceability == Traceability.Traceable,
                onClick = { onUpdate { it.copy(traceability = Traceability.Traceable) } },
                label = { Text("Traceable") }
            )
            FilterChip(
                selected = state.traceability == Traceability.NonTraceable,
                onClick = { onUpdate { it.copy(traceability = Traceability.NonTraceable) } },
                label = { Text("Non-traceable") }
            )
        }
        
        // Age Group
        Text("Age Group", style = MaterialTheme.typography.titleSmall)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AgeGroup.values().forEach { group ->
                FilterChip(
                    selected = state.ageGroup == group,
                    onClick = { onUpdate { it.copy(ageGroup = group) } },
                    label = { Text(group.name) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Title
        OutlinedTextField(
            value = state.title,
            onValueChange = { newTitle -> onUpdate { it.copy(title = newTitle) } },
            label = { Text("Product Title *") },
            isError = errors.containsKey("title"),
            supportingText = errors["title"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Price Type
        Text("Price Type", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.priceType == PriceType.Fixed,
                onClick = { onUpdate { it.copy(priceType = PriceType.Fixed) } },
                label = { Text("Fixed Price") }
            )
            FilterChip(
                selected = state.priceType == PriceType.Auction,
                onClick = { onUpdate { it.copy(priceType = PriceType.Auction) } },
                label = { Text("Auction") }
            )
        }
        
        // Price
        if (state.priceType == PriceType.Fixed) {
            OutlinedTextField(
                value = state.price,
                onValueChange = { newPrice -> onUpdate { it.copy(price = newPrice) } },
                label = { Text("Price (₹) *") },
                isError = errors.containsKey("price"),
                supportingText = errors["price"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = state.auctionStartPrice,
                onValueChange = { newPrice -> onUpdate { it.copy(auctionStartPrice = newPrice) } },
                label = { Text("Starting Price (₹) *") },
                isError = errors.containsKey("auctionStartPrice"),
                supportingText = errors["auctionStartPrice"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Dates with date pickers
        var showFromDatePicker by remember { mutableStateOf(false) }
        var showToDatePicker by remember { mutableStateOf(false) }
        
        OutlinedTextField(
            value = state.availableFrom,
            onValueChange = { },
            label = { Text("Available From (yyyy-mm-dd) *") },
            readOnly = true,
            isError = errors.containsKey("availableFrom"),
            supportingText = errors["availableFrom"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            trailingIcon = {
                IconButton(onClick = { showFromDatePicker = true }) {
                    Icon(Icons.Filled.CalendarToday, "Pick date")
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { showFromDatePicker = true }
        )
        
        OutlinedTextField(
            value = state.availableTo,
            onValueChange = { },
            label = { Text("Available To (yyyy-mm-dd) *") },
            readOnly = true,
            isError = errors.containsKey("availableTo"),
            supportingText = errors["availableTo"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            trailingIcon = {
                IconButton(onClick = { showToDatePicker = true }) {
                    Icon(Icons.Filled.CalendarToday, "Pick date")
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { showToDatePicker = true }
        )
        
        // Date picker dialogs
        if (showFromDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showFromDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            val date = formatter.format(java.util.Date(millis))
                            onUpdate { it.copy(availableFrom = date) }
                        }
                        showFromDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFromDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        if (showToDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showToDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            val date = formatter.format(java.util.Date(millis))
                            onUpdate { it.copy(availableTo = date) }
                        }
                        showToDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showToDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun DetailsStep(
    category: Category,
    traceability: Traceability,
    ageGroup: AgeGroup,
    state: FarmerCreateViewModel.DetailsInfoState,
    onUpdate: ((FarmerCreateViewModel.DetailsInfoState) -> FarmerCreateViewModel.DetailsInfoState) -> Unit,
    onAutoDetectLocation: () -> Unit,
    isDetectingLocation: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Details for ${ageGroup.name}", style = MaterialTheme.typography.titleLarge)
        
        when (ageGroup) {
            AgeGroup.Chick -> {
                OutlinedTextField(
                    value = state.birthPlace,
                    onValueChange = { value -> onUpdate { it.copy(birthPlace = value) } },
                    label = { Text("Birth Place") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.vaccination,
                    onValueChange = { value -> onUpdate { it.copy(vaccination = value) } },
                    label = { Text("Vaccination Records") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.parentInfo,
                    onValueChange = { value -> onUpdate { it.copy(parentInfo = value) } },
                    label = { Text("Parent Info") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AgeGroup.Grower -> {
                OutlinedTextField(
                    value = state.weightText,
                    onValueChange = { value -> onUpdate { it.copy(weightText = value) } },
                    label = { Text("Weight (grams)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.healthUri,
                    onValueChange = { value -> onUpdate { it.copy(healthUri = value) } },
                    label = { Text("Health Records") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AgeGroup.Adult -> {
                OutlinedTextField(
                    value = state.genderText,
                    onValueChange = { value -> onUpdate { it.copy(genderText = value) } },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.sizeText,
                    onValueChange = { value -> onUpdate { it.copy(sizeText = value) } },
                    label = { Text("Size (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.colorPattern,
                    onValueChange = { value -> onUpdate { it.copy(colorPattern = value) } },
                    label = { Text("Color Pattern") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AgeGroup.Senior -> {
                OutlinedTextField(
                    value = state.breedingHistory,
                    onValueChange = { value -> onUpdate { it.copy(breedingHistory = value) } },
                    label = { Text("Breeding History") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = state.geneticTraits,
                    onValueChange = { value -> onUpdate { it.copy(geneticTraits = value) } },
                    label = { Text("Genetic Traits") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.awards,
                    onValueChange = { value -> onUpdate { it.copy(awards = value) } },
                    label = { Text("Awards") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Inline validation hints for traceable adoptions
        if (category == Category.Adoption && traceability == Traceability.Traceable) {
            Text(
                "Vaccination records must be within last 30 days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                "Health logs must be within last 7 days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                "Growth monitoring must be within last 2 weeks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        
        Divider()
        Text("Location", style = MaterialTheme.typography.titleSmall)
        
        Button(
            onClick = onAutoDetectLocation,
            enabled = !isDetectingLocation,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isDetectingLocation) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Detecting...")
            } else {
                Icon(Icons.Filled.LocationOn, null)
                Spacer(Modifier.width(8.dp))
                Text("Auto-detect Location")
            }
        }
        
        if (state.latitude != null && state.longitude != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(12.dp)) {
                    Text("✓ Location captured", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("Lat: ${state.latitude}, Lng: ${state.longitude}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun MediaStep(
    state: FarmerCreateViewModel.MediaInfoState,
    onAddMedia: (String, List<String>) -> Unit,
    onRemoveMedia: (String, Int) -> Unit,
    photoPickerLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    videoPickerLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    audioPickerLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    documentPickerLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Media Upload", style = MaterialTheme.typography.titleLarge)
        
        MediaSection("Photos", state.photoUris.size, 12, { photoPickerLauncher.launch("image/*") }, onRemoveMedia, "photo")
        MediaSection("Videos", state.videoUris.size, 2, { videoPickerLauncher.launch("video/*") }, onRemoveMedia, "video")
        MediaSection("Audio", state.audioUris.size, 5, { audioPickerLauncher.launch("audio/*") }, onRemoveMedia, "audio")
        MediaSection("Documents", state.documentUris.size, 10, { documentPickerLauncher.launch("*/*") }, onRemoveMedia, "document")
    }
}

@Composable
private fun MediaSection(
    label: String,
    count: Int,
    maxCount: Int,
    onLaunchPicker: () -> Unit,
    onRemove: (String, Int) -> Unit,
    type: String
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$label ($count/$maxCount)", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onLaunchPicker,
                enabled = count < maxCount
            ) {
                Icon(Icons.Filled.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add $label")
            }
        }
    }
}

@Composable
private fun ReviewStep(
    basicInfo: FarmerCreateViewModel.BasicInfoState,
    detailsInfo: FarmerCreateViewModel.DetailsInfoState,
    mediaInfo: FarmerCreateViewModel.MediaInfoState,
    validationStatus: Map<String, Boolean>, // Added parameter for validation status
    uiState: FarmerCreateViewModel.UiState
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Review Your Listing", style = MaterialTheme.typography.titleLarge)
        
        SummaryCard("Basic Information") {
            Text("Category: ${basicInfo.category.name}")
            Text("Traceability: ${basicInfo.traceability.name}")
            Text("Age Group: ${basicInfo.ageGroup.name}")
            Text("Title: ${basicInfo.title}")
            Text("Price: ₹${basicInfo.price}")
        }
        
        SummaryCard("Media") {
            Text("Photos: ${mediaInfo.photoUris.size}")
            Text("Videos: ${mediaInfo.videoUris.size}")
        }
        
        // Validation status card
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Validation Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                ValidationItem("Product not in quarantine", validationStatus["notInQuarantine"] ?: false)
                ValidationItem("Recent vaccination (within 30 days)", validationStatus["recentVaccination"] ?: false)
                ValidationItem("Recent health log (within 7 days)", validationStatus["recentHealthLog"] ?: false)
                ValidationItem("Recent growth record (within 2 weeks)", validationStatus["recentGrowthRecord"] ?: false)
            }
        }
        
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp)) {
                Text("Ready to publish?", style = MaterialTheme.typography.titleMedium)
                Text("Review all information before submitting.")
            }
        }
        
        // Add sync status card
        Card {
            Column {
                Text("Sync Status")
                if (uiState.listingSyncState != null) SyncStatusBadge(syncState = uiState.listingSyncState) else Text("Will sync after publish")
            }
        }
    }
}

@Composable
private fun ValidationItem(label: String, isValid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (isValid) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            tint = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SummaryCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun WizardNavigationButtons(
    currentStep: FarmerCreateViewModel.WizardStep,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onPublishRequest: () -> Unit = onSubmit,
    uiState: FarmerCreateViewModel.UiState
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentStep != FarmerCreateViewModel.WizardStep.BASICS) {
            OutlinedButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, null)
                Spacer(Modifier.width(8.dp))
                Text("Back")
            }
        } else {
            Spacer(Modifier)
        }
        
        if (currentStep == FarmerCreateViewModel.WizardStep.REVIEW) {
            Button(onClick = onPublishRequest, enabled = !isSubmitting) {
                if (isSubmitting) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Validating farm data...")
                } else {
                    Text(if (uiState.isOnline) "Publish Listing" else "Queue Listing (Offline)")
                }
            }
        } else {
            Button(onClick = onNext) {
                Text("Next")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, null)
            }
        }
    }
    
    if (!uiState.isOnline) {
        Text("Listing will publish when online", style = MaterialTheme.typography.bodySmall)
    }
}