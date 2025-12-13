package com.rio.rostry.ui.onboarding

import com.rio.rostry.data.repository.OnboardingChecklistRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.usecase.DetermineChecklistRelevanceUseCase
import com.rio.rostry.domain.usecase.GenerateChecklistSuggestionsUseCase
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class OnboardingChecklistViewModelTest {

    @Mock
    private lateinit var onboardingChecklistRepository: OnboardingChecklistRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var currentUserProvider: CurrentUserProvider

    @Mock
    private lateinit var flowAnalyticsTracker: FlowAnalyticsTracker

    @Mock
    private lateinit var determineChecklistRelevanceUseCase: DetermineChecklistRelevanceUseCase

    @Mock
    private lateinit var generateChecklistSuggestionsUseCase: GenerateChecklistSuggestionsUseCase

    private lateinit var viewModel: OnboardingChecklistViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        viewModel = OnboardingChecklistViewModel(
            onboardingChecklistRepository = onboardingChecklistRepository,
            userRepository = userRepository,
            currentUserProvider = currentUserProvider,
            flowAnalyticsTracker = flowAnalyticsTracker,
            determineChecklistRelevanceUseCase = determineChecklistRelevanceUseCase,
            generateChecklistSuggestionsUseCase = generateChecklistSuggestionsUseCase
        )
    }

    @Test
    fun `loadChecklistState - should load checklist state for user`() = runTest {
        // Given
        val userId = "test_user_id"
        whenever(currentUserProvider.userIdOrNull()).thenReturn(userId)
        
        val mockUser = mock<com.rio.rostry.data.database.entity.UserEntity>().apply {
            whenever(this.role).thenReturn(UserType.FARMER)
        }
        
        val mockChecklistItems = listOf(
            OnboardingChecklistRepository.ChecklistItem(
                "complete_profile", 
                "Complete Profile", 
                "Fill in your name, email, and phone number", 
                false, 
                "profile"
            )
        )
        
        val mockChecklistState = OnboardingChecklistRepository.ChecklistState(
            completedIds = emptySet(),
            isDismissed = false
        )
        
        val mockSuggestions = listOf("Complete your profile to unlock marketplace access and advanced features.")
        
        whenever(userRepository.getCurrentUser()).thenReturn(flowOf(Resource.Success(mockUser)))
        whenever(onboardingChecklistRepository.getChecklistItemsForRole(UserType.FARMER)).thenReturn(mockChecklistItems)
        whenever(onboardingChecklistRepository.loadChecklistState(userId)).thenReturn(mockChecklistState)
        whenever(onboardingChecklistRepository.isItemCompleted(any(), eq(mockUser))).thenReturn(false)
        whenever(generateChecklistSuggestionsUseCase(any(), eq(UserType.FARMER))).thenReturn(mockSuggestions)
        whenever(determineChecklistRelevanceUseCase(mockUser, any())).thenReturn(true)
        whenever(onboardingChecklistRepository.isOnboardingCompletedTracked(userId)).thenReturn(false)

        // When
        // ViewModel is initialized with loadChecklistState called in init

        // Then
        // UI State should be updated with correct values
        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.items.size)
        assertEquals(UserType.FARMER, uiState.currentRole)
        assertFalse(uiState.isLoading)
        assertEquals(0, uiState.completionPercentage)  // Since no items are completed
        assertEquals(mockSuggestions, uiState.smartSuggestions)
        assertTrue(uiState.isChecklistRelevant)
    }

    @Test
    fun `markItemCompleted - should update repository and UI state`() = runTest {
        // Given
        val userId = "test_user_id"
        whenever(currentUserProvider.userIdOrNull()).thenReturn(userId)
        whenever(onboardingChecklistRepository.markItemCompleted(userId, "complete_profile"))
            .thenReturn(kotlin.Result.success(Unit))

        val initialItem = OnboardingChecklistViewModel.ChecklistItem(
            "complete_profile",
            "Complete Profile", 
            "Fill in your name, email, and phone number", 
            false, 
            "profile"
        )
        
        val expectedItem = initialItem.copy(isCompleted = true)

        // When
        viewModel.markItemCompleted("complete_profile")

        // Then
        verify(onboardingChecklistRepository).markItemCompleted(userId, "complete_profile")
        verify(flowAnalyticsTracker).trackOnboardingChecklistProgress("complete_profile", true)

        val uiState = viewModel.uiState.value
        assertTrue(uiState.items.first().isCompleted)
        assertEquals(100, uiState.completionPercentage)  // Since it was 1 item and now it's completed
    }

    @Test
    fun `dismissChecklist - should update repository and UI state`() = runTest {
        // Given
        val userId = "test_user_id"
        whenever(currentUserProvider.userIdOrNull()).thenReturn(userId)
        whenever(onboardingChecklistRepository.dismissChecklist(userId))
            .thenReturn(kotlin.Result.success(Unit))

        // When
        viewModel.dismissChecklist()

        // Then
        verify(onboardingChecklistRepository).dismissChecklist(userId)
        
        val uiState = viewModel.uiState.value
        assertTrue(uiState.items.isEmpty())
        assertEquals(100, uiState.completionPercentage)
        assertTrue(uiState.smartSuggestions.isEmpty())
    }

    @Test
    fun `markItemCompleted - should handle repository error`() = runTest {
        // Given
        val userId = "test_user_id"
        whenever(currentUserProvider.userIdOrNull()).thenReturn(userId)
        whenever(onboardingChecklistRepository.markItemCompleted(userId, "complete_profile"))
            .thenReturn(kotlin.Result.failure(Exception("Database error")))

        // When
        viewModel.markItemCompleted("complete_profile")

        // Then
        val uiState = viewModel.uiState.value
        assertEquals("Failed to save progress", uiState.error)
    }

    @Test
    fun `dismissChecklist - should handle repository error`() = runTest {
        // Given
        val userId = "test_user_id"
        whenever(currentUserProvider.userIdOrNull()).thenReturn(userId)
        whenever(onboardingChecklistRepository.dismissChecklist(userId))
            .thenReturn(kotlin.Result.failure(Exception("Database error")))

        // When
        viewModel.dismissChecklist()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals("Failed to dismiss checklist", uiState.error)
    }

    @Test
    fun `calculateCompletionPercentage - should calculate correctly`() {
        // Given
        val completedItem = OnboardingChecklistViewModel.ChecklistItem("", "", "", true, "")
        val incompleteItem = OnboardingChecklistViewModel.ChecklistItem("", "", "", false, "")
        val items = listOf(completedItem, incompleteItem)

        // When
        val result = calculateCompletionPercentage(items)

        // Then
        assertEquals(50, result)  // 1 out of 2 items completed
    }

    @Test
    fun `calculateCompletionPercentage - should return 0 for empty list`() {
        // Given
        val items = emptyList<OnboardingChecklistViewModel.ChecklistItem>()

        // When
        val result = calculateCompletionPercentage(items)

        // Then
        assertEquals(0, result)
    }

    private fun calculateCompletionPercentage(items: List<OnboardingChecklistViewModel.ChecklistItem>): Int {
        if (items.isEmpty()) return 0
        val completedCount = items.count { it.isCompleted }
        return (completedCount * 100) / items.size
    }
}