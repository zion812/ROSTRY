package com.rio.rostry.ui.general.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeatSelectionToolScreen(
    onBack: () -> Unit
) {
    var selectedDishType by remember { mutableStateOf<DishType?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meat Selector") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "What are you cooking today?",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(16.dp))
            
            Column(Modifier.selectableGroup()) {
                DishType.values().forEach { type ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (type == selectedDishType),
                                onClick = { selectedDishType = type },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == selectedDishType),
                            onClick = null // null recommended for accessibility with selectable
                        )
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            selectedDishType?.let { type ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Recommendation:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = type.recommendation,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Why? ${type.reason}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

enum class DishType(val displayName: String, val recommendation: String, val reason: String) {
    FRY("Dry Fry / Roast", "Young Bird (Cockerel/Pullet), 6-8 Months", "Tender meat cooks quickly and stays juicy."),
    CURRY("Traditional Curry", "Mature Male/Female, 10-14 Months", "Firmer meat holds up well in gravy and releases rich flavor."),
    SOUP("Soup / Broth", "Aged Female / Mother Hen", "Bones and tougher meat release maximum nutrients and gelatin for a thick, healthy broth."),
    BIRYANI("Biryani", "Young Male, 8-10 Months", "Balances tenderness with enough texture to not fall apart during dum cooking.")
}
