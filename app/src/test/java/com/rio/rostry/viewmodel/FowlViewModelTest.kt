package com.rio.rostry.viewmodel

import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.HealthRecord
import com.rio.rostry.repository.FowlRepository
import com.rio.rostry.repository.DataErrorType
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class FowlViewModelTest {

    private lateinit var viewModel: FowlViewModel
    private lateinit var mockRepository: FowlRepository

    @Before
    fun setup() {
        // Set the main dispatcher to a test dispatcher
        Dispatchers.setMain(StandardTestDispatcher())
        
        // Create a mock repository
        mockRepository = mock(FowlRepository::class.java)
        
        // Create the ViewModel with the mock repository
        viewModel = FowlViewModel(mockRepository)
    }

    @After
    fun teardown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchFowls should update fowls state on success`() = runTest {
        // Arrange
        val ownerId = "test-user-id"
        val fowls = listOf(
            Fowl(
                fowlId = "1",
                ownerUserId = ownerId,
                name = "Henrietta",
                breed = "Rhode Island Red"
            )
        )
        `when`(mockRepository.getFowlsForUser(ownerId)).thenReturn(Result.success(fowls))

        // Act
        viewModel.fetchFowls(ownerId)

        // Assert
        assertEquals(fowls, viewModel.fowls.value)
        assertFalse(viewModel.loading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `fetchFowls should update error state on failure`() = runTest {
        // Arrange
        val ownerId = "test-user-id"
        val exception = Exception("Failed to fetch fowls")
        `when`(mockRepository.getFowlsForUser(ownerId)).thenReturn(Result.failure(exception))

        // Act
        viewModel.fetchFowls(ownerId)

        // Assert
        assertTrue(viewModel.fowls.value.isEmpty())
        assertFalse(viewModel.loading.value)
        assertEquals("Failed to fetch fowls", viewModel.error.value?.message)
    }

    @Test
    fun `addFowl should call repository and refresh fowls`() = runTest {
        // Arrange
        val fowl = Fowl(
            ownerUserId = "test-user-id",
            name = "Test Chicken",
            breed = "Rhode Island Red"
        )
        val fowlId = "test-fowl-id"
        `when`(mockRepository.addFowl(any())).thenReturn(Result.success(fowlId))
        `when`(mockRepository.getFowlsForUser(fowl.ownerUserId)).thenReturn(Result.success(emptyList()))

        // Act
        viewModel.addFowl(fowl)

        // Assert
        // Verify that addFowl was called
        verify(mockRepository, atLeastOnce()).addFowl(any())
        // Verify that getFowlsForUser was called to refresh the list
        verify(mockRepository, atLeastOnce()).getFowlsForUser(fowl.ownerUserId)
    }

    @Test
    fun `selectFowl should update selectedFowl state`() = runTest {
        // Arrange
        val fowl = Fowl(
            fowlId = "1",
            ownerUserId = "test-user-id",
            name = "Henrietta",
            breed = "Rhode Island Red"
        )

        // Act
        viewModel.selectFowl(fowl)

        // Assert
        assertEquals(fowl, viewModel.selectedFowl.value)
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        // Arrange
        viewModel.error.value = FowlError(
            type = DataErrorType.UNKNOWN_ERROR,
            message = "Test error"
        )

        // Act
        viewModel.clearError()

        // Assert
        assertNull(viewModel.error.value)
    }
}