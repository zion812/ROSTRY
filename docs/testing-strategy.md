# Testing Strategy

**Version:** 2.0  
**Last Updated:** 2025-01-15  
**Audience:** All developers, QA engineers

---

This comprehensive guide outlines the testing approach, tools, patterns, and best practices for the ROSTRY Android application.

## Table of Contents

- [Goals & Philosophy](#goals--philosophy)
- [Test Pyramid](#test-pyramid)
- [Tools & Frameworks](#tools--frameworks)
- [Unit Testing](#unit-testing)
- [Integration Testing](#integration-testing)
- [UI Testing](#ui-testing)
- [Test Data Management](#test-data-management)
- [Mocking Strategies](#mocking-strategies)
- [Coroutines & Flow Testing](#coroutines--flow-testing)
- [Test Organization](#test-organization)
- [Code Coverage](#code-coverage)
- [Performance Testing](#performance-testing)
- [Flaky Tests](#flaky-tests)
- [CI/CD Integration](#cicd-integration)
- [Testing Checklists](#testing-checklists)

---

## Goals & Philosophy

### Primary Goals

1. **Reliability**: Ensure critical paths work correctly (auth, sync, marketplace, transfer, monitoring)
2. **Fast Feedback**: Unit tests complete in <5 seconds
3. **Confidence**: Comprehensive coverage prevents regressions
4. **Maintainability**: Tests are easy to understand and update

### Testing Principles

- **Test Behavior, Not Implementation**: Focus on what, not how
- **Independent Tests**: No dependencies between tests
- **Deterministic**: Same input always produces same output
- **Fast**: Optimize for quick execution
- **Clear**: Test names explain what they verify

---

## Test Pyramid

### Pyramid Structure

```
        ┌─────────┐
        │   E2E   │  ~5% (Slow, Brittle)
        │   UI    │
        └─────────┘
      ┌─────────────┐
      │Integration  │  ~15% (Medium Speed)
      └─────────────┘
    ┌─────────────────┐
    │   Unit Tests    │  ~80% (Fast, Stable)
    └─────────────────┘
```

### Target Distribution

| Layer | Percentage | Speed | Count | Purpose |
|-------|------------|-------|-------|----------|
| **Unit** | 80% | <1s per test | ~500+ | ViewModels, Repositories, Utils |
| **Integration** | 15% | 1-5s per test | ~100 | DB + Repository, Auth flows |
| **UI/E2E** | 5% | 10-30s per test | ~50 | Critical user journeys |

### Rationale

- **Speed**: Unit tests provide instant feedback
- **Cost**: Unit tests cheaper to maintain
- **Coverage**: More unit tests catch more bugs earlier

---

## Tools & Frameworks

### Unit Testing

| Tool | Version | Purpose |
|------|---------|---------|
| **JUnit 5** | 5.10.0 | Test framework |
| **Mockito** | 5.12.0 | Java mocking |
| **MockK** | 1.13.10 | Kotlin-friendly mocking |
| **kotlinx-coroutines-test** | 1.8.1 | Coroutine testing |
| **Turbine** | 1.0.0 | Flow testing |
| **Truth** | 1.1.5 | Fluent assertions |

### Integration & UI Testing

| Tool | Purpose |
|------|---------|
| **Robolectric** | Android unit tests without emulator |
| **Room In-Memory** | Database testing |
| **Hilt Testing** | DI testing |
| **Compose Testing** | Compose UI tests |
| **Espresso** | View-based UI tests |

---

## Unit Testing

### ViewModel Testing Pattern

```kotlin
@ExperimentalCoroutinesTest
class ProductViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: ProductViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = ProductViewModel(repository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `loadProducts emits loading then success state`() = runTest {
        // Given
        val products = listOf(Product("1", "Chicken", 100.0))
        coEvery { repository.getProducts() } returns flowOf(products)
        
        // When
        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertThat(viewModel.uiState.value.products).isEqualTo(products)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
```

### Repository Testing Pattern

```kotlin
@RunWith(RobolectricTestRunner::class)
class ProductRepositoryTest {
    private lateinit var database: RostryDatabase
    private lateinit var dao: ProductDao
    private lateinit var repository: ProductRepositoryImpl
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RostryDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = database.productDao()
        repository = ProductRepositoryImpl(dao, mockk())
    }
    
    @Test
    fun `createProduct saves locally`() = runBlocking {
        val product = Product("1", "Chicken", 100.0)
        repository.createProduct(product)
        val saved = dao.getById("1")
        assertThat(saved?.name).isEqualTo("Chicken")
    }
}
```

---

## Integration Testing

### Database Migration Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RostryDatabase::class.java
    )
    
    @Test
    fun migrate16To17_addsCategory() {
        helper.createDatabase("test", 16).apply {
            execSQL("INSERT INTO products VALUES ('1', 'Chicken', 100.0, 'user1', 1234567890)")
            close()
        }
        
        helper.runMigrationsAndValidate("test", 17, true, MIGRATION_16_17)
        
        helper.openDatabase("test", 17).apply {
            query("SELECT category FROM products WHERE id = '1'").use { cursor ->
                assertThat(cursor.moveToFirst()).isTrue()
                assertThat(cursor.getString(0)).isEqualTo("General")
            }
        }
    }
}
```

---

## UI Testing

### Compose Testing

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun productList_displaysProducts() {
        composeTestRule.setContent {
            ProductListScreen()
        }
        
        composeTestRule.onNodeWithText("Chicken").assertIsDisplayed()
    }
    
    @Test
    fun productClick_navigatesToDetail() {
        var clicked: String? = null
        
        composeTestRule.setContent {
            ProductListScreen(onProductClick = { clicked = it })
        }
        
        composeTestRule.onNodeWithText("Chicken").performClick()
        assertThat(clicked).isEqualTo("product-1")
    }
}
```

---

## Test Data Management

### Fixtures

```kotlin
object TestFixtures {
    fun createProduct(
        id: String = "test-${UUID.randomUUID()}",
        name: String = "Test Product",
        price: Double = 100.0
    ) = Product(id, name, price)
    
    fun createProductList(count: Int = 5) = 
        (1..count).map { createProduct(id = "p$it", name = "Product $it") }
}
```

---

## Mocking Strategies

### Fakes vs Mocks

**Fake** (real implementation):
```kotlin
class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()
    
    override suspend fun getProducts() = flowOf(products.toList())
    override suspend fun createProduct(p: Product) = products.add(p)
}
```

**Mock** (behavior verification):
```kotlin
val repo = mockk<ProductRepository>()
coEvery { repo.getProducts() } returns flowOf(emptyList())
verify { repo.getProducts() }
```

---

## Coroutines & Flow Testing

### TestDispatcher

```kotlin
private val testDispatcher = StandardTestDispatcher()

@Before
fun setup() {
    Dispatchers.setMain(testDispatcher)
}

@Test
fun test() = runTest {
    viewModel.load()
    testDispatcher.scheduler.advanceUntilIdle()
}
```

### Turbine for Flow

```kotlin
@Test
fun flow_emitsExpectedValues() = runTest {
    viewModel.uiState.test {
        assertThat(awaitItem().isLoading).isTrue()
        assertThat(awaitItem().data).isEqualTo("loaded")
    }
}
```

---

## Test Organization

### Package Structure

```
app/src/test/java/com/rio/rostry/
├── ui/{feature}/ViewModelTest.kt
├── data/repository/RepositoryTest.kt
├── util/UtilTest.kt
├── fixtures/TestFixtures.kt
└── fakes/FakeRepository.kt
```

### Naming

**Classes**: `{Class}Test`  
**Methods**: `` `action when condition returns expected` ``

---

## Code Coverage

### JaCoCo Configuration

```kotlin
plugins { id("jacoco") }

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

### Targets

| Module | Target |
|--------|--------|
| ViewModels | 85% |
| Repositories | 80% |
| Utilities | 90% |

### Generate

```bash
./gradlew testDebugUnitTest jacocoTestReport
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## Performance Testing

### Database Benchmarks

```kotlin
@Test
fun measureInsert() {
    benchmarkRule.measureRepeated {
        val products = (1..1000).map { createProduct() }
        database.productDao().insertAll(products)
    }
}
```

---

## Flaky Tests

### Mitigation

**Use Idling Resources**:
```kotlin
IdlingRegistry.getInstance().register(networkIdlingResource)
```

**Avoid Hard Delays**:
```kotlin
// Bad: Thread.sleep(1000)
// Good:
composeTestRule.waitUntil(5000) { viewModel.isLoaded }
```

### Quarantine

```kotlin
@FlakyTest
@Test
fun flakyTest() { /* ... */ }
```

---

## CI/CD Integration

### GitHub Actions

```yaml
- name: Run Tests
  run: ./gradlew test
  
- name: Coverage
  run: ./gradlew jacocoTestReport
  
- name: Upload Report
  uses: codecov/codecov-action@v3
```

**Complete CI/CD Guide**: See [ci-cd.md](ci-cd.md)

---

## Testing Checklists

### Pre-Commit

- [ ] All unit tests pass (`./gradlew test`)
- [ ] Code coverage meets targets
- [ ] No compiler warnings

### Pull Request

- [ ] New tests added for new features
- [ ] Integration tests pass
- [ ] UI tests pass (if UI changes)
- [ ] No flaky tests introduced

### Release

- [ ] Full test suite passes
- [ ] Performance tests pass
- [ ] Accessibility tests pass
- [ ] Manual smoke testing complete

---

**For testing questions or improvements, see [CODE_STYLE.md](../CODE_STYLE.md) or open an issue.**
