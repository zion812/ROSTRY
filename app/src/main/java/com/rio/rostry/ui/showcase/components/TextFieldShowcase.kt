package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Dimens

@Composable
fun TextFieldShowcase() {
    var text by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        Text("Text Fields", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Outlined") }, modifier = Modifier.fillMaxWidth())
        TextField(value = text, onValueChange = { text = it }, label = { Text("Filled") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Search") }, leadingIcon = { Icon(Icons.Default.Search, null) }, trailingIcon = { if (text.isNotEmpty()) TextButton({ text = "" }) { Text("Clear") } }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
    }
}
