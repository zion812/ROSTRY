package com.rio.rostry.ui.verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EnthusiastKycScreen(
    onDone: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val levelState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Enthusiast KYC")
        Text("Enter KYC level (numeric) for placeholder")
        OutlinedTextField(value = levelState.value, onValueChange = { levelState.value = it }, label = { Text("KYC Level") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        Button(onClick = {
            val level = levelState.value.toIntOrNull()
            viewModel.submitEnthusiastKyc(level)
            onDone()
        }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Submit KYC")
        }
        ui.error?.let { Text("Error: $it", modifier = Modifier.padding(top = 8.dp)) }
        ui.message?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
    }
}
