package com.rio.rostry.domain.breeding

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.domain.pedigree.PedigreeBird
import com.rio.rostry.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BreedingServiceTest {

    private val pedigreeRepository = mockk<PedigreeRepository>()
    private lateinit var breedingService: BreedingService

    @Before
    fun setup() {
        breedingService = BreedingService(pedigreeRepository)
    }

    @Test
    fun `calculateCompatibility detects full siblings`() = runBlocking {
        // Arrange
        val sire = createProduct("Sire", "Male", "Dad1", "Mom1")
        val dam = createProduct("Dam", "Female", "Dad1", "Mom1")

        coEvery { pedigreeRepository.getFullPedigree(sire.productId, 3) } returns Resource.Success(createTree(sire))
        coEvery { pedigreeRepository.getFullPedigree(dam.productId, 3) } returns Resource.Success(createTree(dam))

        // Act
        val resultFlow = breedingService.calculateCompatibility(sire, dam)
        val results = resultFlow.toList()

        // Assert
        // Item 0 is Loading, Item 1 is Success
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        
        val compatibility = (results[1] as Resource.Success).data!!
        assertEquals(0.25, compatibility.inbreedingCoefficient, 0.01)
        assertEquals(RiskLevel.CRITICAL, compatibility.riskLevel)
        assertTrue(compatibility.warnings.any { it.contains("Full Siblings") })
    }

    @Test
    fun `calculateCompatibility detects half siblings`() = runBlocking {
        // Arrange
        val sire = createProduct("Sire", "Male", "Dad1", "Mom1")
        val dam = createProduct("Dam", "Female", "Dad1", "Mom2") // Same dad, diff mom

        coEvery { pedigreeRepository.getFullPedigree(sire.productId, 3) } returns Resource.Success(createTree(sire))
        coEvery { pedigreeRepository.getFullPedigree(dam.productId, 3) } returns Resource.Success(createTree(dam))

        // Act
        val result = breedingService.calculateCompatibility(sire, dam).toList().last() as Resource.Success
        val compatibility = result.data!!

        // Assert
        assertEquals(0.125, compatibility.inbreedingCoefficient, 0.01)
        assertEquals(RiskLevel.HIGH, compatibility.riskLevel)
        assertTrue(compatibility.warnings.any { it.contains("Half Siblings") })
    }

    @Test
    fun `predictOffspring predicts breed consistently`() {
        // Arrange
        val sire = createProduct("Sire", "Male").copy(breed = "Aseel")
        val dam = createProduct("Dam", "Female").copy(breed = "Aseel")

        // Act
        val prediction = breedingService.predictOffspring(sire, dam)

        // Assert
        assertEquals("Breeder Quality", prediction.estimatedQuality)
        assertEquals(0.95, prediction.possibleBreeds["Aseel"]!!, 0.01)
    }

    @Test
    fun `predictOffspring predicts mixed breed correctly`() {
        // Arrange
        val sire = createProduct("Sire", "Male").copy(breed = "Aseel")
        val dam = createProduct("Dam", "Female").copy(breed = "Kadaknath")

        // Act
        val prediction = breedingService.predictOffspring(sire, dam)

        // Assert
        assertEquals("Pet Quality", prediction.estimatedQuality)
        assertTrue(prediction.possibleBreeds.containsKey("Mixed (Aseel x Kadaknath)"))
    }

    // Helpers
    private fun createProduct(id: String, gender: String, fatherId: String? = null, motherId: String? = null): ProductEntity {
        return ProductEntity(
            productId = id,
            name = id,
            category = "Chicken",
            gender = gender,
            parentMaleId = fatherId,
            parentFemaleId = motherId,
            sellerId = "user1",
            createdAt = 0L,
            status = "ACTIVE"
        )
    }

    private fun createTree(bird: ProductEntity): PedigreeTree {
        return PedigreeTree(
            bird = PedigreeBird.fromProduct(bird),
            sire = null,
            dam = null
        )
    }
}
