package com.rio.rostry.ui.fowl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.components.RostryDropdown
import com.rio.rostry.ui.components.RostryTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FowlRegistrationScreen(
    viewModel: FowlViewModel = hiltViewModel(),
    onFowlRegistered: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var sireId by remember { mutableStateOf("") }
    var damId by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Date?>(null) }
    var status by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    val fowlStatuses = listOf("Chick", "Pullet", "Cockerel", "Hen", "Rooster")

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        birthDate = Date(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Select Image", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        RostryTextField(value = name, onValueChange = { name = it }, label = "Name")
        Spacer(modifier = Modifier.height(8.dp))
        RostryTextField(value = group, onValueChange = { group = it }, label = "Group")
        Spacer(modifier = Modifier.height(8.dp))
        RostryTextField(value = sireId, onValueChange = { sireId = it }, label = "Sire ID")
        Spacer(modifier = Modifier.height(8.dp))
        RostryTextField(value = damId, onValueChange = { damId = it }, label = "Dam ID")
        Spacer(modifier = Modifier.height(8.dp))
        RostryTextField(
            value = birthDate?.let { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it) } ?: "",
            onValueChange = { },
            label = "Birth Date",
            readOnly = true,
            modifier = Modifier.clickable { showDatePicker = true }
        )
        Spacer(modifier = Modifier.height(8.dp))
        RostryDropdown(label = "Status", items = fowlStatuses, selectedItem = status, onItemSelected = { status = it })
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                birthDate?.let {
                    viewModel.addFowl(
                        name = name,
                        group = group.takeIf { it.isNotBlank() },
                        sireId = sireId.takeIf { it.isNotBlank() },
                        damId = damId.takeIf { it.isNotBlank() },
                        birthDate = it,
                        status = status,
                        imageUri = imageUri
                    )
                    onFowlRegistered()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && status.isNotBlank() && birthDate != null
        ) {
            Text("Register Fowl")
        }
    }
}
