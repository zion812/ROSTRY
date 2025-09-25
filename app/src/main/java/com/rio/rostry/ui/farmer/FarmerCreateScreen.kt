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
import androidx.compose.material3.TextButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

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
    // Dynamic fields
    val birthDateMillis: Long? = null,
    val birthPlace: String? = null,
    val vaccinationRecords: String? = null,
    val parentInfo: String? = null,
    val weightGrams: Double? = null,
    val heightCm: Double? = null,
    val gender: String? = null,
    val sizeCm: Double? = null,
    val colorPattern: String? = null,
    val specialCharacteristics: String? = null,
    val breedingHistory: String? = null,
    val provenPairsDoc: String? = null,
    val geneticTraits: String? = null,
    val awards: String? = null,
    val lineageDoc: String? = null,
    val photoCount: Int = 0,
    val videoCount: Int = 0,
    val audioCount: Int = 0,
    val documentCount: Int = 0,
    // Media URIs
    val photoUris: List<String> = emptyList(),
    val videoUris: List<String> = emptyList(),
    val audioUris: List<String> = emptyList(),
    val documentUris: List<String> = emptyList(),
    // Location override
    val latitude: Double? = null,
    val longitude: Double? = null,
    // Health record date
    val healthRecordDateMillis: Long? = null,
)

