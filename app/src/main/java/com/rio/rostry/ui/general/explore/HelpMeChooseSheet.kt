package com.rio.rostry.ui.general.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpMeChooseSheet(
    onDismiss: () -> Unit,
    onTasteSelected: (String) -> Unit,
    onFarmingSelected: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Help Me Choose",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))

        if (step == 1) {
            Text(
                text = "Are you Cooking or Raising?",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectionCard(
                    title = "Cooking",
                    subtitle = "I want to eat",
                    onClick = { step = 2 },
                    modifier = Modifier.weight(1f)
                )
                SelectionCard(
                    title = "Raising",
                    subtitle = "I want to farm",
                    onClick = {
                        onFarmingSelected()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else if (step == 2) {
            Text(
                text = "Do you prefer Soft Meat or Firm Bite?",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SelectionCard(
                    title = "Soft & Juicy",
                    subtitle = "Melts in mouth, quick cook",
                    onClick = {
                        onTasteSelected("Soft")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                SelectionCard(
                    title = "Firm & Textured",
                    subtitle = "Chewy, rich flavor, slow cook",
                    onClick = {
                        onTasteSelected("Textured")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                SelectionCard(
                    title = "Health & Medicinal",
                    subtitle = "Strong bones, soup, ancient taste",
                    onClick = {
                        onTasteSelected("Medicinal")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
