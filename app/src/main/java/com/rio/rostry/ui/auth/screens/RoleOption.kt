package com.rio.rostry.ui.auth.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.UserType

@Composable
fun RoleOption(
    role: UserType,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = when (role) {
                    UserType.GENERAL -> "General User"
                    UserType.FARMER -> "Farmer"
                    UserType.ENTHUSIAST -> "High-Level Enthusiast"
                },
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = when (role) {
                    UserType.GENERAL -> "Browse marketplace and place orders"
                    UserType.FARMER -> "List products and manage orders"
                    UserType.ENTHUSIAST -> "Advanced tracking, breeding records, and coin management"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}