enum class Category { Meat, Adoption }
enum class Traceability { Traceable, NonTraceable }
enum class AgeGroup { Chick, Grower, Adult, Senior }
enum class PriceType { Fixed, Auction }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListingForm(
    locationVerified: Boolean,
    onRequestVerifyLocation: () -> Unit,
    onSubmit: (ListingForm) -> Unit
) {
    val context = LocalContext.current
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
    // Dynamic inputs
    var birthPlace by remember { mutableStateOf("") }
    var birthDateMillis by remember { mutableStateOf<Long?>(null) }
    var showBirthPicker by remember { mutableStateOf(false) }
    val birthState = rememberDatePickerState()
    var vaccination by remember { mutableStateOf("") }
    var parentInfo by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var genderText by remember { mutableStateOf("") }
    var sizeText by remember { mutableStateOf("") }
    var colorPattern by remember { mutableStateOf("") }
    var specialChars by remember { mutableStateOf("") }
    var breedingHistory by remember { mutableStateOf("") }
    var provenPairs by remember { mutableStateOf("") }
    var geneticTraits by remember { mutableStateOf("") }
    var awards by remember { mutableStateOf("") }
    var lineageDoc by remember { mutableStateOf("") }
    var healthRecordDateMillis by remember { mutableStateOf<Long?>(null) }
    var showHealthPicker by remember { mutableStateOf(false) }
    val healthState = rememberDatePickerState()
    var photoUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var videoUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var audioUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var documentUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var showAdvanced by remember { mutableStateOf(false) }

    // Media pickers
    val pickPhotos = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = 12)) { uris ->
        photoUris = uris.map { it.toString() }
    }
    val pickVideos = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2)) { uris ->
        videoUris = uris.map { it.toString() }
    }
    val pickDocs = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        documentUris = uris.map { it.toString() }
    }
    val pickAudio = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        audioUris = uris.map { it.toString() }
    }

    // Location capture
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true || perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            val loc = lastKnownLocation(context)
            latitude = loc?.latitude
            longitude = loc?.longitude
        }
    }
    fun requestLocation() {
        locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    // Fused location client
    val fusedClient = remember(context) { LocationServices.getFusedLocationProviderClient(context) }
    fun requestFusedLocation() {
        locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        // Try last location first
        try {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    latitude = loc.latitude
                    longitude = loc.longitude
                } else {
                    // Fallback to current location
                    val cts = CancellationTokenSource()
                    fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                        .addOnSuccessListener { cur ->
                            if (cur != null) {
                                latitude = cur.latitude
                                longitude = cur.longitude
                            }
                        }
                }
            }
        } catch (_: SecurityException) {}
    }

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

    // Pricing suggestions
    val mappedCategory = when (category) {
        Category.Meat -> com.rio.rostry.marketplace.model.ProductCategory.Meat
        Category.Adoption -> if (trace == Traceability.Traceable) com.rio.rostry.marketplace.model.ProductCategory.AdoptionTraceable else com.rio.rostry.marketplace.model.ProductCategory.AdoptionNonTraceable
    }
    val mappedAge = when (age) {
        AgeGroup.Chick -> com.rio.rostry.marketplace.model.AgeGroup.CHICK_0_5_WEEKS
        AgeGroup.Grower -> com.rio.rostry.marketplace.model.AgeGroup.YOUNG_5_20_WEEKS
        AgeGroup.Adult -> com.rio.rostry.marketplace.model.AgeGroup.ADULT_20_52_WEEKS
        AgeGroup.Senior -> com.rio.rostry.marketplace.model.AgeGroup.BREEDER_12_MONTHS_PLUS
    }
    val pricing = com.rio.rostry.marketplace.pricing.PricingEngine.suggest(mappedCategory, mappedAge)

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
            TextButton(onClick = { priceText = "${"%.0f".format(pricing.suggestedPrice)}" }) { Text("Apply suggested: ₹${"%.0f".format(pricing.suggestedPrice)}") }
        } else {
            OutlinedTextField(value = auctionText, onValueChange = { auctionText = it }, label = { Text("Start Price (₹)") }, modifier = Modifier.fillMaxWidth())
            TextButton(onClick = { auctionText = "${"%.0f".format(pricing.auctionStart)}" }) { Text("Apply suggested start: ₹${"%.0f".format(pricing.auctionStart)}") }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Available From (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Available To (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(value = healthUri, onValueChange = { healthUri = it }, label = { Text("Health Records (URI or note)") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { showBirthPicker = true }) { Text(if (birthDateMillis != null) "Birth Date: ${formatDate(birthDateMillis!!)}" else "Pick Birth Date") }
            Button(onClick = { showHealthPicker = true }) { Text(if (healthRecordDateMillis != null) "Health Date: ${formatDate(healthRecordDateMillis!!)}" else "Pick Health Date") }
        }

        // Progressive disclosure: advanced details
        TextButton(onClick = { showAdvanced = !showAdvanced }) { Text(if (showAdvanced) "Hide advanced" else "Show advanced") }
        if (showAdvanced) {
            when (age) {
                AgeGroup.Chick -> {
                    OutlinedTextField(value = birthPlace, onValueChange = { birthPlace = it }, label = { Text("Birth Place") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = vaccination, onValueChange = { vaccination = it }, label = { Text("Vaccination Records") }, modifier = Modifier.fillMaxWidth())
                    if (trace == Traceability.Traceable) {
                        OutlinedTextField(value = parentInfo, onValueChange = { parentInfo = it }, label = { Text("Parent Bird Info") }, modifier = Modifier.fillMaxWidth())
                    }
                }
                AgeGroup.Grower -> {
                    OutlinedTextField(value = weightText, onValueChange = { weightText = it }, label = { Text("Weight (g)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = healthUri, onValueChange = { healthUri = it }, label = { Text("Health Records") }, modifier = Modifier.fillMaxWidth())
                }
                AgeGroup.Adult -> {
                    OutlinedTextField(value = genderText, onValueChange = { genderText = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sizeText, onValueChange = { sizeText = it }, label = { Text("Size (cm)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = colorPattern, onValueChange = { colorPattern = it }, label = { Text("Color Pattern") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = specialChars, onValueChange = { specialChars = it }, label = { Text("Special Characteristics") }, modifier = Modifier.fillMaxWidth())
                }
                AgeGroup.Senior -> {
                    OutlinedTextField(value = breedingHistory, onValueChange = { breedingHistory = it }, label = { Text("Breeding History") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = provenPairs, onValueChange = { provenPairs = it }, label = { Text("Proven Pairs Doc") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = geneticTraits, onValueChange = { geneticTraits = it }, label = { Text("Genetic Traits") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = awards, onValueChange = { awards = it }, label = { Text("Awards") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = lineageDoc, onValueChange = { lineageDoc = it }, label = { Text("Lineage Doc ID") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Media pickers
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { pickPhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("Pick Photos (${photoUris.size})") }
            Button(onClick = { pickVideos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }) { Text("Pick Videos (${videoUris.size})") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { pickAudio.launch(arrayOf("audio/*")) }) { Text("Pick Audio (${audioUris.size})") }
            Button(onClick = { pickDocs.launch(arrayOf("application/pdf", "image/*")) }) { Text("Pick Documents (${documentUris.size})") }
        }

        // Location capture button
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { requestLocation() }) {
                val lat = latitude
                val lng = longitude
                val label = if (lat != null && lng != null) "Location: %.4f , %.4f".format(lat, lng) else "Capture GPS Location"
                Text(label)
            }
            Button(onClick = { requestFusedLocation() }) { Text("Use Fused Location") }
        }

        Divider()
        Text("Commission: 5% • ₹${"%.2f".format(commission)}")
        Text("Net payout: ₹${"%.2f".format(netPayout)}")

        val mediaStats = com.rio.rostry.marketplace.media.MediaManager.MediaStats(
            photos = photoUris.size,
            videos = videoUris.size,
            audios = audioUris.size,
            documents = documentUris.size
        )
        val mediaCheck = com.rio.rostry.marketplace.media.MediaManager.checkLimits(mediaStats)

        val dynInput = com.rio.rostry.marketplace.form.DynamicListingValidator.Input(
            category = mappedCategory,
            ageGroup = mappedAge,
            isTraceable = trace == Traceability.Traceable,
            title = title,
            birthDateMillis = birthDateMillis,
            birthPlace = birthPlace.ifBlank { null },
            vaccinationRecords = if (vaccination.isBlank()) null else vaccination,
            parentInfo = parentInfo.ifBlank { null },
            weightGrams = weightText.toDoubleOrNull(),
            healthRecords = healthUri.ifBlank { null },
            gender = genderText.ifBlank { null },
            sizeCm = sizeText.toDoubleOrNull(),
            colorPattern = colorPattern.ifBlank { null },
            specialCharacteristics = specialChars.ifBlank { null },
            breedingHistory = breedingHistory.ifBlank { null },
            provenPairsDoc = provenPairs.ifBlank { null },
            geneticTraits = geneticTraits.ifBlank { null },
            awards = awards.ifBlank { null },
            lineageDoc = lineageDoc.ifBlank { null },
            photosCount = mediaStats.photos,
            videosCount = mediaStats.videos,
            audioCount = mediaStats.audios,
            documentsCount = mediaStats.documents,
            price = parsedPrice,
            startPrice = parsedAuction,
            latitude = latitude,
            longitude = longitude
        )
        val dynRes = com.rio.rostry.marketplace.form.DynamicListingValidator.validate(dynInput)

        val errors = buildList {
            if (!locationVerified) add("Location must be verified.")
            if (title.isBlank()) add("Title is required.")
            if (priceType == PriceType.Fixed && parsedPrice == null) add("Enter valid price.")
            if (priceType == PriceType.Auction && parsedAuction == null) add("Enter valid start price.")
            if (startDate.isBlank() || endDate.isBlank()) add("Availability dates required.")
            if (healthMandatory && healthUri.isBlank()) add("Health records are mandatory for Adult/Senior.")
            if (!mediaCheck.withinLimits) addAll(mediaCheck.reasons)
            if (!dynRes.valid) addAll(dynRes.errors)
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
                        healthRecordUri = healthUri.ifBlank { null },
                        birthDateMillis = birthDateMillis,
                        birthPlace = birthPlace.ifBlank { null },
                        vaccinationRecords = vaccination.ifBlank { null },
                        parentInfo = parentInfo.ifBlank { null },
                        weightGrams = weightText.toDoubleOrNull(),
                        heightCm = heightText.toDoubleOrNull(),
                        gender = genderText.ifBlank { null },
                        sizeCm = sizeText.toDoubleOrNull(),
                        colorPattern = colorPattern.ifBlank { null },
                        specialCharacteristics = specialChars.ifBlank { null },
                        breedingHistory = breedingHistory.ifBlank { null },
                        provenPairsDoc = provenPairs.ifBlank { null },
                        geneticTraits = geneticTraits.ifBlank { null },
                        awards = awards.ifBlank { null },
                        lineageDoc = lineageDoc.ifBlank { null },
                        photoCount = mediaStats.photos,
                        videoCount = mediaStats.videos,
                        audioCount = mediaStats.audios,
                        documentCount = mediaStats.documents,
                        photoUris = photoUris,
                        videoUris = videoUris,
                        audioUris = audioUris,
                        documentUris = documentUris,
                        latitude = latitude,
                        longitude = longitude,
                        healthRecordDateMillis = healthRecordDateMillis
                    )
                )
            },
            enabled = errors.isEmpty()
        ) { Text("Publish Listing") }
    }

    if (showBirthPicker) {
        DatePickerDialog(onDismissRequest = { showBirthPicker = false }, confirmButton = {
            TextButton(onClick = {
                birthDateMillis = birthState.selectedDateMillis
                showBirthPicker = false
            }) { Text("Set Birth Date") }
        }, dismissButton = { TextButton(onClick = { showBirthPicker = false }) { Text("Cancel") } }) {
            DatePicker(state = birthState)
        }
    }
    if (showHealthPicker) {
        DatePickerDialog(onDismissRequest = { showHealthPicker = false }, confirmButton = {
            TextButton(onClick = {
                healthRecordDateMillis = healthState.selectedDateMillis
                showHealthPicker = false
            }) { Text("Set Health Date") }
        }, dismissButton = { TextButton(onClick = { showHealthPicker = false }) { Text("Cancel") } }) {
            DatePicker(state = healthState)
        }
    }
}

private fun lastKnownLocation(context: Context): Location? {
    return try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true)
        var best: Location? = null
        for (p in providers) {
            @Suppress("MissingPermission")
            val l = lm.getLastKnownLocation(p) ?: continue
            if (best == null || l.accuracy < best!!.accuracy) best = l
        }
        best
    } catch (e: Exception) { null }
}

private fun formatDate(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(millis))
}

@Composable
private fun CommunityPostComposer(onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Share with community") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Button(onClick = { if (text.isNotBlank()) onSubmit(text.trim()) }, enabled = text.isNotBlank()) { Text("Post") }
    }
}
