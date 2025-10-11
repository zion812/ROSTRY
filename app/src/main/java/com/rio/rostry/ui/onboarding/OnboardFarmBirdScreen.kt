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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.utils.validation.OnboardingValidator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardFarmBirdScreen(
    onDone: (String) -> Unit,
    onBack: () -> Unit,
    role: String? = null,
    vm: OnboardFarmBirdViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val ctx = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(state.savedId) {
        state.savedId?.let { id ->
            // Optional success notification with deep link to daily log
            com.rio.rostry.utils.notif.FarmNotifier.ensureChannel(ctx)
            com.rio.rostry.utils.notif.FarmNotifier.notifyBirdOnboarded(ctx, state.coreDetails.name.ifBlank { id }, id)
            onDone(id)
        }
    }

    LaunchedEffect(role) {
        role?.let { vm.setRole(it) }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Add Bird") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (state.isTraceable) {
                            true -> {
                                Button(onClick = { vm.updateIsTraceable(true) }) { Text("Traceable") }
                                OutlinedButton(onClick = { vm.updateIsTraceable(false) }) { Text("Non-Traceable") }
                            }
                            false -> {
                                OutlinedButton(onClick = { vm.updateIsTraceable(true) }) { Text("Traceable") }
                                Button(onClick = { vm.updateIsTraceable(false) }) { Text("Non-Traceable") }
                            }
                            null -> {
                                OutlinedButton(onClick = { vm.updateIsTraceable(true) }) { Text("Traceable") }
                                OutlinedButton(onClick = { vm.updateIsTraceable(false) }) { Text("Non-Traceable") }
                            }
                        }
                    }
                    state.validationErrors["path"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.AGE_GROUP -> {
                    Text("Select Age Group")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val sel = state.ageGroup
                        if (sel == OnboardingValidator.AgeGroup.CHICK) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.CHICK) }) { Text("Chick") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.CHICK) }) { Text("Chick") }

                        if (sel == OnboardingValidator.AgeGroup.JUVENILE) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.JUVENILE) }) { Text("Juvenile") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.JUVENILE) }) { Text("Juvenile") }

                        if (sel == OnboardingValidator.AgeGroup.ADULT) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.ADULT) }) { Text("Adult") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.ADULT) }) { Text("Adult") }

                        if (sel == OnboardingValidator.AgeGroup.BREEDER) Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.BREEDER) }) { Text("Breeder") }
                        else OutlinedButton(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.BREEDER) }) { Text("Breeder") }
                    }
                    state.validationErrors["ageGroup"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.CORE_DETAILS -> {
                    Text("Core Details")
                    OutlinedTextField(
                        value = state.coreDetails.name,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(name = text) } },
                        label = { Text("Name") },
                        singleLine = true
                    )
                    // Birth date picker
                    val showDate = remember { mutableStateOf(false) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showDate.value = true }) {
                            val d = state.coreDetails.birthDate?.let { java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(it)) } ?: "Pick Birth Date"
                            Text(d)
                        }
                        if (showDate.value) {
                            val now = java.util.Calendar.getInstance()
                            android.app.DatePickerDialog(
                                ctx,
                                { _, y, m, day ->
                                    val cal = java.util.Calendar.getInstance(); cal.set(y, m, day, 0, 0, 0); cal.set(java.util.Calendar.MILLISECOND, 0)
                                    vm.updateCoreDetails { cd -> cd.copy(birthDate = cal.timeInMillis) }
                                    showDate.value = false
                                },
                                now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    }
                    // Gender quick selectors
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val selectedGender = state.coreDetails.gender
                        if (selectedGender == "Male") Button(onClick = { vm.updateCoreDetails { it.copy(gender = "Male") } }) { Text("Male") }
                        else OutlinedButton(onClick = { vm.updateCoreDetails { it.copy(gender = "Male") } }) { Text("Male") }
                        
                        if (selectedGender == "Female") Button(onClick = { vm.updateCoreDetails { it.copy(gender = "Female") } }) { Text("Female") }
                        else OutlinedButton(onClick = { vm.updateCoreDetails { it.copy(gender = "Female") } }) { Text("Female") }
                        
                        if (selectedGender == "Unknown") Button(onClick = { vm.updateCoreDetails { it.copy(gender = "Unknown") } }) { Text("Unknown") }
                        else OutlinedButton(onClick = { vm.updateCoreDetails { it.copy(gender = "Unknown") } }) { Text("Unknown") }
                    }
                    OutlinedTextField(
                        value = state.coreDetails.breed,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(breed = text) } },
                        label = { Text("Breed") },
                        singleLine = true
                    )
                    // Numeric fields
                    OutlinedTextField(
                        value = state.coreDetails.weightGrams,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(weightGrams = text.filter { it.isDigit() }) } },
                        label = { Text("Weight (grams)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = state.coreDetails.heightCm,
                        onValueChange = { text -> vm.updateCoreDetails { cd -> cd.copy(heightCm = text.filter { it.isDigit() }) } },
                        label = { Text("Height (cm)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    // Conditional fields based on age group (mirror validator expectations)
                    when (state.ageGroup) {
                        OnboardingValidator.AgeGroup.CHICK -> {
                            OutlinedTextField(
                                value = state.coreDetails.birthPlace,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(birthPlace = t) } },
                                label = { Text("Birth place") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = state.coreDetails.vaccinationRecords,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(vaccinationRecords = t) } },
                                label = { Text("Vaccination records") }
                            )
                        }
                        OnboardingValidator.AgeGroup.ADULT -> {
                            OutlinedTextField(
                                value = state.coreDetails.physicalId,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(physicalId = t) } },
                                label = { Text("Physical ID (tag/ring)") },
                                singleLine = true
                            )
                        }
                        OnboardingValidator.AgeGroup.BREEDER -> {
                            OutlinedTextField(
                                value = state.coreDetails.breedingHistory,
                                onValueChange = { t -> vm.updateCoreDetails { it.copy(breedingHistory = t) } },
                                label = { Text("Breeding history") }
                            )
                        }
                        else -> {}
                    }
                    state.validationErrors.values.firstOrNull()?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.LINEAGE -> {
                    Text("Lineage - select parents (optional scaffold)")
                    val showMale = remember { mutableStateOf(false) }
                    val showFemale = remember { mutableStateOf(false) }
                    val showScanMale = remember { mutableStateOf(false) }
                    val showScanFemale = remember { mutableStateOf(false) }
                    val males by vm.availableMaleParents.collectAsState()
                    val females by vm.availableFemaleParents.collectAsState()
                    LaunchedEffect(Unit) { vm.loadAvailableParents() }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showMale.value = true }) { Text("Select Male Parent") }
                        Button(onClick = { showFemale.value = true }) { Text("Select Female Parent") }
                        OutlinedButton(onClick = { showScanMale.value = true }) { Text("Scan Male QR") }
                        OutlinedButton(onClick = { showScanFemale.value = true }) { Text("Scan Female QR") }
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
                    state.validationErrors["lineage"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.PROOFS -> {
                    Text("Proofs & Media (picker to be integrated)")
                    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                        if (uris != null) vm.updateMedia { m -> m.copy(photoUris = uris.map { it.toString() }) }
                    }
                    val pickDocs = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                        if (uris != null) vm.updateMedia { m -> m.copy(documentUris = uris.map { it.toString() }) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { pickImages.launch("image/*") }) { Text("Pick Photos") }
                        OutlinedButton(onClick = { pickDocs.launch("application/pdf") }) { Text("Pick Documents") }
                    }
                    state.validationErrors["photos"]?.let { Text(it) }
                    state.validationErrors["documents"]?.let { Text(it) }
                }
                OnboardFarmBirdViewModel.WizardStep.REVIEW -> {
                    Text("Review & Submit")
                    Text("Name: ${state.coreDetails.name}")
                    Text("Age Group: ${state.ageGroup}")
                }
            }

            Spacer(Modifier.height(8.dp))
            // Minimal per-step gating for Next button
            val canProceed = when (state.currentStep) {
                OnboardFarmBirdViewModel.WizardStep.PATH_SELECTION -> state.isTraceable != null
                OnboardFarmBirdViewModel.WizardStep.AGE_GROUP -> state.ageGroup != null
                OnboardFarmBirdViewModel.WizardStep.CORE_DETAILS -> state.coreDetails.name.isNotBlank()
                OnboardFarmBirdViewModel.WizardStep.LINEAGE -> true
                OnboardFarmBirdViewModel.WizardStep.PROOFS -> true
                OnboardFarmBirdViewModel.WizardStep.REVIEW -> true
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.previousStep(onBack) }) { Text("Back") }
                if (state.currentStep == OnboardFarmBirdViewModel.WizardStep.REVIEW) {
                    Button(onClick = { vm.save() }, enabled = !state.saving) { Text("Submit") }
                } else {
                    Button(onClick = { vm.nextStep() }, enabled = canProceed) { Text("Next") }
                }
            }
            state.error?.let { Text(it) }
        }
    }
}
