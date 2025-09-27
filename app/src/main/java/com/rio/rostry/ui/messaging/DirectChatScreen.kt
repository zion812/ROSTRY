package com.rio.rostry.ui.messaging

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun DirectChatScreen(
    threadId: String,
    meUserId: String,
    peerUserId: String,
    onBack: () -> Unit,
    vm: DirectChatViewModel = hiltViewModel()
) {
    LaunchedEffect(threadId) {
        vm.bind(threadId)
        vm.markSeen(threadId, meUserId)
    }
    val msgs = vm.messages
    val outbox = vm.outbox.collectAsStateWithLifecycle().value
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            vm.sendQueuedFileDm(threadId = threadId, fromUserId = meUserId, toUserId = peerUserId, fileUri = uri, fileName = null)
        }
    }
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            items(msgs.value) { m ->
                Text(text = "${m.fromUserId.take(6)}: ${m.text}")
            }
            // Pending/sent items from local outbox (simple tail list)
            items(outbox) { q ->
                val label = when (q.status) {
                    "PENDING" -> "(sending...)"
                    "FAILED" -> "(failed)"
                    else -> "(sent)"
                }
                val body = q.bodyText ?: q.fileName ?: "Attachment"
                Text(text = "${q.fromUserId.take(6)}: $body $label")
                val uri = q.fileUri
                if (!uri.isNullOrBlank()) {
                    val lower = uri.lowercase()
                    val isImage = lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")
                    val isVideo = lower.endsWith(".mp4") || lower.endsWith(".3gp") || lower.endsWith(".mkv")
                    val isAudio = lower.endsWith(".mp3") || lower.endsWith(".aac") || lower.endsWith(".wav") || lower.endsWith(".m4a")
                    if (isImage) {
                        AsyncImage(
                            model = android.net.Uri.parse(uri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else if (isVideo || isAudio) {
                        val label = if (isVideo) "Video attachment" else "Audio attachment"
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text(label, modifier = Modifier.weight(1f))
                            Button(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(android.net.Uri.parse(uri), if (isVideo) "video/*" else "audio/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                runCatching { context.startActivity(intent) }
                            }) { Text("Open") }
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (input.isNotBlank()) {
                    vm.sendQueuedDm(threadId = threadId, fromUserId = meUserId, toUserId = peerUserId, text = input)
                    input = ""
                }
            }, modifier = Modifier.padding(start = 8.dp)) { Text("Send") }
            Button(onClick = { filePicker.launch("*/*") }, modifier = Modifier.padding(start = 8.dp)) {
                Text("Attach")
            }
        }
    }
}
