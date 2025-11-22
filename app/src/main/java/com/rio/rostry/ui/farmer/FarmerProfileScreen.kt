package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.rio.rostry.data.database.entity.ProductEntity
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FarmerProfileScreen(
    viewModel: FarmerProfileViewModel = hiltViewModel(),
    onEditProfile: () -> Unit,
    onManageCertifications: () -> Unit,
    onContactSupport: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Surface(Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.user?.fullName ?: "Farmer",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Reputation Score: ${state.reputation?.score ?: 0}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Member since: ${state.user?.createdAt?.let { java.text.SimpleDateFormat("MMM yyyy").format(java.util.Date(it)) } ?: "Unknown"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
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
                            val kycStatus = if (state.user?.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED) "Verified ✓" else "Pending ⚠"
                            Text("KYC: $kycStatus")
                            Text("Location: Verified ✓") // Placeholder logic
                            Text("Badges: Trusted Seller • Traceability Pro") // Placeholder
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
                            Text("Phone: ${state.user?.phoneNumber ?: "Not set"}")
                            Text("Email: ${state.user?.email ?: "Not set"}")
                            Text("Location: Erode, Tamil Nadu") // Placeholder
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
                            if (state.products.isEmpty()) {
                                Text("No products listed yet.", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                androidx.compose.foundation.lazy.LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                                ) {
                                    items(state.products) { product ->
                                        ProductCardSmall(product)
                                    }
                                }
                            }
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
                            if (state.salesHistory.isEmpty()) {
                                Text("No sales yet.", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                state.salesHistory.take(5).forEach { order ->
                                    Text("Order #${order.orderId.takeLast(4)} • ₹${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
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
}
