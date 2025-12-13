package com.rio.rostry.ui.farmer

enum class Category { Meat, Adoption }
enum class Traceability { Traceable, NonTraceable }
enum class AgeGroup { Chick, Grower, Adult, Senior }
enum class PriceType { Fixed, Auction }

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
    val birthDateMillis: Long? = null,
    val birthPlace: String? = null,
    val vaccinationRecords: String? = null,
    val parentInfo: String? = null,
    val weightGrams: Double? = null,
    val photoUris: List<String> = emptyList(),
    val videoUris: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val deliveryOptions: List<String> = emptyList(),
    val deliveryCost: Double? = null,
    val leadTimeDays: Int? = null,
    val quantity: Int = 1,
    val isBatch: Boolean = false
)
