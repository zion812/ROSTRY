---
Version: 1.0
Last Updated: 2025-12-29
Audience: Developers
Status: Active
---

# ROSTRY Testing Cookbook

This cookbook provides common testing patterns and examples for the ROSTRY Android application.

## Table of Contents
- [ViewModel Testing with StateFlow](#viewmodel-testing-with-stateflow)
- [Repository Testing with Room and Firebase](#repository-testing-with-room-and-firebase)
- [Compose UI Testing with Semantics](#compose-ui-testing-with-semantics)
- [Worker Testing with WorkManager TestDriver](#worker-testing-with-workmanager-testdriver)
- [Migration Testing with Room](#migration-testing-with-room)
- [Digital Farm Rendering Testing](#digital-farm-rendering-testing)
- [Evidence Order Flow Testing](#evidence-order-flow-testing)

---

## ViewModel Testing with StateFlow

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class DigitalFarmViewModelTest {
    private val repository = mockk<EnthusiastBreedingRepository>()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: DigitalFarmViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DigitalFarmViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        viewModel.uiState.test {
            val item = awaitItem()
            assertTrue(item.isLoading)
        }
    }

    @Test
    fun `loading farm data updates state`() = runTest {
        val mockBirds = listOf(VisualBird(id = "1", type = BirdType.HEN))
        coEvery { repository.getFarmBirds() } returns flowOf(mockBirds)

        viewModel.loadFarm()

        viewModel.uiState.test {
            val item = awaitItem()
            assertEquals(mockBirds, item.birds)
            assertFalse(item.isLoading)
        }
    }
}
```

---

## Repository Testing with Room and Firebase

```kotlin
@RunWith(AndroidJUnit4::class)
class EvidenceOrderRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: OrderEvidenceDao
    private val firestore = mockk<FirebaseFirestore>()
    private lateinit var repository: EvidenceOrderRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.orderEvidenceDao()
        repository = EvidenceOrderRepositoryImpl(dao, firestore)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `saveEvidence saves to local and remote`() = runTest {
        val evidence = OrderEvidenceEntity(id = "e1", orderId = "o1", type = EvidenceType.PAYMENT_PROOF)
        
        repository.saveEvidence(evidence)

        val local = dao.getById("e1")
        assertNotNull(local)
        coVerify { firestore.collection("evidence").document("e1").set(any()) }
    }
}
```

---

## Compose UI Testing with Semantics

```kotlin
class ProductCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productCard_displaysInformation() {
        val product = Product(name = "Premium Rooster", price = 1500.0)
        
        composeTestRule.setContent {
            ProductCard(product = product)
        }

        composeTestRule.onNodeWithText("Premium Rooster").assertIsDisplayed()
        composeTestRule.onNodeWithText("₹1,500.00").assertIsDisplayed()
    }

    @Test
    fun productCard_click_triggersCallback() {
        var clicked = false
        val product = Product(id = "p1", name = "Bird")

        composeTestRule.setContent {
            ProductCard(product = product, onClick = { clicked = true })
        }

        composeTestRule.onNodeWithTag("product_card_p1").performClick()
        assertTrue(clicked)
    }
}
```

---

## Worker Testing with WorkManager TestDriver

```kotlin
class SyncWorkerTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testSyncWorker() {
        val worker = TestListenableWorkerBuilder<SyncWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}
```

---

## Migration Testing with Room

```kotlin
@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate58To59() {
        var db = helper.createDatabase(TEST_DB, 58)
        
        // Insert data using SQLite to version 58 schema
        db.execSQL("INSERT INTO products (productId, name) VALUES ('p1', 'Old Bird')")
        db.close()

        // Migrate to 59
        db = helper.runMigrationsAndValidate(TEST_DB, 59, true, AppDatabase.MIGRATION_58_59)

        // Verify data and new table
        val cursor = db.query("SELECT * FROM show_records")
        assertNotNull(cursor)
        db.close()
    }
}
```

---

## Digital Farm Rendering Testing

```kotlin
class FarmCanvasRendererTest {
    // Canvas rendering is often tested via Screenshot Tests or checking internal state
    @Test
    fun `calculateBirdPosition moves bird toward target`() {
        val renderer = FarmCanvasRenderer()
        val current = Offset(0f, 0f)
        val target = Offset(100f, 100f)
        
        val next = renderer.calculateNextPosition(current, target, speed = 1f)
        
        assertTrue(next.x > current.x)
        assertTrue(next.y > current.y)
    }
}
```

---

## Evidence Order Flow Testing

```kotlin
class EvidenceOrderFlowTest {
    @Test
    fun `buyer payment moves order to PAYMENT_PROOF_SUBMITTED`() = runTest {
        val order = OrderEntity(id = "o1", status = OrderStatus.ADVANCE_PENDING)
        val repository = mockk<EvidenceOrderRepository>()
        
        coEvery { repository.submitPaymentProof(any(), any()) } returns Result.Success(Unit)
        
        // Implementation check
    }
}
```
