package com.rio.rostry.ui.general.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rio.rostry.ui.components.AddToFarmDialog
import kotlinx.coroutines.launch

@Composable
fun GeneralCartRoute(
    onCheckoutComplete: (String) -> Unit,
    viewModel: GeneralCartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.lastCreatedOrderId) {
        uiState.lastCreatedOrderId?.let { orderId ->
            onCheckoutComplete(orderId)
            viewModel.clearLastOrderId()
        }
    }

    if (!uiState.isAuthenticated) {
        AuthRequiredState()
        return
    }

    GeneralCartScreen(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onIncrement = viewModel::incrementQuantity,
        onDecrement = viewModel::decrementQuantity,
        onRemove = viewModel::removeItem,
        onSelectDelivery = viewModel::selectDeliveryOption,
        onSelectPayment = viewModel::selectPayment,
        onSelectAddress = viewModel::selectAddress,
        onCheckout = viewModel::checkout
    )
    
    // Marketplace-to-farm bridge: Show onboarding choice after farmer purchases
    if (uiState.showAddToFarmDialog && uiState.addToFarmProductId != null) {
        AddToFarmDialog(
            onDismiss = { viewModel.dismissAddToFarmDialog() },
            onSelectIndividual = { viewModel.addToFarmMonitoring(uiState.addToFarmProductId!!) },
            onSelectBatch = { viewModel.addToFarmMonitoring(uiState.addToFarmProductId!!) }
        )
    }
}

@Composable
private fun AuthRequiredState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", modifier = Modifier.height(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("Sign in to manage your cart", style = MaterialTheme.typography.titleMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GeneralCartScreen(
    state: GeneralCartViewModel.CartUiState,
    snackbarHostState: SnackbarHostState,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onRemove: (String) -> Unit,
    onSelectDelivery: (String) -> Unit,
    onSelectPayment: (GeneralCartViewModel.PaymentMethod) -> Unit,
    onSelectAddress: (String) -> Unit,
    onCheckout: () -> Unit
) {
    var showAddressMenu by rememberSaveable { mutableStateOf(false) }
    var showDeliveryMenu by rememberSaveable { mutableStateOf(false) }
    var showPaymentMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your cart", maxLines = 1, overflow = TextOverflow.Ellipsis) })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                com.rio.rostry.ui.components.LoadingOverlay()
            }
            return@Scaffold
        }

        if (state.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                com.rio.rostry.ui.components.EmptyState(
                    title = "Cart is empty",
                    subtitle = "Browse the marketplace to add items",
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.items, key = { it.productId }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrement = { onIncrement(item.productId) },
                        onDecrement = { onDecrement(item.productId) },
                        onRemove = { onRemove(item.productId) }
                    )
                }

                item {
                    DeliveryCard(
                        state = state,
                        showDeliveryMenu = showDeliveryMenu,
                        onToggleDeliveryMenu = { showDeliveryMenu = !showDeliveryMenu },
                        onSelectDelivery = {
                            onSelectDelivery(it)
                            showDeliveryMenu = false
                        }
                    )
                }

                item {
                    PaymentCard(
                        state = state,
                        showPaymentMenu = showPaymentMenu,
                        onTogglePaymentMenu = { showPaymentMenu = !showPaymentMenu },
                        onSelectPayment = {
                            onSelectPayment(it)
                            showPaymentMenu = false
                        }
                    )
                }

                item {
                    AddressCard(
                        state = state,
                        showAddressMenu = showAddressMenu,
                        onToggleAddressMenu = { showAddressMenu = !showAddressMenu },
                        onSelectAddress = {
                            onSelectAddress(it)
                            showAddressMenu = false
                        }
                    )
                }

                if (state.orderHistory.isNotEmpty()) {
                    item {
                        OrderHistoryCard(state = state)
                    }
                }
            }

            SummarySection(state = state, onCheckout = onCheckout)
        }
    }
}

