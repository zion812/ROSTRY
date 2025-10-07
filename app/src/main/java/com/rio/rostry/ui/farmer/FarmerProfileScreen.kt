package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Farm Profile", style = MaterialTheme.typography.titleLarge)
            }
            
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onEditProfile, modifier = Modifier.weight(1f)) {
                        Text("Edit Profile")
                    }
                    OutlinedButton(onClick = onManageCertifications, modifier = Modifier.weight(1f)) {
                        Text("Certifications")
                    }
                }
            }
            
            item {
                HorizontalDivider()
            }

            item {
                Card {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Verification", style = MaterialTheme.typography.titleMedium)
                        Text("KYC: Verified ✓")
                        Text("Location: Verified ✓")
                        Text("Badges: Trusted Seller • Traceability Pro")
                    }
                }
            }

            item {
                Card {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Contact", style = MaterialTheme.typography.titleMedium)
                        Text("Phone: +91-98765 43210")
                        Text("Email: farmer@example.com")
                        Text("Location: Erode, Tamil Nadu")
                    }
                }
            }

            item {
                Card {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Portfolio", style = MaterialTheme.typography.titleMedium)
                        Text("• Broilers")
                        Text("• Layers")
                        Text("• Breeder pairs")
                        Text("• Hatching eggs")
                    }
                }
            }

            item {
                Card {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Sales History", style = MaterialTheme.typography.titleMedium)
                        Text("Order #1241 • ₹2,650")
                        Text("Order #1238 • ₹9,240")
                        Text("Order #1227 • ₹1,180")
                    }
                }
            }

            item {
                Card {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Certifications", style = MaterialTheme.typography.titleMedium)
                        Text("FSSAI No. 20xxxxxxxx")
                        Text("Organic Feed Compliance")
                        OutlinedButton(
                            onClick = onManageCertifications,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Manage")
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onContactSupport,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Contact Support")
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
