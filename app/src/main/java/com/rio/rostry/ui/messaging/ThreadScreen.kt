package com.rio.rostry.ui.messaging

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

@Composable
fun ThreadScreen(threadId: String, onBack: () -> Unit, vm: ThreadViewModel = hiltViewModel()) {
    LaunchedEffect(threadId) { vm.bind(threadId) }
    val msgs = vm.messages
    var input by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            items(msgs.value) { m ->
                Text(text = "${m.fromUserId.take(6)}: ${m.text}")
            }
        }
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (input.isNotBlank()) {
                    // For demo, we don't have current user/toUser; enqueue with placeholders
                    vm.sendQueuedDm(threadId = threadId, fromUserId = "me", toUserId = "them", text = input)
                    input = ""
                }
            }, modifier = Modifier.padding(start = 8.dp)) { Text("Send") }
        }
    }
}
