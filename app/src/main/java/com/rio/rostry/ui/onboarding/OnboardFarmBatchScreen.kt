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
import androidx.compose.material3.OutlinedButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardFarmBatchScreen(
    onDone: (String) -> Unit,
    onBack: () -> Unit,
    role: String? = null,
    vm: OnboardFarmBatchViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.savedId) {
        state.savedId?.let { onDone(it) }
    }

    LaunchedEffect(role) { role?.let { vm.setRole(it) } }

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
            LinearProgressIndicator(progress = currentIndex / totalSteps.toFloat(), modifier = Modifier.fillMaxWidth())
            when (state.currentStep) {
                OnboardFarmBatchViewModel.WizardStep.PATH_SELECTION -> {
                    Text("Select Path")
                    val enthusiast = role.equals("enthusiast", ignoreCase = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.updateIsTraceable(true) }) { Text("Traceable") }
                        OutlinedButton(onClick = { if (!enthusiast) vm.updateIsTraceable(false) }, enabled = !enthusiast) { Text("Non-Traceable") }
                    }
                    state.validationErrors["path"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.AGE_GROUP -> {
                    Text("Select Age Group")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.CHICK) }) { Text("Chick") }
                        Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.ADULT) }) { Text("Adult") }
                        Button(onClick = { vm.updateAgeGroup(OnboardingValidator.AgeGroup.BREEDER) }) { Text("Breeder") }
                    }
                    state.validationErrors["ageGroup"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.BATCH_DETAILS -> {
                    Text("Batch Details")
                    OutlinedTextField(
                        value = state.batchDetails.batchName,
                        onValueChange = { text -> vm.updateBatchDetails { bd -> bd.copy(batchName = text) } },
                        label = { Text("Batch Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.batchDetails.count,
                        onValueChange = { text -> vm.updateBatchDetails { bd -> bd.copy(count = text.filter { it.isDigit() }) } },
                        label = { Text("Count (>= 2)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = state.batchDetails.breed,
                        onValueChange = { text -> vm.updateBatchDetails { bd -> bd.copy(breed = text) } },
                        label = { Text("Breed") },
                        singleLine = true
                    )
                    // Hatch date picker
                    val showDate = remember { mutableStateOf(false) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showDate.value = true }) {
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
                    state.validationErrors.values.firstOrNull()?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.LINEAGE -> {
                    Text("Lineage - select parents (optional scaffold)")
                    val showMale = remember { mutableStateOf(false) }
                    val showFemale = remember { mutableStateOf(false) }
                    val males by vm.availableMaleParents.collectAsState()
                    val females by vm.availableFemaleParents.collectAsState()
                    LaunchedEffect(Unit) { vm.loadAvailableParents() }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showMale.value = true }) { Text("Select Male Parent") }
                        Button(onClick = { showFemale.value = true }) { Text("Select Female Parent") }
                        val male = vm.state.collectAsState().value.lineage.maleParentId
                        val female = vm.state.collectAsState().value.lineage.femaleParentId
                        if (!male.isNullOrBlank()) OutlinedButton(onClick = { vm.updateLineage(null, vm.state.value.lineage.femaleParentId) }) { Text("Male: ${male.take(6)}…") }
                        if (!female.isNullOrBlank()) OutlinedButton(onClick = { vm.updateLineage(vm.state.value.lineage.maleParentId, null) }) { Text("Female: ${female.take(6)}…") }
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
                    state.validationErrors["lineage"]?.let { Text(it) }
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
                        OutlinedButton(onClick = { pickImages.launch("image/*") }) { Text("Pick Photos") }
                        OutlinedButton(onClick = { pickDocs.launch("application/pdf") }) { Text("Pick Documents") }
                    }
                    val photos = state.media.photoUris.size
                    val docs = state.media.documentUris.size
                    Text("Selected: $photos photos, $docs documents")
                    state.validationErrors["photos"]?.let { Text(it) }
                    state.validationErrors["documents"]?.let { Text(it) }
                }
                OnboardFarmBatchViewModel.WizardStep.REVIEW -> {
                    Text("Review & Submit")
                    Text("Batch: ${state.batchDetails.batchName}")
                    Text("Count: ${state.batchDetails.count}")
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.previousStep(onBack) }) { Text("Back") }
                if (state.currentStep == OnboardFarmBatchViewModel.WizardStep.REVIEW) {
                    Button(onClick = { vm.save() }, enabled = !state.saving) { Text("Submit") }
                } else {
                    Button(onClick = { vm.nextStep() }) { Text("Next") }
                }
            }
            state.error?.let { Text(it) }
        }
    }
}
