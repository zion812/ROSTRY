package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.ui.collectAsStateWithLifecycle()
    val wizardState = uiState.wizardState
    
    LaunchedEffect(uiState.successProductId) {
        if (uiState.successProductId != null) {
            onNavigateBack()
        }
    }
    
    Column(Modifier.fillMaxSize()) {
        WizardProgressIndicator(wizardState.currentStep)
        
        Box(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            when (wizardState.currentStep) {
                FarmerCreateViewModel.WizardStep.BASICS -> BasicInfoStep(
                    state = wizardState.basicInfo,
                    errors = wizardState.validationErrors,
                    onUpdate = viewModel::updateBasicInfo
                )
                FarmerCreateViewModel.WizardStep.DETAILS -> DetailsStep(
                    ageGroup = wizardState.basicInfo.ageGroup,
                    state = wizardState.detailsInfo,
                    onUpdate = viewModel::updateDetails,
                    onAutoDetectLocation = viewModel::autoDetectLocation,
                    isDetectingLocation = uiState.isSubmitting
                )
                FarmerCreateViewModel.WizardStep.MEDIA -> MediaStep(
                    state = wizardState.mediaInfo,
                    onAddMedia = viewModel::addMedia,
                    onRemoveMedia = viewModel::removeMedia
                )
                FarmerCreateViewModel.WizardStep.REVIEW -> ReviewStep(
                    basicInfo = wizardState.basicInfo,
                    detailsInfo = wizardState.detailsInfo,
                    mediaInfo = wizardState.mediaInfo
                )
            }
        }
        
        WizardNavigationButtons(
            currentStep = wizardState.currentStep,
            isSubmitting = uiState.isSubmitting,
            onBack = viewModel::previousStep,
            onNext = viewModel::nextStep,
            onSubmit = viewModel::submitWizardListing
        )
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
        
        // Dates
        OutlinedTextField(
            value = state.availableFrom,
            onValueChange = { newDate -> onUpdate { it.copy(availableFrom = newDate) } },
            label = { Text("Available From (yyyy-mm-dd) *") },
            isError = errors.containsKey("availableFrom"),
            supportingText = errors["availableFrom"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = state.availableTo,
            onValueChange = { newDate -> onUpdate { it.copy(availableTo = newDate) } },
            label = { Text("Available To (yyyy-mm-dd) *") },
            isError = errors.containsKey("availableTo"),
            supportingText = errors["availableTo"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DetailsStep(
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
    onRemoveMedia: (String, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Media Upload", style = MaterialTheme.typography.titleLarge)
        
        MediaSection("Photos", state.photoUris.size, 12, onAddMedia, onRemoveMedia, "photo")
        MediaSection("Videos", state.videoUris.size, 2, onAddMedia, onRemoveMedia, "video")
        MediaSection("Audio", state.audioUris.size, 5, onAddMedia, onRemoveMedia, "audio")
        MediaSection("Documents", state.documentUris.size, 10, onAddMedia, onRemoveMedia, "document")
    }
}

@Composable
private fun MediaSection(
    label: String,
    count: Int,
    maxCount: Int,
    onAdd: (String, List<String>) -> Unit,
    onRemove: (String, Int) -> Unit,
    type: String
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$label ($count/$maxCount)", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { /* Launch picker */ },
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
    mediaInfo: FarmerCreateViewModel.MediaInfoState
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
        
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp)) {
                Text("Ready to publish?", style = MaterialTheme.typography.titleMedium)
                Text("Review all information before submitting.")
            }
        }
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
    onSubmit: () -> Unit
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
            Button(onClick = onSubmit, enabled = !isSubmitting) {
                if (isSubmitting) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text("Publish Listing")
            }
        } else {
            Button(onClick = onNext) {
                Text("Next")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, null)
            }
        }
    }
}
