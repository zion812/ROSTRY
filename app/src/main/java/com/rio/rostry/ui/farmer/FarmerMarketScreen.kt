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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FarmerMarketScreen(
    onCreateListing: () -> Unit,
    onEditListing: (String) -> Unit,
    onBoostListing: (String) -> Unit,
    onPauseListing: (String) -> Unit,
    onOpenOrder: (String) -> Unit,
    onOpenProduct: (String) -> Unit = {},
    onApplyPriceBreed: (Double?, Double?, String?) -> Unit = { _, _, _ -> },
    selectedTabIndex: Int = 0,
    onSelectTab: (Int) -> Unit = {},
    metricsRevenue: Double = 0.0,
    metricsOrders: Int = 0,
    metricsViews: Int = 0,
    isLoadingBrowse: Boolean = false,
    isLoadingMine: Boolean = false,
    browse: List<Listing> = emptyList(),
    mine: List<Listing> = emptyList(),
    onRefresh: () -> Unit = {},
    onSelectCategoryMeat: () -> Unit = {},
    onSelectCategoryAdoption: () -> Unit = {},
    onSelectTraceable: () -> Unit = {},
    onSelectNonTraceable: () -> Unit = {},
    categoryMeatSelected: Boolean = false,
    categoryAdoptionSelected: Boolean = false,
    traceableSelected: Boolean = false,
    nonTraceableSelected: Boolean = false,
) {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Marketplace", style = MaterialTheme.typography.titleLarge)
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(selected = selectedTabIndex == 0, onClick = { onSelectTab(0) }, text = { Text("Browse") })
                Tab(selected = selectedTabIndex == 1, onClick = { onSelectTab(1) }, text = { Text("Sell") })
            }
            when (selectedTabIndex) {
                0 -> BrowseMarket(
                    onOpenOrder = onOpenOrder,
                    onOpenProduct = onOpenProduct,
                    onApplyPriceBreed = onApplyPriceBreed,
                    isLoading = isLoadingBrowse,
                    items = browse,
                    onSelectCategoryMeat = onSelectCategoryMeat,
                    onSelectCategoryAdoption = onSelectCategoryAdoption,
                    onSelectTraceable = onSelectTraceable,
                    onSelectNonTraceable = onSelectNonTraceable,
                    categoryMeatSelected = categoryMeatSelected,
                    categoryAdoptionSelected = categoryAdoptionSelected,
                    traceableSelected = traceableSelected,
                    nonTraceableSelected = nonTraceableSelected
                )
                else -> SellManager(
                    onCreateListing = onCreateListing,
                    onEditListing = onEditListing,
                    onBoostListing = onBoostListing,
                    onPauseListing = onPauseListing,
                    isLoading = isLoadingMine,
                    items = mine,
                    onRefresh = onRefresh,
                    metricsRevenue = metricsRevenue,
                    metricsOrders = metricsOrders,
                    metricsViews = metricsViews
                )
            }
        }
    }
}

enum class MarketMode { Browse, Sell }

@Composable
private fun BrowseMarket(
    onOpenOrder: (String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onApplyPriceBreed: (Double?, Double?, String?) -> Unit,
    isLoading: Boolean,
    items: List<Listing>,
    onSelectCategoryMeat: () -> Unit,
    onSelectCategoryAdoption: () -> Unit,
    onSelectTraceable: () -> Unit,
    onSelectNonTraceable: () -> Unit,
    categoryMeatSelected: Boolean,
    categoryAdoptionSelected: Boolean,
    traceableSelected: Boolean,
    nonTraceableSelected: Boolean,
) {
    // Use a single LazyColumn that contains header controls and the list items to ensure smooth scrolling
    var minPriceText by remember { mutableStateOf("") }
    var maxPriceText by remember { mutableStateOf("") }
    var breedText by remember { mutableStateOf("") }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(selected = categoryMeatSelected, onClick = onSelectCategoryMeat, label = { Text("Meat") })
                FilterChip(selected = categoryAdoptionSelected, onClick = onSelectCategoryAdoption, label = { Text("Adoption") })
                FilterChip(selected = traceableSelected, onClick = onSelectTraceable, label = { Text("Traceable") })
                FilterChip(selected = nonTraceableSelected, onClick = onSelectNonTraceable, label = { Text("Non-traceable") })
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = minPriceText, onValueChange = { minPriceText = it }, label = { Text("Min ₹") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = maxPriceText, onValueChange = { maxPriceText = it }, label = { Text("Max ₹") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = breedText, onValueChange = { breedText = it }, label = { Text("Breed") }, modifier = Modifier.fillMaxWidth())
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = {
                    val min = minPriceText.toDoubleOrNull()
                    val max = maxPriceText.toDoubleOrNull()
                    val breed = breedText.ifBlank { null }
                    onApplyPriceBreed(min, max, breed)
                }) { Text("Apply Filters") }
            }
        }
        item { Divider(modifier = Modifier.padding(vertical = 4.dp)) }
        if (isLoading) {
            item { Text("Loading...") }
        }
        items(items) { item ->
            Card { Column(Modifier.padding(12.dp)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text("₹${item.price}")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Text("Views: ${item.views}")
                    Text("Inquiries: ${item.inquiries}")
                    Text("Orders: ${item.orders}")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedButton(onClick = { onOpenOrder(item.id) }) { Text("Message") }
                    Button(onClick = { onOpenProduct(item.id) }) { Text("Buy") }
                }
            } }
        }
    }
}

@Composable
private fun SellManager(
    onCreateListing: () -> Unit,
    onEditListing: (String) -> Unit,
    onBoostListing: (String) -> Unit,
    onPauseListing: (String) -> Unit,
    isLoading: Boolean,
    items: List<Listing>,
    onRefresh: () -> Unit,
    metricsRevenue: Double,
    metricsOrders: Int,
    metricsViews: Int,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onCreateListing, modifier = Modifier.fillMaxWidth()) { Text("Create Listing") }
                OutlinedButton(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) { Text("Refresh") }
            }
        }
        item {
            // Simple analytics summary
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Revenue: ₹${"%.0f".format(metricsRevenue)}")
                Text("Orders: $metricsOrders")
                Text("Views: $metricsViews")
            }
        }
        item { Spacer(Modifier.height(4.dp)) }
        item { Text("Your Listings", style = MaterialTheme.typography.titleMedium) }
        if (isLoading) {
            item { Text("Loading...") }
        }
        items(items) { item ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(12.dp)) {
                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                    Text("₹${item.price}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedButton(onClick = { onEditListing(item.id) }) { Text("Edit") }
                        OutlinedButton(onClick = { onPauseListing(item.id) }) { Text("Pause") }
                        Button(onClick = { onBoostListing(item.id) }) { Text("Boost") }
                    }
                    Divider(modifier = Modifier.padding(top = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                        Text("Views: ${item.views}")
                        Text("Inquiries: ${item.inquiries}")
                        Text("Conversions: ${(if (item.views>0) (item.orders.toDouble()/item.views*100) else 0.0).toInt()}%")
                    }
                }
            }
        }
    }
}

data class Listing(
    val id: String,
    val title: String,
    val price: Double,
    val views: Int,
    val inquiries: Int,
    val orders: Int,
)
