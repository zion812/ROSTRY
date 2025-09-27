package com.rio.rostry.ui.transfer

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rio.rostry.workers.CertificateExportWorker
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransferDocumentationScreen(
    transferId: String,
    onBack: () -> Unit,
    vm: TransferDocumentationViewModel = hiltViewModel()
) {
    LaunchedEffect(transferId) { vm.load(transferId) }
    val state = vm.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    var editable by remember { mutableStateOf(false) }
    var exportedUri by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Transfer Documentation", style = MaterialTheme.typography.titleMedium)
        state.transfer?.let { t ->
            Text("Transfer: ${t.transferId}")
            Text("From: ${t.fromUserId ?: "-"} → To: ${t.toUserId ?: "-"}")
            Text("Status: ${t.status}")
        }
        if (state.error != null) {
            Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        val json = state.certificateJson ?: "{}"
        OutlinedTextField(
            value = json,
            onValueChange = { /* read-only unless editable */ },
            readOnly = !editable,
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp),
            label = { Text("Certificate JSON") }
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, json)
                }
                context.startActivity(Intent.createChooser(intent, "Share certificate"))
            }) { Text("Share") }
            Button(onClick = {
                val input = Data.Builder()
                    .putString(CertificateExportWorker.KEY_TRANSFER_ID, state.transfer?.transferId ?: "")
                    .putString(CertificateExportWorker.KEY_CERT_JSON, json)
                    .build()
                val req = OneTimeWorkRequestBuilder<CertificateExportWorker>()
                    .setInputData(input)
                    .build()
                WorkManager.getInstance(context).enqueue(req)
                WorkManager.getInstance(context).getWorkInfoByIdLiveData(req.id).observeForever { info ->
                    if (info != null && info.state.isFinished) {
                        val uri = info.outputData.getString(CertificateExportWorker.KEY_URI) ?: ""
                        exportedUri = uri
                    }
                }
            }, modifier = Modifier.padding(start = 8.dp)) { Text("Export PDF") }
            TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) { Text("Back") }
        }
        if (exportedUri.isNotBlank()) {
            Text("Exported: $exportedUri", modifier = Modifier.padding(top = 8.dp))
        }
    }
}
