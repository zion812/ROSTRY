package com.rio.rostry.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.utils.validation.OnboardingValidator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.Manifest
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardFarmBirdScreen(
    onNavigateRoute: (String) -> Unit,
    onBack: () -> Unit,
    role: String? = null,
    vm: OnboardFarmBirdViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val ctx = androidx.compose.ui.platform.LocalContext.current

    // Camera permission handling
    val cameraPermissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    val showRationaleDialog = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            // Permission granted, proceed to show scanner (handled in onClick)
        } else {
            showRationaleDialog.value = true
        }
    }

    LaunchedEffect(role) {
        role?.let { vm.setRole(it) }
    }

    // Observe navigation events and route out
    LaunchedEffect(Unit) {
        vm.navigationEvent.collect { route ->
            onNavigateRoute(route)
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Add Bird") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val totalSteps = if (state.isTraceable == true) 6 else 5
            val currentIndex = when (state.currentStep) {
                OnboardFarmBirdViewModel.WizardStep.PATH_SELECTION -> 1
                OnboardFarmBirdViewModel.WizardStep.AGE_GROUP -> 2
                OnboardFarmBirdViewModel.WizardStep.CORE_DETAILS -> 3
                OnboardFarmBirdViewModel.WizardStep.LINEAGE -> 4
                OnboardFarmBirdViewModel.WizardStep.PROOFS -> if (state.isTraceable == true) 5 else 4
                OnboardFarmBirdViewModel.WizardStep.REVIEW -> totalSteps
            }
            LinearProgressIndicator(progress = currentIndex / totalSteps.toFloat(), modifier = Modifier.fillMaxWidth())

            when (state.currentStep) {
                OnboardFarmBirdViewModel.WizardStep.PATH_SELECTION -> {
                    Text("Select Path")
                    val pathErrors = OnboardingValidator.validatePathSelection(state.isTraceable)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (state.isTraceable) {
                            true -> {
                                Button(onClick = { vm.updateIsTraceable(true) }, modifier = Modifier.semantics { contentDescription = "Select traceable path" }) { Text("Traceable") }
                                OutlinedButton(onClick = { vm.updateIsTraceable(false) }, modifier = Modifier.semantics { contentDescription = "Select non-traceable path" }) { Text("Non-Traceable") }
                            }
                            false -> {
                                OutlinedButton(onClick = { vm.updateIsTraceable(true) }, modifier = Modifier.semantics { contentDescription = "Select traceable path" }) { Text("Traceable") }
                                Button(onClick = { vm.updateIsTraceable(false) }, modifier = Modifier.semantics { contentDescription = "Select non-traceable path" }) { Text("Non-Traceable") }
                            }
                            null -> {
                                OutlinedButton(onClick = { vm.updateIsTraceable(true) }, modifier = Modifier.semantics { contentDescription = "Select traceable path" }) { Text("Traceable") }
                                OutlinedButton(onClick = { vm.updateIsTraceable(false) }, modifier = Modifier.semantics { contentDescription = "Select non-traceable path" }) { Text("Non-Traceable") }
                            }
                        }
                    }
                    pathErrors["path"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.AGE_GROUP -> {
                    Text("Select Age Group")
                    val ageErrors = OnboardingValidator.validateAgeGroup(state.ageGroup)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val sel = state.ageGroup
                        if (sel == OnboardingValidator.AgeGroup.CHICK) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.CHICK) }, modifier = Modifier.semantics { contentDescription = "Select chick age group" }) { Text("Chick") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.CHICK) }, modifier = Modifier.semantics { contentDescription = "Select chick age group" }) { Text("Chick") }

                        if (sel == OnboardingValidator.AgeGroup.JUVENILE) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.JUVENILE) }, modifier = Modifier.semantics { contentDescription = "Select juvenile age group" }) { Text("Juvenile") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.JUVENILE) }, modifier = Modifier.semantics { contentDescription = "Select juvenile age group" }) { Text("Juvenile") }

                        if (sel == OnboardingValidator.AgeGroup.ADULT) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.ADULT) }, modifier = Modifier.semantics { contentDescription = "Select adult age group" }) { Text("Adult") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.ADULT) }, modifier = Modifier.semantics { contentDescription = "Select adult age group" }) { Text("Adult") }

                        if (sel == OnboardingValidator.AgeGroup.BREEDER) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.BREEDER) }, modifier = Modifier.semantics { contentDescription = "Select breeder age group" }) { Text("Breeder") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.BREEDER) }, modifier = Modifier.semantics { contentDescription = "Select breeder age group" }) { Text("Breeder") }
                    }
                    ageErrors["ageGroup"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.CORE_DETAILS -> {
                    Text("Core Details")
                    val coreErrors = if (state.ageGroup != null && state.isTraceable != null) {
                        val details = OnboardingValidator.CoreDetailsState(
                            name = state.coreDetails.name,
                            birthDate = state.coreDetails.birthDate,
                            birthPlace = state.coreDetails.birthPlace,
                            gender = state.coreDetails.gender,
                            weightGrams = state.coreDetails.weightGrams,
                            heightCm = state.coreDetails.heightCm,
                            colors = state.coreDetails.colors,
                            breed = state.coreDetails.breed,
                            physicalId = state.coreDetails.physicalId,
                            vaccinationRecords = state.coreDetails.vaccinationRecords,
                            healthStatus = state.coreDetails.healthStatus,
                            breedingHistory = state.coreDetails.breedingHistory,
                            awards = state.coreDetails.awards,
                            location = state.coreDetails.location,
                            price = state.coreDetails.price,
                            deliveryOptions = state.coreDetails.deliveryOptions
                        )
                        val ageGroup = state.ageGroup ?: OnboardingValidator.AgeGroup.CHICK
                        val isTraceable = state.isTraceable ?: false
                        OnboardingValidator.validateCoreDetails(details, ageGroup, isTraceable, state.coreDetails.listForSale)
                    } else mapOf("step" to "Select previous options")
                    OutlinedTextField(
                        value = state.coreDetails.name,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(name = text) } },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.semantics { contentDescription = "Enter bird name" }
                    )
                    coreErrors["name"]?.let { Text(it) }
                    
                    // Location Selector - Manual Options
                    Text("Location", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val loc = state.coreDetails.location
                        listOf("Coop", "Free Range", "Quarantine").forEach { l ->
                            if (loc == l || loc.startsWith("GPS:") && l == "Coop") {
                                Button(onClick = { vm.updateCoreDetails { it.copy(location = l, latitude = null, longitude = null) } }) { Text(l) }
                            } else {
                                OutlinedButton(onClick = { vm.updateCoreDetails { it.copy(location = l, latitude = null, longitude = null) } }) { Text(l) }
                            }
                        }
                    }
                    
                    // GPS Auto-Detect Location
                    Spacer(Modifier.height(8.dp))
                    Text("Or detect GPS location:", style = MaterialTheme.typography.bodySmall)
                    
                    // Location permission launcher
                    val locationPermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            vm.autoDetectLocation()
                        }
                    }
                    
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                vm.autoDetectLocation()
                            } else {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        enabled = !state.isDetectingLocation,
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Auto-detect GPS location" }
                    ) {
                        if (state.isDetectingLocation) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp).height(18.dp).fillMaxWidth(0.1f),
                                strokeWidth = 2.dp
                            )
                            Text("Detecting...")
                        } else {
                            Text("📍 Auto-Detect My Location")
                        }
                    }

                    // Show detection error if any
                    state.error?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                    
                    // Show detected GPS coordinates
                    if (state.coreDetails.latitude != null && state.coreDetails.longitude != null) {
                        androidx.compose.material3.Card(
                            colors = androidx.compose.material3.CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("✓ Location captured", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "Lat: ${String.format("%.4f", state.coreDetails.latitude)}, Lng: ${String.format("%.4f", state.coreDetails.longitude)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    coreErrors["location"]?.let { Text(it, color = androidx.compose.ui.graphics.Color.Red) }

                    // Birth date picker
                    val showDate = remember { mutableStateOf(false) }
                    
                    // Show date picker dialog when showDate is true
                    if (showDate.value) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = state.coreDetails.birthDate ?: System.currentTimeMillis()
                        )
                        DatePickerDialog(
                            onDismissRequest = { showDate.value = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        vm.updateCoreDetails { cd -> cd.copy(birthDate = millis) }
                                    }
                                    showDate.value = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDate.value = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showDate.value = true }, 
                            modifier = Modifier.semantics { contentDescription = "Pick birth date" }
                        ) {
                            val d = state.coreDetails.birthDate?.let { 
                                java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) 
                            } ?: "Pick Birth Date"
                            Text(d)
                        }
                    }
                    coreErrors["birthDate"]?.let { Text(it, color = androidx.compose.ui.graphics.Color.Red) }
                    // Gender quick selectors
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val selectedGender = state.coreDetails.gender
                        if (selectedGender == "Male") Button(onClick = { vm.updateCoreDetails { it.copy(gender = "Male") } }, modifier = Modifier.semantics { contentDescription = "Select male gender" }) { Text("Male") }
                        else OutlinedButton(onClick = { vm.updateCoreDetails { it.copy(gender = "Male") } }, modifier = Modifier.semantics { contentDescription = "Select male gender" }) { Text("Male") }

                        if (selectedGender == "Female") Button(onClick = { vm.updateCoreDetails { it.copy(gender = "Female") } }, modifier = Modifier.semantics { contentDescription = "Select female gender" }) { Text("Female") }
                        else OutlinedButton(onClick = { vm.updateCoreDetails { it.copy(gender = "Female") } }, modifier = Modifier.semantics { contentDescription = "Select female gender" }) { Text("Female") }

                        if (selectedGender == "Unknown") Button(onClick = { vm.updateCoreDetails { it.copy(gender = "Unknown") } }, modifier = Modifier.semantics { contentDescription = "Select unknown gender" }) { Text("Unknown") }
                        else OutlinedButton(onClick = { vm.updateCoreDetails { it.copy(gender = "Unknown") } }, modifier = Modifier.semantics { contentDescription = "Select unknown gender" }) { Text("Unknown") }
                    }
                    coreErrors["gender"]?.let { Text(it) }
                    OutlinedTextField(
                        value = state.coreDetails.breed,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(breed = text) } },
                        label = { Text("Breed") },
                        singleLine = true,
                        modifier = Modifier.semantics { contentDescription = "Enter bird breed" }
                    )
                    coreErrors["breed"]?.let { Text(it) }
                    // Numeric fields
                    OutlinedTextField(
                        value = state.coreDetails.weightGrams,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(weightGrams = text.filter { it.isDigit() }) } },
                        label = { Text("Weight (grams)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.semantics { contentDescription = "Enter bird weight in grams" }
                    )
                    coreErrors["weightGrams"]?.let { Text(it) }
                    OutlinedTextField(
                        value = state.coreDetails.heightCm,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(heightCm = text.filter { it.isDigit() }) } },
                        label = { Text("Height (cm)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.semantics { contentDescription = "Enter bird height in cm" }
                    )
                    // Conditional fields based on age group (mirror validator expectations)
                    when (state.ageGroup) {
                        OnboardingValidator.AgeGroup.CHICK -> {
                            OutlinedTextField(
                                value = state.coreDetails.birthPlace,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(birthPlace = t) } },
                                label = { Text("Birth place") },
                                singleLine = true,
                                modifier = Modifier.semantics { contentDescription = "Enter birth place" }
                            )
                            coreErrors["birthPlace"]?.let { Text(it) }
                            OutlinedTextField(
                                value = state.coreDetails.vaccinationRecords,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(vaccinationRecords = t) } },
                                label = { Text("Vaccination records") },
                                modifier = Modifier.semantics { contentDescription = "Enter vaccination records" }
                            )
                            coreErrors["vaccinationRecords"]?.let { Text(it) }
                        }
                        OnboardingValidator.AgeGroup.ADULT -> {
                            OutlinedTextField(
                                value = state.coreDetails.physicalId,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(physicalId = t) } },
                                label = { Text("Physical ID (tag/ring)") },
                                singleLine = true,
                                modifier = Modifier.semantics { contentDescription = "Enter physical ID" }
                            )
                            coreErrors["physicalId"]?.let { Text(it) }
                        }
                        OnboardingValidator.AgeGroup.BREEDER -> {
                            OutlinedTextField(
                                value = state.coreDetails.breedingHistory,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(breedingHistory = t) } },
                                label = { Text("Breeding history") },
                                modifier = Modifier.semantics { contentDescription = "Enter breeding history" }
                            )
                            coreErrors["breedingHistory"]?.let { Text(it) }
                        }
                        else -> {}
                    }
                    
                    // List for Sale Toggle Section
                    Spacer(Modifier.height(16.dp))
                    androidx.compose.material3.Divider()
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("List for Sale?", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Enable to set price and delivery options",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        androidx.compose.material3.Switch(
                            checked = state.coreDetails.listForSale,
                            onCheckedChange = { listForSale ->
                                vm.updateCoreDetails { it.copy(listForSale = listForSale) }
                            }
                        )
                    }
                    
                    // Conditional Delivery Options - Only shown when List for Sale is enabled
                    if (state.coreDetails.listForSale) {
                        Spacer(Modifier.height(16.dp))
                        
                        // Price field
                        OutlinedTextField(
                            value = state.coreDetails.price?.toString() ?: "",
                            onValueChange = { text ->
                                vm.updateCoreDetails { it.copy(price = text.toDoubleOrNull()) }
                            },
                            label = { Text("Price (₹)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter price" }
                        )
                        coreErrors["price"]?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        
                        Spacer(Modifier.height(12.dp))
                        Text("Delivery Options", style = MaterialTheme.typography.labelLarge)
                        
                        // Delivery options chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val allDeliveryOptions = listOf("SELF_PICKUP", "FARMER_DELIVERY")
                            allDeliveryOptions.forEach { option ->
                                androidx.compose.material3.FilterChip(
                                    selected = state.coreDetails.deliveryOptions.contains(option),
                                    onClick = {
                                        val updatedOptions = if (state.coreDetails.deliveryOptions.contains(option)) {
                                            state.coreDetails.deliveryOptions - option
                                        } else {
                                            state.coreDetails.deliveryOptions + option
                                        }
                                        vm.updateCoreDetails { it.copy(deliveryOptions = updatedOptions) }
                                    },
                                    label = { Text(option.replace("_", " ")) }
                                )
                            }
                        }
                        coreErrors["deliveryOptions"]?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Delivery cost
                        OutlinedTextField(
                            value = state.coreDetails.deliveryCost?.toString() ?: "",
                            onValueChange = { text ->
                                vm.updateCoreDetails { it.copy(deliveryCost = text.toDoubleOrNull()) }
                            },
                            label = { Text("Delivery Cost (₹)") },
                            placeholder = { Text("0.00") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter delivery cost" }
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Lead time days
                        OutlinedTextField(
                            value = state.coreDetails.leadTimeDays?.toString() ?: "",
                            onValueChange = { text ->
                                vm.updateCoreDetails { it.copy(leadTimeDays = text.toIntOrNull()) }
                            },
                            label = { Text("Lead Time (days)") },
                            placeholder = { Text("Number of days notice required") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter lead time days" }
                        )
                    }
                }
                OnboardFarmBirdViewModel.WizardStep.LINEAGE -> {
                    Text("Lineage - select parents (optional scaffold)")
                    val lineageErrors = OnboardingValidator.validateLineage(
                        OnboardingValidator.LineageState(maleParentId = state.lineage.maleParentId, femaleParentId = state.lineage.femaleParentId),
                        state.isTraceable == true
                    )
                    val showMale = remember { mutableStateOf(false) }
                    val showFemale = remember { mutableStateOf(false) }
                    val showScanMale = remember { mutableStateOf(false) }
                    val showScanFemale = remember { mutableStateOf(false) }
                    val males by vm.availableMaleParents.collectAsState()
                    val females by vm.availableFemaleParents.collectAsState()
                    LaunchedEffect(Unit) { vm.loadAvailableParents() }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showMale.value = true }, modifier = Modifier.semantics { contentDescription = "Select male parent" }) { Text("Select Male Parent") }
                        Button(onClick = { showFemale.value = true }, modifier = Modifier.semantics { contentDescription = "Select female parent" }) { Text("Select Female Parent") }
                        OutlinedButton(
                            onClick = {
                                if (cameraPermissionGranted) showScanMale.value = true else permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            enabled = cameraPermissionGranted,
                            modifier = Modifier.semantics { contentDescription = "Scan QR for male parent" }
                        ) { Text("Scan Male QR") }
                        OutlinedButton(
                            onClick = {
                                if (cameraPermissionGranted) showScanFemale.value = true else permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            enabled = cameraPermissionGranted,
                            modifier = Modifier.semantics { contentDescription = "Scan QR for female parent" }
                        ) { Text("Scan Female QR") }
                    }
                    if (showMale.value) {
                        ParentSelectorDialog(
                            availableParents = males,
                            gender = "male",
                            onDismiss = { showMale.value = false },
                            onSelectParent = {
                                vm.selectMaleParent(it)
                                showMale.value = false
                            },
                            onScanQr = { showScanMale.value = true }
                        )
                    }
                    if (showFemale.value) {
                        ParentSelectorDialog(
                            availableParents = females,
                            gender = "female",
                            onDismiss = { showFemale.value = false },
                            onSelectParent = {
                                vm.selectFemaleParent(it)
                                showFemale.value = false
                            },
                            onScanQr = { showScanFemale.value = true }
                        )
                    }
                    if (showScanMale.value) {
                        com.rio.rostry.ui.scan.QrScannerScreen(onResult = { pid ->
                            vm.applyScannedParent(pid, "male"); showScanMale.value = false
                        })
                    }
                    if (showScanFemale.value) {
                        com.rio.rostry.ui.scan.QrScannerScreen(onResult = { pid ->
                            vm.applyScannedParent(pid, "female"); showScanFemale.value = false
                        })
                    }
                    lineageErrors["lineage"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.PROOFS -> {
                    Text("Proofs & Media (picker to be integrated)")
                    val mediaErrors = OnboardingValidator.validateMedia(
                        OnboardingValidator.MediaState(
                            photoUris = state.media.photoUris,
                            videoUris = state.media.videoUris,
                            documentUris = state.media.documentUris
                        ),
                        state.isTraceable == true
                    )
                    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
                        uris.forEach { uri ->
                            try {
                                ctx.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            } catch (e: Exception) { }
                        }
                        if (uris != null) vm.updateMedia { m -> m.copy(photoUris = uris.map { it.toString() }) }
                    }
                    val pickDocs = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
                        uris.forEach { uri ->
                            try {
                                ctx.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            } catch (e: Exception) { }
                        }
                        if (uris != null) vm.updateMedia { m -> m.copy(documentUris = uris.map { it.toString() }) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { pickImages.launch(arrayOf("image/*")) }, modifier = Modifier.semantics { contentDescription = "Pick photos" }) { Text("Pick Photos") }
                        OutlinedButton(onClick = { pickDocs.launch(arrayOf("application/pdf")) }, modifier = Modifier.semantics { contentDescription = "Pick documents" }) { Text("Pick Documents") }
                    }
                    mediaErrors["photos"]?.let { Text(it) }
                    mediaErrors["documents"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.REVIEW -> {
                    Text("Review & Submit")
                    Text("Name: ${state.coreDetails.name}")
                    Text("Age Group: ${state.ageGroup}")
                }
            }

            Spacer(Modifier.height(8.dp))
            // Memoized validation to prevent recalculation on every recomposition
            val canProceed = remember(state.currentStep, state.isTraceable, state.ageGroup, state.coreDetails, state.lineage, state.media) {
                when (state.currentStep) {
                    OnboardFarmBirdViewModel.WizardStep.PATH_SELECTION -> OnboardingValidator.validatePathSelection(state.isTraceable).isEmpty()
                    OnboardFarmBirdViewModel.WizardStep.AGE_GROUP -> OnboardingValidator.validateAgeGroup(state.ageGroup).isEmpty()
                    OnboardFarmBirdViewModel.WizardStep.CORE_DETAILS -> {
                        if (state.ageGroup != null && state.isTraceable != null) {
                            val details = OnboardingValidator.CoreDetailsState(
                                name = state.coreDetails.name,
                                birthDate = state.coreDetails.birthDate,
                                birthPlace = state.coreDetails.birthPlace,
                                gender = state.coreDetails.gender,
                                weightGrams = state.coreDetails.weightGrams,
                                heightCm = state.coreDetails.heightCm,
                                colors = state.coreDetails.colors,
                                breed = state.coreDetails.breed,
                                physicalId = state.coreDetails.physicalId,
                                vaccinationRecords = state.coreDetails.vaccinationRecords,
                                healthStatus = state.coreDetails.healthStatus,
                                breedingHistory = state.coreDetails.breedingHistory,
                                awards = state.coreDetails.awards,
                                location = state.coreDetails.location
                            )
                            val ageGroup2 = state.ageGroup ?: OnboardingValidator.AgeGroup.CHICK
                            val isTraceable2 = state.isTraceable ?: false
                            OnboardingValidator.validateCoreDetails(details, ageGroup2, isTraceable2).isEmpty()
                        } else false
                    }
                    OnboardFarmBirdViewModel.WizardStep.LINEAGE -> OnboardingValidator.validateLineage(
                        OnboardingValidator.LineageState(maleParentId = state.lineage.maleParentId, femaleParentId = state.lineage.femaleParentId),
                        state.isTraceable == true
                    ).isEmpty()
                    OnboardFarmBirdViewModel.WizardStep.PROOFS -> OnboardingValidator.validateMedia(
                        OnboardingValidator.MediaState(
                            photoUris = state.media.photoUris,
                            videoUris = state.media.videoUris,
                            documentUris = state.media.documentUris
                        ),
                        state.isTraceable == true
                    ).isEmpty()
                    OnboardFarmBirdViewModel.WizardStep.REVIEW -> true
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.previousStep(onBack) }, enabled = !state.saving, modifier = Modifier.semantics { contentDescription = "Go to previous step" }) { Text("Back") }
                if (state.currentStep == OnboardFarmBirdViewModel.WizardStep.REVIEW) {
                    Button(
                        onClick = { vm.save() },
                        enabled = !state.saving,
                        modifier = Modifier.semantics { contentDescription = "Submit bird onboarding" }
                    ) {
                        if (state.saving) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp).height(18.dp).fillMaxWidth(0.1f),
                                strokeWidth = 2.dp
                            )
                        }
                        Text(if (state.saving) "Saving..." else "Submit")
                    }
                } else {
                    Button(onClick = { vm.nextStep() }, enabled = canProceed, modifier = Modifier.semantics { contentDescription = "Go to next step" }) { Text("Next") }
                }
            }
            state.error?.let { errorMsg ->
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            state.warning?.let { warning ->
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Upgrade Recommended",
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = warning,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                        )
                        TextButton(
                            onClick = { onNavigateRoute(com.rio.rostry.ui.navigation.Routes.Builders.upgradeWizard(com.rio.rostry.domain.model.UserType.ENTHUSIAST)) }
                        ) {
                            Text("Upgrade Role")
                        }
                    }
                }
            }

            // Upload status banner
            val pendingOrUploading = state.uploadStatus.values.count { it == "PENDING" || it == "UPLOADING" }
            val successCount = state.uploadStatus.values.count { it == "SUCCESS" }
            if (pendingOrUploading > 0) {
                Text("Uploading media: $pendingOrUploading pending/uploading, $successCount succeeded")
            }

            // Rationale dialog for camera permission
            if (showRationaleDialog.value) {
                AlertDialog(
                    onDismissRequest = { showRationaleDialog.value = false },
                    title = { Text("Camera Permission Required") },
                    text = { Text("To scan QR codes for parent selection, camera permission is needed. You can also enter the product ID manually.") },
                    confirmButton = {
                        TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA); showRationaleDialog.value = false }) {
                            Text("Grant Permission")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRationaleDialog.value = false }) {
                            Text("Manual Entry")
                        }
                    }
                )
            }
        }
    }
}
