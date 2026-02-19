package com.rio.rostry.ui.enthusiast.digitalfarm.grading

import androidx.lifecycle.SavedStateHandle
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import com.rio.rostry.domain.digitaltwin.DigitalTwinService
import com.rio.rostry.domain.digitaltwin.ManualMorphologyGrades
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(org.robolectric.RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MorphologyGradingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val digitalTwinService = mockk<DigitalTwinService>(relaxed = true)
    private lateinit var viewModel: MorphologyGradingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mock behavior
        coEvery { digitalTwinService.getTwin("test-bird-123") } returns createTestTwin()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadBird loads existing grades from metadata`() = runTest {
        // Arrange
        val existingGradesJson = """
            {
                "manualGrades": {
                    "beakType": "HOOKED",
                    "eyeColor": "RED",
                    "bodyStructureScore": 8
                }
            }
        """.trimIndent()
        
        coEvery { digitalTwinService.getTwin("test-bird-123") } returns createTestTwin().copy(metadataJson = existingGradesJson)
        
        // Act
        val savedState = SavedStateHandle(mapOf("birdId" to "test-bird-123"))
        viewModel = MorphologyGradingViewModel(savedState, digitalTwinService)

        // Ensure coroutines launched in init block complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("HOOKED", state.grades.beakType)
        assertEquals("RED", state.grades.eyeColor)
        assertEquals(8, state.grades.bodyStructureScore)
        assertFalse(state.isLoading)
    }

    @Test
    fun `updateBeak updates state`() = runTest {
        // Arrange
        viewModel = MorphologyGradingViewModel(SavedStateHandle(mapOf("birdId" to "test-bird-123")), digitalTwinService)

        // Act
        viewModel.updateBeak("PARROT")

        // Assert
        assertEquals("PARROT", viewModel.uiState.value.grades.beakType)
    }

    @Test
    fun `submitGrading calls service and updates success state`() = runTest {
        // Arrange
        viewModel = MorphologyGradingViewModel(SavedStateHandle(mapOf("birdId" to "test-bird-123")), digitalTwinService)
        
        // Act
        viewModel.updateBeak("STRAIGHT")
        viewModel.submitGrading()

        // Assert
        coVerify { digitalTwinService.submitManualGrading("test-bird-123", any()) }
        assertTrue(viewModel.uiState.value.success)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `submitGrading handles error`() = runTest {
        // Arrange
        viewModel = MorphologyGradingViewModel(SavedStateHandle(mapOf("birdId" to "test-bird-123")), digitalTwinService)
        
        coEvery { digitalTwinService.submitManualGrading(any(), any()) } throws RuntimeException("DB Error")

        // Act
        viewModel.submitGrading()

        // Assert
        assertEquals("DB Error", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaving)
        assertFalse(viewModel.uiState.value.success)
    }

    private fun createTestTwin(): DigitalTwinEntity {
        return DigitalTwinEntity(
            twinId = "twin-1",
            birdId = "test-bird-123",
            registryId = "REG-123",
            ownerId = "owner-1",
            baseBreed = "Aseel",
            metadataJson = "{}"
        )
    }
}
