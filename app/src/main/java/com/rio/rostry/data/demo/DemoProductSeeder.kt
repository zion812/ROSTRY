package com.rio.rostry.data.demo

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds database with 80 sample products for testing General user flows.
 * Only runs in debug builds to provide realistic marketplace data.
 */
@Singleton
class DemoProductSeeder @Inject constructor(
    private val productDao: ProductDao
) {
    
    private data class BreedTemplate(
        val name: String,
        val avgPrice: Double,
        val minAge: Int,
        val maxAge: Int,
        val description: String
    )
    
    private data class LocationData(
        val city: String,
        val state: String,
        val latitude: Double,
        val longitude: Double
    )
    
    private val breeds = listOf(
        BreedTemplate("Asil", 3500.0, 180, 730, "Fighting breed with strong physique"),
        BreedTemplate("Kadaknath", 2800.0, 90, 365, "Black meat chicken, prized for health benefits"),
        BreedTemplate("Rhode Island Red", 800.0, 60, 365, "Popular egg-laying breed, dual purpose"),
        BreedTemplate("Leghorn", 600.0, 45, 365, "High egg production, white eggs"),
        BreedTemplate("Brahma", 2200.0, 120, 545, "Large gentle giant, good meat bird"),
        BreedTemplate("Cochin", 1800.0, 90, 545, "Fluffy ornamental breed, good brooders")
    )
    
    private val locations = listOf(
        LocationData("Bangalore", "Karnataka", 12.9716, 77.5946),
        LocationData("Hyderabad", "Telangana", 17.3850, 78.4867),
        LocationData("Chennai", "Tamil Nadu", 13.0827, 80.2707),
        LocationData("Pune", "Maharashtra", 18.5204, 73.8567),
        LocationData("Mumbai", "Maharashtra", 19.0760, 72.8777),
        LocationData("Delhi", "Delhi", 28.7041, 77.1025),
        LocationData("Vijayawada", "Andhra Pradesh", 16.5062, 80.6480),
        LocationData("Coimbatore", "Tamil Nadu", 11.0168, 76.9558)
    )
    
    private val ageGroups = listOf(
        Pair("Day-old", 0..7),
        Pair("Chicks", 8..35),
        Pair("Grower", 36..140),
        Pair("Adult", 141..730)
    )
    
    private val sellerIds = listOf(
        "demo_seller_1", "demo_seller_2", "demo_seller_3", 
        "demo_seller_4", "demo_seller_5", "demo_seller_6"
    )
    
    suspend fun seedProducts() = withContext(Dispatchers.IO) {
        try {
            // Check if already seeded by querying products
            val existingProducts = productDao.findById("demo_prod_check")
            if (existingProducts != null) {
                Timber.d("DemoProductSeeder: Database already seeded")
                return@withContext
            }
            
            Timber.i("DemoProductSeeder: Starting to seed 80 sample products...")
            
            val products = mutableListOf<ProductEntity>()
            val now = System.currentTimeMillis()
            
            // Generate products for each breed
            for (breedTemplate in breeds) {
                // Generate 13-14 products per breed (total ~80)
                repeat(13) { index ->
                    val location = locations.random()
                    val ageGroup = ageGroups.random()
                    val daysOld = ageGroup.second.random()
                    
                    // Price variation based on age and random factor
                    val priceVariation = when {
                        daysOld < 30 -> 0.3 // Young birds cheaper
                        daysOld < 120 -> 0.8
                        daysOld < 365 -> 1.2
                        else -> 1.5 // Mature birds more expensive
                    }
                    val price = (breedTemplate.avgPrice * priceVariation * (0.8 + Math.random() * 0.4))
                    
                    // Determine if traceable (50% chance)
                    val isTraceable = Math.random() > 0.5
                    val familyTreeId = if (isTraceable) "FAMILY_${UUID.randomUUID().toString().take(8)}" else null
                    
                    // Determine if from verified seller (60% chance)
                    val seller = sellerIds.random()
                    val isVerified = Math.random() > 0.4
                    
                    // Generate realistic name
                    val gender = listOf("Rooster", "Hen", "Pair", "Chicks").random()
                    val ageDesc = when {
                        daysOld < 30 -> "Day-old"
                        daysOld < 120 -> "$daysOld days"
                        daysOld < 365 -> "${daysOld / 30} months"
                        else -> "${daysOld / 365} years"
                    }
                    val name = "${breedTemplate.name} $gender - $ageDesc"
                    
                    // Quantity varies
                    val quantity = when {
                        gender == "Chicks" -> (5..20).random().toDouble()
                        gender == "Pair" -> 2.0
                        else -> 1.0
                    }
                    
                    val product = ProductEntity(
                        productId = "demo_prod_${UUID.randomUUID()}",
                        sellerId = seller,
                        name = name,
                        description = "${breedTemplate.description}. Age: $ageDesc. Location: ${location.city}.",
                        category = breedTemplate.name,
                        price = price,
                        quantity = quantity,
                        unit = if (quantity == 1.0) "piece" else "pieces",
                        imageUrls = listOf(generateImageUrl(breedTemplate.name, index)),
                        location = "${location.city}, ${location.state}",
                        latitude = location.latitude + (Math.random() * 0.1 - 0.05), // Slight variation
                        longitude = location.longitude + (Math.random() * 0.1 - 0.05),
                        status = "available",
                        condition = if (Math.random() > 0.7) "organic" else "fresh",
                        birthDate = now - (daysOld * 86400000L),
                        vaccinationRecordsJson = if (Math.random() > 0.5) """[{"date":${now - 30 * 86400000L},"vaccine":"Newcastle"}]""" else null,
                        weightGrams = calculateWeight(breedTemplate.name, daysOld) * 1000, // Convert kg to grams
                        gender = gender.takeIf { it != "Pair" && it != "Chicks" },
                        color = listOf("Black", "White", "Brown", "Red", "Mixed").random(),
                        breed = breedTemplate.name,
                        familyTreeId = familyTreeId,
                        parentIdsJson = if (isTraceable) """["parent_${UUID.randomUUID().toString().take(6)}"]""" else null,
                        transferHistoryJson = if (isTraceable) """[{"from":"breeder_1","to":"$seller","date":${now - 86400000}}]""" else null,
                        createdAt = now - ((0..30).random() * 86400000L), // Created within last 30 days
                        updatedAt = now,
                        lastModifiedAt = now,
                        isDeleted = false,
                        dirty = false
                    )
                    
                    products.add(product)
                }
            }
            
            // Batch insert all products
            productDao.insertProducts(products)
            
            Timber.i("DemoProductSeeder: Successfully seeded ${products.size} products")
            Timber.d("DemoProductSeeder: Breeds: ${products.groupBy { it.breed }.mapValues { it.value.size }}")
            Timber.d("DemoProductSeeder: Locations: ${products.groupBy { it.location }.mapValues { it.value.size }}")
            Timber.d("DemoProductSeeder: Traceable: ${products.count { it.familyTreeId != null }}")
            
        } catch (e: Exception) {
            Timber.e(e, "DemoProductSeeder: Failed to seed products")
        }
    }
    
    private fun generateImageUrl(breed: String, index: Int): String {
        // Use placeholder service or local assets
        val breedSlug = breed.lowercase().replace(" ", "-")
        return "https://picsum.photos/seed/$breedSlug-$index/400/300"
    }
    
    private fun calculateWeight(breed: String, ageInDays: Int): Double {
        // Rough weight estimation in kg
        return when (breed) {
            "Asil" -> (ageInDays * 0.025).coerceAtMost(4.5)
            "Kadaknath" -> (ageInDays * 0.020).coerceAtMost(3.0)
            "Rhode Island Red" -> (ageInDays * 0.030).coerceAtMost(3.5)
            "Leghorn" -> (ageInDays * 0.018).coerceAtMost(2.5)
            "Brahma" -> (ageInDays * 0.035).coerceAtMost(5.5)
            "Cochin" -> (ageInDays * 0.032).coerceAtMost(5.0)
            else -> (ageInDays * 0.025).coerceAtMost(3.5)
        }
    }
    
    suspend fun clearDemoData() = withContext(Dispatchers.IO) {
        try {
            // Remove all demo products
            // Note: In production, you'd query products and filter by ID prefix
            // For simplicity, this is a placeholder for a manual cleanup operation
            Timber.i("DemoProductSeeder: Demo data cleanup not implemented - use database tools to remove demo_prod_* entries")
        } catch (e: Exception) {
            Timber.e(e, "DemoProductSeeder: Failed to clear demo data")
        }
    }
}
