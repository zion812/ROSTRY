package com.rio.rostry.ui.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeedbackScreen() {
    val (comment, setComment) = remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp)) {
        Text("We value your feedback")
        OutlinedTextField(value = comment, onValueChange = setComment, modifier = Modifier.fillMaxWidth())
        Button(onClick = { /* submit */ }, modifier = Modifier.padding(top = 12.dp)) {
            Text("Submit")
        }
    }
}
