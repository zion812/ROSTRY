package com.rio.rostry.ui.screens.fowl

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.models.Fowl

@Composable
fun AddFowlScreen(
    onAddFowl: (String, String, Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf(System.currentTimeMillis()) }
    var isBreeder by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add New Fowl",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Breed") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Date of birth picker would go here
        // For now, we'll just use a text field
        TextField(
            value = dateOfBirth.toString(),
            onValueChange = { dateOfBirth = it.toLongOrNull() ?: System.currentTimeMillis() },
            label = { Text("Date of Birth (timestamp)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Checkbox(
                checked = isBreeder,
                onCheckedChange = { isBreeder = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Breeder")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onAddFowl(name, breed, dateOfBirth, isBreeder) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Fowl")
        }
    }
}