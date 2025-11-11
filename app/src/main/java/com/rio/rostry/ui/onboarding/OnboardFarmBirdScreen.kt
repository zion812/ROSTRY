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
                            awards = state.coreDetails.awards
                        )
                        val ageGroup = state.ageGroup ?: OnboardingValidator.AgeGroup.CHICK
                        val isTraceable = state.isTraceable ?: false
                        OnboardingValidator.validateCoreDetails(details, ageGroup, isTraceable)
                    } else mapOf("step" to "Select previous options")
                    OutlinedTextField(
                        value = state.coreDetails.name,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(name = text) } },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.semantics { contentDescription = "Enter bird name" }
                    )
                    coreErrors["name"]?.let { Text(it) }
                    // Birth date picker
                    val showDate = remember { mutableStateOf(false) }
                    
                    // Show date picker dialog when showDate is true
                    if (showDate.value) {
                        val now = java.util.Calendar.getInstance()
                        LaunchedEffect(Unit) {
                            android.app.DatePickerDialog(
                                ctx,
                                { _, y, m, day ->
                                    val cal = java.util.Calendar.getInstance()
                                    cal.set(y, m, day, 0, 0, 0)
                                    cal.set(java.util.Calendar.MILLISECOND, 0)
                                    vm.updateCoreDetails { cd -> cd.copy(birthDate = cal.timeInMillis) }
                                    showDate.value = false
                                },
                                now.get(java.util.Calendar.YEAR), 
                                now.get(java.util.Calendar.MONTH), 
                                now.get(java.util.Calendar.DAY_OF_MONTH)
                            ).apply {
                                setOnCancelListener { showDate.value = false }
                                setOnDismissListener { showDate.value = false }
                            }.show()
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
                    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                        if (uris != null) vm.updateMedia { m -> m.copy(photoUris = uris.map { it.toString() }) }
                    }
                    val pickDocs = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                        if (uris != null) vm.updateMedia { m -> m.copy(documentUris = uris.map { it.toString() }) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { pickImages.launch("image/*") }, modifier = Modifier.semantics { contentDescription = "Pick photos" }) { Text("Pick Photos") }
                        OutlinedButton(onClick = { pickDocs.launch("application/pdf") }, modifier = Modifier.semantics { contentDescription = "Pick documents" }) { Text("Pick Documents") }
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
            // Dynamic per-step gating for Next button based on validator
            val canProceed = when (state.currentStep) {
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
                            awards = state.coreDetails.awards
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.previousStep(onBack) }, modifier = Modifier.semantics { contentDescription = "Go to previous step" }) { Text("Back") }
                if (state.currentStep == OnboardFarmBirdViewModel.WizardStep.REVIEW) {
                    Button(onClick = { vm.save() }, enabled = !state.saving, modifier = Modifier.semantics { contentDescription = "Submit bird onboarding" }) { Text("Submit") }
                } else {
                    Button(onClick = { vm.nextStep() }, enabled = canProceed, modifier = Modifier.semantics { contentDescription = "Go to next step" }) { Text("Next") }
                }
            }
            state.error?.let { Text(it) }

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