@Composable
private fun CartItemCard(
    item: GeneralCartViewModel.CartItemUi,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = "Product image",
                    modifier = Modifier.height(80.dp).fillMaxWidth(0.3f)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.location, style = MaterialTheme.typography.bodySmall)
                    Text("₹${"%.2f".format(item.price)} / ${item.unit}", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove from cart")
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDecrement) { Text("-") }
                    Text("${"%.1f".format(item.quantity)}", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onIncrement) { Text("+") }
                }
                Text("Subtotal ₹${"%.2f".format(item.subtotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun DeliveryCard(
    state: GeneralCartViewModel.CartUiState,
    showDeliveryMenu: Boolean,
    onToggleDeliveryMenu: () -> Unit,
    onSelectDelivery: (String) -> Unit
) {
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Delivery", style = MaterialTheme.typography.titleMedium)
            Text(state.selectedDelivery?.eta ?: "Select delivery")
            TextButton(onClick = onToggleDeliveryMenu) {
                Icon(Icons.Filled.LocalShipping, contentDescription = "Delivery options")
                Spacer(Modifier.width(8.dp))
                Text(state.selectedDelivery?.label ?: "Choose option")
            }
            DropdownMenu(expanded = showDeliveryMenu, onDismissRequest = onToggleDeliveryMenu) {
                state.deliveryOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text("${option.label} • ₹${"%.0f".format(option.fee)}") },
                        onClick = { onSelectDelivery(option.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(
    state: GeneralCartViewModel.CartUiState,
    showPaymentMenu: Boolean,
    onTogglePaymentMenu: () -> Unit,
    onSelectPayment: (GeneralCartViewModel.PaymentMethod) -> Unit
) {
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Payment method", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onTogglePaymentMenu) {
                Icon(Icons.Filled.Payment, contentDescription = "Payment methods")
                Spacer(Modifier.width(8.dp))
                Text(state.selectedPayment.name.replace('_', ' '))
            }
            DropdownMenu(expanded = showPaymentMenu, onDismissRequest = onTogglePaymentMenu) {
                state.paymentMethods.forEach { method ->
                    DropdownMenuItem(
                        text = { Text(method.name.replace('_', ' ')) },
                        onClick = { onSelectPayment(method) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressCard(
    state: GeneralCartViewModel.CartUiState,
    showAddressMenu: Boolean,
    onToggleAddressMenu: () -> Unit,
    onSelectAddress: (String) -> Unit
) {
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Delivery address", style = MaterialTheme.typography.titleMedium)
            Text(state.selectedAddress ?: "Select address")
            TextButton(onClick = onToggleAddressMenu) {
                Icon(Icons.Filled.Home, contentDescription = "Addresses")
                Spacer(Modifier.width(8.dp))
                Text("Change address")
            }
            DropdownMenu(expanded = showAddressMenu, onDismissRequest = onToggleAddressMenu) {
                state.addresses.forEach { address ->
                    DropdownMenuItem(text = { Text(address) }, onClick = { onSelectAddress(address) })
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(state: GeneralCartViewModel.CartUiState) {
    ElevatedCard { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Recent orders", style = MaterialTheme.typography.titleMedium)
            state.orderHistory.forEach { order ->
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(order.orderId.takeLast(6), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("Status: ${order.status}", style = MaterialTheme.typography.bodySmall)
                    Text("Amount: ₹${"%.2f".format(order.amount)}", style = MaterialTheme.typography.bodySmall)
                    Text("Payment: ${order.paymentMethod ?: "COD"}", style = MaterialTheme.typography.bodySmall)
                }
                Divider()
            }
        }
    }
}

@Composable
private fun SummarySection(state: GeneralCartViewModel.CartUiState, onCheckout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                Text("₹${"%.2f".format(state.subtotal)}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery", style = MaterialTheme.typography.bodyMedium)
                Text("₹${"%.2f".format(state.deliveryFee)}", style = MaterialTheme.typography.bodyMedium)
            }
            Divider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("₹${"%.2f".format(state.total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onCheckout,
                enabled = state.checkoutEnabled && !state.isCheckingOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isCheckingOut) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Processing…")
                } else {
                    Text("Place order")
                }
            }
            if (!state.checkoutEnabled && !state.isCheckingOut && state.checkoutHint != null) {
                Text(
                    text = state.checkoutHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
