---
name: generate-tests
description: Accelerates unit test creation using ROSTRY's strict testing stack. Use this when asked to write tests for ViewModels, UseCases, or Repositories.
---

# Test Generation Skill

This skill enforces the testing standards for the ROSTRY project. The project utilizes a specific stack; you must not hallucinate or introduce external testing libraries (like Mockito, unless explicitly requested).

## ROSTRY Testing Stack

You MUST strictly use the following libraries for unit testing:
*   **Test Framework**: `JUnit4`
*   **Mocking**: `MockK` (Use `@MockK`, `mockk()`, `coEvery`, `coVerify`, `slot`)
*   **Flow Testing**: `Turbine` (Use `flow.test { ... awaitItem() ... }`)
*   **Coroutines**: `kotlinx-coroutines-test` (Use `runTest`, `StandardTestDispatcher`, `UnconfinedTestDispatcher`)
*   **Assertions**: Standard `kotlin.test` asserts (`assertEquals`, `assertTrue`, etc.) or Truth (`assertThat`).

## Generation Rules

### 1. ViewModel Testing
*   Use `runTest`.
*   Replace the Main dispatcher using `Dispatchers.setMain(UnconfinedTestDispatcher())` in a `@Before` setup and reset it in `@After` (`Dispatchers.resetMain()`), OR use a generic `MainDispatcherRule` if one exists in the project.
*   Verify state emissions using Turbine: `viewModel.uiState.test { assertEquals(ExpectedState, awaitItem()) }`.
*   Mock UseCases or Repository dependencies injected into the ViewModel.

### 2. UseCase Testing
*   Use `runTest`.
*   Mock the underlying Repository (`@MockK lateinit var repository: MyRepository`).
*   Test successful scenarios and error scenarios (e.g., simulating exceptions from the repository).

### 3. Repository Testing (Local Unit Tests)
*   For Repository implementations, mock the DAOs or API services.
*   Verify that correct parameters are passed downwards (`coVerify { api.fetchData(eq("id")) }`).
*   Ensure mappers are functioning correctly as part of the repository test, or test mappers independently.

## Example Structure

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {

    @MockK
    private lateinit var myUseCase: MyUseCase

    private lateinit var viewModel: MyViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = MyViewModel(myUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when action occurs, state updates correctly`() = runTest {
        // Arrange
        coEvery { myUseCase() } returns Result.success(TestData)

        // Act & Assert
        viewModel.uiState.test {
            // initial state
            assertEquals(InitialState, awaitItem())
            
            // trigger action
            viewModel.performAction()

            // subsequent state
            assertEquals(SuccessState(TestData), awaitItem())
        }
    }
}
```
