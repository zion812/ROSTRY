package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

/**
 * Form data for Enthusiast upgrade request.
 */
data class EnthusiastUpgradeFormData(
    val name: String = "",
    val contactNumber: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val addressLine3: String = "",
    val note: String = ""
)

/**
 * Enthusiast Upgrade Sheet - Form for farmers to request upgrade to Enthusiast role.
 * Collects: name, contact number, address (3 lines), and an optional note.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastUpgradeSheet(
    onDismiss: () -> Unit,
    onSubmit: (EnthusiastUpgradeFormData) -> Unit,
    isSubmitting: Boolean = false,
    modifier: Modifier = Modifier
) {
    var formData by remember { mutableStateOf(EnthusiastUpgradeFormData()) }
    var showErrors by remember { mutableStateOf(false) }
    
    // Validation
    val isNameValid = formData.name.isNotBlank() && formData.name.length >= 2
    val isContactValid = formData.contactNumber.length >= 10
    val isAddress1Valid = formData.addressLine1.isNotBlank()
    val isFormValid = isNameValid && isContactValid && isAddress1Valid
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with premium styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.WorkspacePremium,
                        contentDescription = null,
                        tint = Color(0xFFD4AF37),  // Gold
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Upgrade to Enthusiast",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unlock advanced breeding & analytics",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Benefits preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD4AF37).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "✨ Enthusiast Benefits",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("• Breeding calculator & pedigree tracking", style = MaterialTheme.typography.bodySmall)
                    Text("• Performance journal & analytics", style = MaterialTheme.typography.bodySmall)
                    Text("• Digital farm visualization", style = MaterialTheme.typography.bodySmall)
                    Text("• Rooster card generator & showcase", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Form fields
            Text(
                "Your Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            
            // Name
            OutlinedTextField(
                value = formData.name,
                onValueChange = { formData = formData.copy(name = it) },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && !isNameValid,
                supportingText = if (showErrors && !isNameValid) {
                    { Text("Name is required") }
                } else null,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Contact Number
            OutlinedTextField(
                value = formData.contactNumber,
                onValueChange = { formData = formData.copy(contactNumber = it.filter { c -> c.isDigit() }) },
                label = { Text("Contact Number *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && !isContactValid,
                supportingText = if (showErrors && !isContactValid) {
                    { Text("Valid 10-digit number required") }
                } else null,
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                placeholder = { Text("10-digit mobile number") }
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            
            // Address Line 1
            OutlinedTextField(
                value = formData.addressLine1,
                onValueChange = { formData = formData.copy(addressLine1 = it) },
                label = { Text("House/Shop No., Street *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && !isAddress1Valid,
                supportingText = if (showErrors && !isAddress1Valid) {
                    { Text("Address is required") }
                } else null,
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Address Line 2
            OutlinedTextField(
                value = formData.addressLine2,
                onValueChange = { formData = formData.copy(addressLine2 = it) },
                label = { Text("Village/Town, Mandal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Address Line 3
            OutlinedTextField(
                value = formData.addressLine3,
                onValueChange = { formData = formData.copy(addressLine3 = it) },
                label = { Text("District, State, PIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Note
            OutlinedTextField(
                value = formData.note,
                onValueChange = { formData = formData.copy(note = it) },
                label = { Text("Share a Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                placeholder = { Text("Tell us about your farm, experience, or goals...") }
            )
            
            Spacer(Modifier.height(24.dp))
            
            // Submit button
            Button(
                onClick = {
                    showErrors = true
                    if (isFormValid) {
                        onSubmit(formData)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4AF37)  // Gold
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Submit Upgrade Request",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                "Your request will be reviewed within 24-48 hours",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
