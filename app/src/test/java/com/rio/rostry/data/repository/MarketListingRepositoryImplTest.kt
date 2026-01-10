package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.InventoryItemDao
import com.rio.rostry.data.database.dao.MarketListingDao
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.InventoryItemEntity
import com.rio.rostry.data.database.entity.MarketListingEntity
import com.rio.rostry.domain.model.ListingStatus
import com.rio.rostry.utils.Resource
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Unit tests for MarketListingRepositoryImpl, specifically testing
 * the createListingFromAsset() method for the Farm-to-Market Bridge feature.
 */
class MarketListingRepositoryImplTest {

    @MockK
    private lateinit var marketListingDao: MarketListingDao

    @MockK
    private lateinit var firestore: FirebaseFirestore

    @MockK
    private lateinit var farmAssetDao: FarmAssetDao

    @MockK
    private lateinit var inventoryItemDao: InventoryItemDao

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var repository: MarketListingRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns TEST_USER_ID

        repository = MarketListingRepositoryImpl(
            marketListingDao = marketListingDao,
            firestore = firestore,
            farmAssetDao = farmAssetDao,
            inventoryItemDao = inventoryItemDao,
            firebaseAuth = firebaseAuth
        )
    }

    @Test
    fun `createListingFromAsset - success with valid asset`() = runTest {
        // Given
        val farmAsset = createTestFarmAsset()
        coEvery { farmAssetDao.getAssetById(TEST_ASSET_ID) } returns farmAsset
        coEvery { inventoryItemDao.upsert(any()) } just Runs
        coEvery { marketListingDao.insert(any()) } just Runs
        coEvery { farmAssetDao.update(any()) } just Runs

        // When - using correct parameter names
        val result = repository.createListingFromAsset(
            assetId = TEST_ASSET_ID,
            price = 1500.0,
            quantity = 5.0,
            title = "Premium Aseel Rooster",
            description = "Healthy 8-month old rooster"
        )

        // Then
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        val listingId = (result as Resource.Success).data
        assertThat(listingId).isNotNull()
        assertThat(listingId).isNotEmpty()

        // Verify inventory item was created
        coVerify { inventoryItemDao.upsert(match { 
            it.sourceAssetId == TEST_ASSET_ID && it.dirty == true 
        }) }

        // Verify listing was created with dirty flag
        coVerify { marketListingDao.insert(match { 
            it.sellerId == TEST_USER_ID && 
            it.status == ListingStatus.ACTIVE.value &&
            it.dirty == true
        }) }

        // Verify asset was marked as listed
        coVerify { farmAssetDao.update(match { it.isListed == true }) }
    }

    @Test
    fun `createListingFromAsset - fails when asset not found`() = runTest {
        // Given
        coEvery { farmAssetDao.getAssetById(TEST_ASSET_ID) } returns null

        // When
        val result = repository.createListingFromAsset(
            assetId = TEST_ASSET_ID,
            price = 100.0,
            quantity = 1.0,
            title = "Test",
            description = "Test"
        )

        // Then
        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat((result as Resource.Error).message).contains("Asset not found")
    }

    @Test
    fun `createListingFromAsset - fails when user not authenticated`() = runTest {
        // Given
        every { firebaseAuth.currentUser } returns null
        val farmAsset = createTestFarmAsset()
        coEvery { farmAssetDao.getAssetById(TEST_ASSET_ID) } returns farmAsset

        // When
        val result = repository.createListingFromAsset(
            assetId = TEST_ASSET_ID,
            price = 100.0,
            quantity = 1.0,
            title = "Test",
            description = "Test"
        )

        // Then
        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat((result as Resource.Error).message).contains("User not authenticated")
    }

    @Test
    fun `createListingFromAsset - fails when quantity exceeds available`() = runTest {
        // Given
        val farmAsset = createTestFarmAsset(currentQuantity = 5)
        coEvery { farmAssetDao.getAssetById(TEST_ASSET_ID) } returns farmAsset

        // When
        val result = repository.createListingFromAsset(
            assetId = TEST_ASSET_ID,
            price = 100.0,
            quantity = 10.0, // More than available
            title = "Test",
            description = "Test"
        )

        // Then
        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat((result as Resource.Error).message).contains("Insufficient quantity")
    }

    @Test
    fun `createListingFromAsset - sets dirty flag for offline sync`() = runTest {
        // Given
        val farmAsset = createTestFarmAsset()
        coEvery { farmAssetDao.getAssetById(TEST_ASSET_ID) } returns farmAsset
        coEvery { inventoryItemDao.upsert(any()) } just Runs
        coEvery { marketListingDao.insert(any()) } just Runs
        coEvery { farmAssetDao.update(any()) } just Runs

        val capturedInventory = slot<InventoryItemEntity>()
        val capturedListing = slot<MarketListingEntity>()

        coEvery { inventoryItemDao.upsert(capture(capturedInventory)) } just Runs
        coEvery { marketListingDao.insert(capture(capturedListing)) } just Runs

        // When
        repository.createListingFromAsset(
            assetId = TEST_ASSET_ID,
            price = 100.0,
            quantity = 1.0,
            title = "Test",
            description = "Test"
        )

        // Then - verify dirty flags are set for offline-first sync
        assertThat(capturedInventory.captured.dirty).isTrue()
        assertThat(capturedListing.captured.dirty).isTrue()
    }

    // Helper functions
    private fun createTestFarmAsset(
        currentQuantity: Int = 10
    ) = FarmAssetEntity(
        assetId = TEST_ASSET_ID,
        ownerId = TEST_USER_ID,
        name = "Test Asset",
        category = "POULTRY",
        assetType = "ROOSTER",
        currentQuantity = currentQuantity,
        unit = "BIRDS",
        breed = "Aseel",
        isListed = false,
        dirty = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    companion object {
        private const val TEST_USER_ID = "test_user_123"
        private const val TEST_ASSET_ID = "test_asset_456"
    }
}
