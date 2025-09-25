package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
fun FarmerCreateScreen(
    locationVerified: Boolean,
    onRequestVerifyLocation: () -> Unit,
    onSubmitListing: (ListingForm) -> Unit,
    onCreatePost: (String) -> Unit,
) {
    var mode by remember { mutableStateOf(CreateMode.Product) }

    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Create", style = MaterialTheme.typography.titleLarge)
            val selectedIndex = if (mode == CreateMode.Product) 0 else 1
            TabRow(selectedTabIndex = selectedIndex) {
                Tab(selected = selectedIndex == 0, onClick = { mode = CreateMode.Product }, text = { Text("Product Listing") })
                Tab(selected = selectedIndex == 1, onClick = { mode = CreateMode.Post }, text = { Text("Community Post") })
            }

            when (mode) {
                CreateMode.Product -> Box(Modifier.weight(1f)) {
                    ProductListingForm(
                        locationVerified = locationVerified,
                        onRequestVerifyLocation = onRequestVerifyLocation,
                        onSubmit = onSubmitListing
                    )
                }
                CreateMode.Post -> Box(Modifier.weight(1f)) {
                    CommunityPostComposer(onSubmit = onCreatePost)
                }
            }
        }
    }
}

enum class CreateMode { Product, Post }

data class ListingForm(
    val category: Category,
    val traceability: Traceability,
    val ageGroup: AgeGroup,
    val title: String,
    val priceType: PriceType,
    val price: Double?,
    val auctionStartPrice: Double?,
    val availableFrom: String,
    val availableTo: String,
    val healthRecordUri: String?,
)

enum class Category { Meat, Adoption }
enum class Traceability { Traceable, NonTraceable }
enum class AgeGroup { Chick, Grower, Adult, Senior }
enum class PriceType { Fixed, Auction }

@Composable
private fun ProductListingForm(
    locationVerified: Boolean,
    onRequestVerifyLocation: () -> Unit,
    onSubmit: (ListingForm) -> Unit
) {
    var category by remember { mutableStateOf(Category.Meat) }
    var trace by remember { mutableStateOf(Traceability.Traceable) }
    var age by remember { mutableStateOf(AgeGroup.Grower) }
    var title by remember { mutableStateOf("") }
    var priceType by remember { mutableStateOf(PriceType.Fixed) }
    var priceText by remember { mutableStateOf("") }
    var auctionText by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var healthUri by remember { mutableStateOf("") }

    // Derived business logic
    val healthMandatory = age == AgeGroup.Adult || age == AgeGroup.Senior
    val parsedPrice = priceText.toDoubleOrNull()
    val parsedAuction = auctionText.toDoubleOrNull()
    val commissionRate = 0.05 // 5%
    val gross = when (priceType) {
        PriceType.Fixed -> parsedPrice ?: 0.0
        PriceType.Auction -> parsedAuction ?: 0.0
    }
    val commission = gross * commissionRate
    val netPayout = (gross - commission).coerceAtLeast(0.0)

    Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (!locationVerified) {
            Card { Column(Modifier.padding(12.dp)) {
                Text("Location verification required before listing.")
                Spacer(Modifier.height(6.dp))
                Button(onClick = onRequestVerifyLocation) { Text("Verify Location") }
            } }
        }
        // Category & Traceability
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { category = Category.Meat }, enabled = category != Category.Meat) { Text("Meat") }
            Button(onClick = { category = Category.Adoption }, enabled = category != Category.Adoption) { Text("Adoption") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { trace = Traceability.Traceable }, enabled = trace != Traceability.Traceable) { Text("Traceable") }
            Button(onClick = { trace = Traceability.NonTraceable }, enabled = trace != Traceability.NonTraceable) { Text("Non-traceable") }
        }
        // Age group
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(AgeGroup.Chick, AgeGroup.Grower, AgeGroup.Adult, AgeGroup.Senior).forEach { g ->
                Button(onClick = { age = g }, enabled = age != g, modifier = Modifier.fillMaxWidth()) { Text(g.name) }
            }
        }
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { priceType = PriceType.Fixed }, enabled = priceType != PriceType.Fixed) { Text("Fixed") }
            Button(onClick = { priceType = PriceType.Auction }, enabled = priceType != PriceType.Auction) { Text("Auction") }
        }
        if (priceType == PriceType.Fixed) {
            OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("Price (₹)") }, modifier = Modifier.fillMaxWidth())
        } else {
            OutlinedTextField(value = auctionText, onValueChange = { auctionText = it }, label = { Text("Start Price (₹)") }, modifier = Modifier.fillMaxWidth())
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Available From (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Available To (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(value = healthUri, onValueChange = { healthUri = it }, label = { Text("Health Records (URI or note)") }, modifier = Modifier.fillMaxWidth())

        Divider()
        Text("Commission: 5% • ₹${"%.2f".format(commission)}")
        Text("Net payout: ₹${"%.2f".format(netPayout)}")

        val errors = buildList {
            if (!locationVerified) add("Location must be verified.")
            if (title.isBlank()) add("Title is required.")
            if (priceType == PriceType.Fixed && parsedPrice == null) add("Enter valid price.")
            if (priceType == PriceType.Auction && parsedAuction == null) add("Enter valid start price.")
            if (startDate.isBlank() || endDate.isBlank()) add("Availability dates required.")
            if (healthMandatory && healthUri.isBlank()) add("Health records are mandatory for Adult/Senior.")
        }
        if (errors.isNotEmpty()) {
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Please fix:", style = MaterialTheme.typography.titleSmall)
                errors.forEach { Text("• $it") }
            } }
        }

        Button(
            onClick = {
                onSubmit(
                    ListingForm(
                        category = category,
                        traceability = trace,
                        ageGroup = age,
                        title = title.trim(),
                        priceType = priceType,
                        price = parsedPrice,
                        auctionStartPrice = parsedAuction,
                        availableFrom = startDate,
                        availableTo = endDate,
                        healthRecordUri = healthUri.ifBlank { null }
                    )
                )
            },
            enabled = errors.isEmpty()
        ) { Text("Publish Listing") }
    }
}

@Composable
private fun CommunityPostComposer(onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Share with community") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Button(onClick = { if (text.isNotBlank()) onSubmit(text.trim()) }, enabled = text.isNotBlank()) { Text("Post") }
    }
}
