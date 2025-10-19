package com.rio.rostry.ui.onboarding
  
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.utils.validation.OnboardingValidator
import androidx.compose.material3.OutlinedButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.rio.rostry.utils.notif.FarmNotifier
import com.rio.rostry.ui.scan.QrScannerScreen
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.core.content.ContextCompat
  
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardFarmBatchScreen(
    onNavigateRoute: (String) -> Unit,
    onBack: () -> Unit,
    role: String? = null,
    vm: OnboardFarmBatchViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val ctx = androidx.compose.ui.platform.LocalContext.current
  
    LaunchedEffect(role) { role?.let { vm.setRole(it) } }
  
    // Observe navigation events and route out
    LaunchedEffect(Unit) {
        vm.navigationEvent.collect { route ->
            onNavigateRoute(route)
        }
    }
  
    // Camera permission handling
    val cameraPermissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    val showRationaleDialog = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) showRationaleDialog.value = true
    }

    fun validateCurrent(state: OnboardFarmBatchViewModel.WizardState): Map<String, String> {
        return when (state.currentStep) {
            OnboardFarmBatchViewModel.WizardStep.PATH_SELECTION -> OnboardingValidator.validatePathSelection(state.isTraceable)
            OnboardFarmBatchViewModel.WizardStep.AGE_GROUP -> OnboardingValidator.validateAgeGroup(state.ageGroup)
            OnboardFarmBatchViewModel.WizardStep.BATCH_DETAILS -> {
                val e = mutableMapOf<String, String>()
                if (state.batchDetails.batchName.isBlank()) e["batchName"] = "Batch name is required"
                val count = state.batchDetails.count.toIntOrNull() ?: 0
                if (count < 2) e["count"] = "Count must be at least 2"
                if (state.batchDetails.hatchDate == null) e["hatchDate"] = "Hatch date required"
                e
            }
            OnboardFarmBatchViewModel.WizardStep.LINEAGE -> OnboardingValidator.validateLineage(
                OnboardingValidator.LineageState(state.lineage.maleParentId, state.lineage.femaleParentId),
                state.isTraceable == true
            )
            OnboardFarmBatchViewModel.WizardStep.PROOFS -> OnboardingValidator.validateMedia(
                OnboardingValidator.MediaState(state.media.photoUris, state.media.videoUris, state.media.documentUris),
                state.isTraceable == true
            )
            OnboardFarmBatchViewModel.WizardStep.REVIEW -> emptyMap()
        }
    }
  
    val currentErrors = validateCurrent(state)
    val canProceed = currentErrors.isEmpty()
  
    var showScanner by remember { mutableStateOf(false) }
    var scannerGender by remember { mutableStateOf("") }
  
    Scaffold(topBar = { TopAppBar(title = { Text("Onboard Batch") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val totalSteps = if (state.isTraceable == true) 6 else 5
            val currentIndex = when (state.currentStep) {
                OnboardFarmBatchViewModel.WizardStep.PATH_SELECTION -> 1
                OnboardFarmBatchViewModel.WizardStep.AGE_GROUP -> 2
                OnboardFarmBatchViewModel.WizardStep.BATCH_DETAILS -> 3
                OnboardFarmBatchViewModel.WizardStep.LINEAGE -> 4
                OnboardFarmBatchViewModel.WizardStep.PROOFS -> if (state.isTraceable == true) 5 else 4
                OnboardFarmBatchViewModel.WizardStep.REVIEW -> totalSteps
            }
            LinearProgressIndicator(
                progress = currentIndex / totalSteps.toFloat(),
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Onboarding progress: step $currentIndex of $totalSteps" }
            )
            when (state.currentStep) {
                OnboardFarmBatchViewModel.WizardStep.PATH_SELECTION -> {
                    Text("Select Path")
                    val enthusiast = role.equals("enthusiast", ignoreCase = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (state.isTraceable) {
                            true -> {
                                Button(onClick = { vm.updateIsTraceable(true) }, modifier = Modifier.semantics { contentDescription = "Select traceable path" }) { Text("Traceable") }
                                OutlinedButton(onClick = { if (!enthusiast) vm.updateIsTraceable(false) }, enabled = !enthusiast, modifier = Modifier.semantics { contentDescription = "Select non-traceable path" }) { Text("Non-Traceable") }
                            }
                            false -> {
                                OutlinedButton(onClick = { vm.updateIsTraceable(true) }, modifier = Modifier.semantics { contentDescription = "Select traceable path" }) { Text("Traceable") }
                                Button(onClick = { if (!enthusiast) vm.updateIsTraceable(false) }, enabled = !enthusiast, modifier = Modifier.semantics { contentDescription = "Select non-traceable path" }) { Text("Non-Traceable") }
                            }
                            null -> {
                                OutlinedButton(onClick = { vm.updateIsTraceable(true) }, modifier = Modifier.semantics { contentDescription = "Select traceable path" }) { Text("Traceable") }
                                OutlinedButton(onClick = { if (!enthusiast) vm.updateIsTraceable(false) }, enabled = !enthusiast, modifier = Modifier.semantics { contentDescription = "Select non-traceable path" }) { Text("Non-Traceable") }
                            }
                        }
                    }
                    currentErrors["path"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.AGE_GROUP -> {
                    Text("Select Age Group")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val sel = state.ageGroup
                        if (sel == OnboardingValidator.AgeGroup.CHICK) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.CHICK) }, modifier = Modifier.semantics { contentDescription = "Select chick age group" }) { Text("Chick") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.CHICK) }, modifier = Modifier.semantics { contentDescription = "Select chick age group" }) { Text("Chick") }
                        
                        if (sel == OnboardingValidator.AgeGroup.ADULT) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.ADULT) }, modifier = Modifier.semantics { contentDescription = "Select adult age group" }) { Text("Adult") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.ADULT) }, modifier = Modifier.semantics { contentDescription = "Select adult age group" }) { Text("Adult") }
                        
                        if (sel == OnboardingValidator.AgeGroup.BREEDER) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.BREEDER) }, modifier = Modifier.semantics { contentDescription = "Select breeder age group" }) { Text("Breeder") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.BREEDER) }, modifier = Modifier.semantics { contentDescription = "Select breeder age group" }) { Text("Breeder") }
                    }
                    currentErrors["ageGroup"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.BATCH_DETAILS -> {
                    Text("Batch Details")
                    OutlinedTextField(
                        value = state.batchDetails.batchName,
                        onValueChange = { text -> vm.updateBatchDetails { bd -> bd.copy(batchName = text) } },
                        label = { Text("Batch Name") },
                        singleLine = true,
                        modifier = Modifier.semantics { contentDescription = "Batch name" }
                    )
                    currentErrors["batchName"]?.let { Text(it) }
                    OutlinedTextField(
                        value = state.batchDetails.count,
                        onValueChange = { text -> vm.updateBatchDetails { bd -> bd.copy(count = text.filter { it.isDigit() }) } },
                        label = { Text("Count (>= 2)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.semantics { contentDescription = "Batch count" }
                    )
                    currentErrors["count"]?.let { Text(it) }
                    OutlinedTextField(
                        value = state.batchDetails.breed,
                        onValueChange = { text -> vm.updateBatchDetails { bd -> bd.copy(breed = text) } },
                        label = { Text("Breed") },
                        singleLine = true,
                        modifier = Modifier.semantics { contentDescription = "Batch breed" }
                    )
                    // Hatch date picker
                    val showDate = remember { mutableStateOf(false) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showDate.value = true }, modifier = Modifier.semantics { contentDescription = "Pick hatch date" }) {
                            val d = state.batchDetails.hatchDate?.let { java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(it)) } ?: "Pick Hatch Date"
                            Text(d)
                        }
                        if (showDate.value) {
                            val now = java.util.Calendar.getInstance()
                            android.app.DatePickerDialog(
                                androidx.compose.ui.platform.LocalContext.current,
                                { _, y, m, day ->
                                    val cal = java.util.Calendar.getInstance(); cal.set(y, m, day, 0, 0, 0); cal.set(java.util.Calendar.MILLISECOND, 0)
                                    vm.updateBatchDetails { bd -> bd.copy(hatchDate = cal.timeInMillis) }
                                    showDate.value = false
                                },
                                now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    }
                    currentErrors["hatchDate"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.LINEAGE -> {
                    Text("Lineage - select parents (optional scaffold)")
                    val showMale = remember { mutableStateOf(false) }
                    val showFemale = remember { mutableStateOf(false) }
                    val males by vm.availableMaleParents.collectAsState()
                    val females by vm.availableFemaleParents.collectAsState()
                    LaunchedEffect(Unit) { vm.loadAvailableParents() }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showMale.value = true }, modifier = Modifier.semantics { contentDescription = "Select male parent" }) { Text("Select Male Parent") }
                        Button(onClick = {
                            if (cameraPermissionGranted) { scannerGender = "male"; showScanner = true } else permissionLauncher.launch(Manifest.permission.CAMERA)
                        }, modifier = Modifier.semantics { contentDescription = "Scan QR for male parent" }, enabled = cameraPermissionGranted) { Text("Scan Male QR") }
                        Button(onClick = { showFemale.value = true }, modifier = Modifier.semantics { contentDescription = "Select female parent" }) { Text("Select Female Parent") }
                        Button(onClick = {
                            if (cameraPermissionGranted) { scannerGender = "female"; showScanner = true } else permissionLauncher.launch(Manifest.permission.CAMERA)
                        }, modifier = Modifier.semantics { contentDescription = "Scan QR for female parent" }, enabled = cameraPermissionGranted) { Text("Scan Female QR") }
                        val male = vm.state.collectAsState().value.lineage.maleParentId
                        val female = vm.state.collectAsState().value.lineage.femaleParentId
                        if (!male.isNullOrBlank()) OutlinedButton(onClick = { vm.updateLineage(null, vm.state.value.lineage.femaleParentId) }, modifier = Modifier.semantics { contentDescription = "Remove male parent" }) { Text("Male: ${male.take(6)}…") }
                        if (!female.isNullOrBlank()) OutlinedButton(onClick = { vm.updateLineage(vm.state.value.lineage.maleParentId, null) }, modifier = Modifier.semantics { contentDescription = "Remove female parent" }) { Text("Female: ${female.take(6)}…") }
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
                            onScanQr = { /* navigate to scanner if needed */ }
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
                            onScanQr = { /* navigate to scanner if needed */ }
                        )
                    }
                    currentErrors["lineage"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.PROOFS -> {
                    Text("Proofs & Media")
                    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                        if (uris != null) vm.updateMedia { m -> m.copy(photoUris = uris.map { it.toString() }) }
                    }
                    val pickDocs = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                        if (uris != null) vm.updateMedia { m -> m.copy(documentUris = uris.map { it.toString() }) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { pickImages.launch("image/*") }, modifier = Modifier.semantics { contentDescription = "Pick photos for batch proofs" }) { Text("Pick Photos") }
                        OutlinedButton(onClick = { pickDocs.launch("application/pdf") }, modifier = Modifier.semantics { contentDescription = "Pick documents for batch proofs" }) { Text("Pick Documents") }
                    }
                    val photos = state.media.photoUris.size
                    val docs = state.media.documentUris.size
                    Text("Selected: $photos photos, $docs documents")
                    currentErrors["photos"]?.let { Text(it) }
                    currentErrors["documents"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.REVIEW -> {
                    Text("Review & Submit")
                    Text("Batch: ${state.batchDetails.batchName}")
                    Text("Count: ${state.batchDetails.count}")
                }
            }
  
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.previousStep(onBack) }, modifier = Modifier.semantics { contentDescription = "Go to previous step" }) { Text("Back") }
                if (state.currentStep == OnboardFarmBatchViewModel.WizardStep.REVIEW) {
                    Button(onClick = { vm.save() }, enabled = !state.saving, modifier = Modifier.semantics { contentDescription = "Submit batch onboarding" }) { Text("Submit") }
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
        }
    }
  
    if (showScanner) {
        QrScannerScreen(
            onResult = { productId ->
                vm.applyScannedParent(productId, scannerGender)
                showScanner = false
            },
            onValidate = { true },
            hint = "Scan QR code for $scannerGender parent",
            onError = { error -> vm.setError(error) }
        )
    }

    // Rationale dialog for camera permission
    if (showRationaleDialog.value) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog.value = false },
            title = { Text("Camera Permission Required") },
            text = { Text("To scan QR codes for parent selection, camera permission is needed. You can also enter the product ID manually.") },
            confirmButton = {
                TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA); showRationaleDialog.value = false }) { Text("Grant Permission") }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog.value = false }) { Text("Manual Entry") }
            }
        )
    }
}
