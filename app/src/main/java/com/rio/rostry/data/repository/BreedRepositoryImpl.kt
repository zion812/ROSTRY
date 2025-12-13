package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.BreedDao
import com.rio.rostry.data.database.entity.BreedEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedRepositoryImpl @Inject constructor(
    private val breedDao: BreedDao
) : BreedRepository {

    override fun getAllBreeds(): Flow<List<BreedEntity>> = breedDao.getAllBreeds()

    override fun getBreedsByCulinaryProfile(profile: String): Flow<List<BreedEntity>> =
        breedDao.getBreedsByCulinaryProfile(profile)

    override fun getBreedsByDifficulty(difficulty: String): Flow<List<BreedEntity>> =
        breedDao.getBreedsByDifficulty(difficulty)

    override suspend fun getBreedById(breedId: String): BreedEntity? = breedDao.getBreedById(breedId)

    override suspend fun seedBreeds() {
        val initialBreeds = listOf(
            BreedEntity(
                breedId = "rainbow_rooster",
                name = "Rainbow Rooster / Croiler",
                description = "A dual-purpose marvel. These birds grow faster than native breeds but retain the resilience. The meat is soft yet firm, with moderate fat content that renders beautifully in curries. Perfect for families who want a balance between broiler softness and desi flavor.",
                culinaryProfile = "Soft",
                farmingDifficulty = "Beginner",
                tags = listOf("Family Choice", "Sunday Curry Special", "Fast Growth", "High Yield"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/breeds%2Frainbow_rooster.jpg?alt=media"
            ),
            BreedEntity(
                breedId = "nattu_kodi_tender",
                name = "Young Nattu Kodi (Tender)",
                description = "Harvested at 3-4 months, these young native birds offer the authentic taste of Nattu Kodi without the toughness. The meat is juicy, tender, and cooks relatively quickly. Ideal for fry dishes and quick gravies.",
                culinaryProfile = "Soft",
                farmingDifficulty = "Beginner",
                tags = listOf("Family Choice", "Sunday Curry Special", "Authentic Taste", "Quick Cook"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/breeds%2Fnattu_kodi.jpg?alt=media"
            ),
            BreedEntity(
                breedId = "nattu_kodi_pure",
                name = "Pure Nattu Kodi (Free Range)",
                description = "The gold standard of desi chicken. These birds have roamed free, foraging in fields. The meat is lean, high in muscle density, and has a distinct, robust texture that requires slow cooking. The flavor is intense and unmatched.",
                culinaryProfile = "Textured",
                farmingDifficulty = "Expert",
                tags = listOf("Gourmet Texture", "Traditional Roast Cut", "Free Range", "Intense Flavor"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/breeds%2Fnattu_kodi_pure.jpg?alt=media"
            ),
            BreedEntity(
                breedId = "aseel_cross",
                name = "Aseel Cross",
                description = "Carrying the lineage of the mighty Aseel, these birds are athletic and strong. The meat is firm, slightly gamey, and perfect for traditional spicy pulusu (stew). A favorite among connoisseurs who appreciate texture.",
                culinaryProfile = "Textured",
                farmingDifficulty = "Intermediate",
                tags = listOf("Gourmet Texture", "Traditional Roast Cut", "Gamey Note", "Slow Cook"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/breeds%2Faseel_cross.jpg?alt=media"
            ),
            BreedEntity(
                breedId = "kadaknath",
                name = "Kadaknath",
                description = "India's black pearl. Famous for its black meat, bones, and blood. It is prized for its medicinal properties, low fat, and high protein. The taste is unique, slightly gamey, and earthy. A premium choice for health-conscious gourmets.",
                culinaryProfile = "Medicinal",
                farmingDifficulty = "Expert",
                tags = listOf("Health & Heritage", "Medicinal Choice", "GI Tagged", "Superfood"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/breeds%2Fkadaknath.jpg?alt=media"
            ),
            BreedEntity(
                breedId = "aseel_pure",
                name = "Pure Aseel / Bhimavaram Lines",
                description = "The warrior breed. Known for its massive size, high bone density, and almost zero fat. The meat is extremely firm and requires pressure cooking or long slow braising. The bones make a soup that is considered a tonic for strength.",
                culinaryProfile = "Medicinal",
                farmingDifficulty = "Expert",
                tags = listOf("Health & Heritage", "Medicinal Choice", "Warrior Breed", "Bone Soup"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o/breeds%2Faseel_pure.jpg?alt=media"
            )
        )
        breedDao.insertBreeds(initialBreeds)
    }
}
