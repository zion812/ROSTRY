package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FarmerProfileScreen(
    onEditProfile: () -> Unit,
    onManageCertifications: () -> Unit,
    onContactSupport: () -> Unit,
) {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Farm Profile", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onEditProfile, modifier = Modifier.weight(1f)) { Text("Edit Profile") }
                OutlinedButton(onClick = onManageCertifications, modifier = Modifier.weight(1f)) { Text("Certifications") }
            }
            Divider()

            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Verification", style = MaterialTheme.typography.titleMedium)
                Text("KYC: Verified ✓")
                Text("Location: Verified ✓")
                Text("Badges: Trusted Seller • Traceability Pro")
            } }

            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Contact", style = MaterialTheme.typography.titleMedium)
                Text("Phone: +91-98765 43210")
                Text("Email: farmer@example.com")
                Text("Location: Erode, Tamil Nadu")
            } }

            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Portfolio", style = MaterialTheme.typography.titleMedium)
                val items = listOf("Broilers", "Layers", "Breeder pairs", "Hatching eggs")
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { it -> Text("• $it") }
                }
            } }

            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Sales History", style = MaterialTheme.typography.titleMedium)
                listOf("Order #1241 • ₹2,650", "Order #1238 • ₹9,240", "Order #1227 • ₹1,180").forEach { Text(it) }
            } }

            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Certifications", style = MaterialTheme.typography.titleMedium)
                listOf("FSSAI No. 20xxxxxxxx", "Organic Feed Compliance").forEach { Text(it) }
                OutlinedButton(onClick = onManageCertifications, modifier = Modifier.padding(top = 8.dp)) { Text("Manage") }
            } }

            OutlinedButton(onClick = onContactSupport, modifier = Modifier.fillMaxWidth()) { Text("Contact Support") }
        }
    }
}
