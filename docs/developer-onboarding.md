# Developer Onboarding Guide

**Version:** 2.0  
**Last Updated:** 2025-01-15  
**Audience:** New developers joining the ROSTRY project  
**Estimated Time:** 4-6 hours for complete setup and first contribution

---

Welcome to ROSTRY! This comprehensive guide will help you set up your development environment, understand the architecture, and make your first contribution.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Step-by-Step Setup](#step-by-step-setup)
- [Architecture Overview](#architecture-overview)
- [Key Concepts](#key-concepts)
- [Code Walkthroughs](#code-walkthroughs)
- [Common Tasks](#common-tasks)
- [Development Workflow](#development-workflow)
- [Debugging & Testing](#debugging--testing)
- [Troubleshooting](#troubleshooting)
- [Learning Resources](#learning-resources)
- [Onboarding Checklist](#onboarding-checklist)
- [Next Steps](#next-steps)

---

## Prerequisites

### System Requirements

**Minimum**:
- CPU: 64-bit processor (Intel i5 or AMD Ryzen 5)
- RAM: 8GB (16GB recommended for emulator)
- Storage: 10GB free space
- OS: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)

### Required Software

| Software | Version | Purpose |
|----------|---------|----------|
| **Android Studio** | Hedgehog (2023.1.1)+ | Primary IDE |
| **JDK** | OpenJDK 17 | Java runtime |
| **Git** | Latest stable | Version control |
| **Gradle** | 8.2+ | Build system (managed by wrapper) |

### Optional Tools

- **Firebase CLI**: Backend development (`npm install -g firebase-tools`)
- **Postman**: API testing
- **DB Browser for SQLite**: Database inspection
- **Scrcpy**: Device mirroring

### Accounts & Access

- **GitHub Account**: For repository access
- **Firebase Console Access**: Contact admin for project invitation
- **Google Cloud Console**: For Maps API key

---

## Step-by-Step Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/ROSTRY.git
cd ROSTRY
git checkout -b feat/your-name-setup
```

### 2. Android Studio Configuration

**Install Android Studio**:
1. Download from [developer.android.com/studio](https://developer.android.com/studio)
2. Run installer and follow setup wizard
3. Install recommended SDK components

**Configure SDK**:
1. **File** → **Settings** → **Appearance & Behavior** → **System Settings** → **Android SDK**
2. Install:
   - Android SDK Platform 33 (Android 13)
   - Android SDK Build-Tools 34.0.0
   - Android Emulator
   - Android SDK Platform-Tools

**Import Project**:
1. **File** → **Open** → Select ROSTRY folder
2. Wait for Gradle sync (5-10 minutes first time)
3. Update Gradle Plugin if prompted

### 3. JDK 17 Verification

```bash
# Check version
java -version
# Should show: openjdk version "17.x.x"
```

**Configure JDK in Android Studio**:
1. **File** → **Project Structure** → **SDK Location**
2. Verify **JDK location** points to JDK 17

### 4. Gradle Sync

**Initial Sync**:
- Automatic on project open
- Downloads ~500MB dependencies
- Watch **Build** panel for progress

**If Sync Fails**:
```bash
./gradlew clean
./gradlew --refresh-dependencies

# Windows:
.\\gradlew.bat clean
```

### 5. Firebase Setup

**Get google-services.json**:
1. Contact admin or download from [Firebase Console](https://console.firebase.google.com/)
2. Place at: `app/google-services.json`

**Complete Guide**: See [docs/firebase-setup.md](firebase-setup.md)

### 6. Environment Variables

**Create local.properties**:
```bash
cp local.properties.template local.properties
```

**Edit local.properties**:
```properties
MAPS_API_KEY=your_google_maps_api_key_here
```

**Get Maps API Key**: See [docs/api-keys-setup.md](api-keys-setup.md)

**⚠️ Never commit** `local.properties` (already gitignored)

### 7. First Build & Run

**Build**:
```bash
./gradlew assembleDebug
# Or: Build → Make Project (Ctrl+F9)
```

**Run on Emulator**:
1. **Tools** → **Device Manager**
2. Create: Pixel 6, API 33
3. Click **Run** (Shift+F10)

**Run on Device**:
1. Enable **Developer Options** + **USB Debugging**
2. Connect via USB
3. Select device and **Run**

**First Launch**:
- Phone auth required (use demo mode for testing)
- Explore via bottom navigation

---

## Architecture Overview

ROSTRY follows **Clean Architecture** with **MVVM** pattern.

### Architectural Layers

```
┌─────────────────────────────────────────┐
│      UI Layer (Jetpack Compose)         │
│  • Composables (Screens)                │
│  • ViewModels (State Management)        │
└─────────────────┬───────────────────────┘
                  │ StateFlow
┌─────────────────┴───────────────────────┐
│        Domain Layer (Optional)          │
│  • Use Cases                            │
│  • Business Logic                       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────┴───────────────────────┐
│           Data Layer                    │
│  • Repositories                         │
│  • Data Sources (Room, Firebase)        │
└─────────────────────────────────────────┘
```

### Key Patterns

- **MVVM**: UI observes ViewModel via StateFlow
- **Repository**: Abstracts data sources
- **DI**: Hilt manages dependencies
- **Offline-First**: Room as source of truth
- **Reactive**: Kotlin Flow for streams

**Full Details**: [docs/architecture.md](architecture.md)

### Navigation Structure

```
AppNavHost
├── AuthNavGraph
├── RoleNavGraph (Role-based)
│   ├── GeneralNavGraph
│   ├── FarmerNavGraph
│   └── EnthusiastNavGraph
└── MainScreen (Bottom nav)
```

### Dependency Injection

**Hilt Modules**:
- `di/AppModule.kt` - App-level
- `di/NetworkModule.kt` - Retrofit, Firebase
- `di/DatabaseModule.kt` - Room
- `di/RepositoryModule.kt` - Repositories

**Scopes**:
- `@Singleton` - App lifetime
- `@ViewModelScoped` - ViewModel lifetime
- `@ActivityRetainedScoped` - Survives config changes

---

## Key Concepts

### 1. Hilt Dependency Injection

**Basic Usage**:
```kotlin
@HiltAndroidApp
class RostryApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel()
```

### 2. Room Database

**Components**:
- **Entity**: Table definition
- **DAO**: Query interface
- **Database**: Database holder

**Example**:
```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double
)

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAll(): Flow<List<ProductEntity>>
}
```

**Encryption**: Using SQLCipher - see [docs/security-encryption.md](security-encryption.md)

### 3. WorkManager

**Background Tasks**:
```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: SyncRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            repository.syncData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

### 4. Firebase Integration

**Services**:
- **Authentication**: Phone auth
- **Firestore**: NoSQL database
- **Storage**: Media files
- **Cloud Functions**: Backend logic
- **FCM**: Push notifications

### 5. Navigation

**Routes**:
```kotlin
object Routes {
    const val HOME = "home"
    const val PRODUCT_DETAIL = "product/{productId}"
}

navController.navigate("product/123")
```

### 6. StateFlow & Flow

**ViewModel Pattern**:
```kotlin
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.getData()
                .collect { data ->
                    _uiState.update { it.copy(data = data) }
                }
        }
    }
}
```

### 7. Repository Pattern

**Interface-Based**:
```kotlin
interface UserRepository {
    suspend fun getUser(id: String): Result<User>
    fun observeUser(id: String): Flow<User?>
}

class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
    private val firestore: FirebaseFirestore
) : UserRepository {
    // Implementation
}
```

---

## Code Walkthroughs

### Adding a New Screen

**1. Create Composable** (`ui/myfeature/MyFeatureScreen.kt`):
```kotlin
@Composable
fun MyFeatureScreen(
    onNavigateBack: () -> Unit,
    viewModel: MyFeatureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Feature") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text("Error: ${uiState.error}")
                else -> Text("Data: ${uiState.data}")
            }
        }
    }
}
```

**2. Create ViewModel** (`ui/myfeature/MyFeatureViewModel.kt`):
```kotlin
data class MyFeatureUiState(
    val isLoading: Boolean = false,
    val data: String? = null,
    val error: String? = null
)

@HiltViewModel
class MyFeatureViewModel @Inject constructor(
    private val repository: MyFeatureRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyFeatureUiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getData()
                .onSuccess { data ->
                    _uiState.update { 
                        it.copy(isLoading = false, data = data)
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }
}
```

**3. Add Route** (`navigation/Routes.kt`):
```kotlin
object Routes {
    const val MY_FEATURE = "my_feature"
}
```

**4. Add to NavHost** (`navigation/AppNavHost.kt`):
```kotlin
composable(Routes.MY_FEATURE) {
    MyFeatureScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}
```

### Creating a Repository

**1. Define Interface** (`data/myfeature/MyFeatureRepository.kt`):
```kotlin
interface MyFeatureRepository {
    suspend fun getData(): Result<String>
    fun observeData(): Flow<String>
    suspend fun saveData(data: String): Result<Unit>
}
```

**2. Implement** (`data/myfeature/MyFeatureRepositoryImpl.kt`):
```kotlin
class MyFeatureRepositoryImpl @Inject constructor(
    private val dao: MyFeatureDao,
    private val remoteSource: MyFeatureRemoteDataSource
) : MyFeatureRepository {
    override suspend fun getData(): Result<String> {
        return try {
            // Try local first
            dao.getData()?.let {
                return Result.Success(it.toModel())
            }
            // Fetch remote
            val remote = remoteSource.fetchData()
            dao.insert(remote.toEntity())
            Result.Success(remote)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
```

**3. Bind with Hilt** (`di/RepositoryModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMyFeatureRepository(
        impl: MyFeatureRepositoryImpl
    ): MyFeatureRepository
}
```

### Adding a Background Worker

**1. Create Worker** (`worker/MySyncWorker.kt`):
```kotlin
@HiltWorker
class MySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: MyFeatureRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            repository.syncData()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
```

**2. Schedule Worker**:
```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

val request = PeriodicWorkRequestBuilder<MySyncWorker>(
    repeatInterval = 15,
    repeatIntervalTimeUnit = TimeUnit.MINUTES
).setConstraints(constraints).build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "MySyncWork",
    ExistingPeriodicWorkPolicy.KEEP,
    request
)
```

### Database Migration

**Scenario**: Add column to existing table

**1. Update Entity**:
```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String = "General" // New column
)
```

**2. Increment Version**:
```kotlin
@Database(entities = [ProductEntity::class], version = 17)
abstract class RostryDatabase : RoomDatabase()
```

**3. Define Migration**:
```kotlin
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE products ADD COLUMN category TEXT NOT NULL DEFAULT 'General'"
        )
    }
}
```

**Full Guide**: [docs/database-migrations.md](database-migrations.md)

---

## Common Tasks

### Add Entity & DAO

**Entity**:
```kotlin
@Entity(tableName = "my_entities")
data class MyEntity(
    @PrimaryKey val id: String,
    val name: String
)
```

**DAO**:
```kotlin
@Dao
interface MyEntityDao {
    @Query("SELECT * FROM my_entities")
    fun getAll(): Flow<List<MyEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MyEntity)
}
```

### Write Unit Tests

**ViewModel Test**:
```kotlin
@ExperimentalCoroutinesTest
class MyViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MyViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val repository = mockk<MyRepository>()
        viewModel = MyViewModel(repository)
    }
    
    @Test
    fun `loadData success updates state`() = runTest {
        // Test implementation
    }
}
```

**More**: [docs/testing-strategy.md](testing-strategy.md)

### Add Navigation with Arguments

**Route**:
```kotlin
const val PRODUCT_DETAIL = "product/{productId}"
fun productDetail(id: String) = "product/$id"
```

**NavHost**:
```kotlin
composable(
    route = Routes.PRODUCT_DETAIL,
    arguments = listOf(
        navArgument("productId") { type = NavType.StringType }
    )
) { backStackEntry ->
    val productId = backStackEntry.arguments?.getString("productId") ?: ""
    ProductDetailScreen(productId = productId)
}
```

---

## Development Workflow

### Branching Strategy

**Branch Naming**:
- `feat/feature-name` - New features
- `fix/bug-description` - Bug fixes
- `refactor/what-changed` - Code refactoring
- `docs/what-updated` - Documentation

**Process**:
```bash
# Create branch
git checkout -b feat/my-feature

# Make changes, commit often
git add .
git commit -m "feat: add product filter"

# Push and create PR
git push origin feat/my-feature
```

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add product filter
fix: resolve crash on empty cart
refactor: simplify auth logic
docs: update API documentation
test: add unit tests for repository
```

### Code Review

**Before PR**:
- [ ] Code follows [CODE_STYLE.md](../CODE_STYLE.md)
- [ ] Tests pass locally
- [ ] New tests added
- [ ] Documentation updated
- [ ] No merge conflicts

**PR Template**: Use GitHub PR template

**Review Process**: See [CONTRIBUTING.md](../CONTRIBUTING.md)

### Pre-PR Testing

```bash
# Run all checks
./gradlew ktlintCheck detekt test

# Build release
./gradlew assembleRelease
```

---

## Debugging & Testing

### Debugging Tools

**Logcat**:
- **View** → **Tool Windows** → **Logcat**
- Filter by tag, log level
- Use Timber for logging

**Network Inspector**:
- **View** → **Tool Windows** → **App Inspection** → **Network Inspector**
- Monitor API calls, responses

**Database Inspector**:
- **View** → **Tool Windows** → **App Inspection** → **Database Inspector**
- Query Room database in real-time

### Running Tests

**Unit Tests**:
```bash
./gradlew test
./gradlew test --tests "MyViewModelTest"
```

**Instrumentation Tests**:
```bash
./gradlew connectedAndroidTest
```

**Coverage**:
```bash
./gradlew jacocoTestReport
# Report: app/build/reports/jacoco/
```

**Testing Guide**: [docs/testing-strategy.md](testing-strategy.md)

---

## Troubleshooting

### Build Fails

**Gradle sync issues**:
```bash
./gradlew clean
./gradlew --refresh-dependencies
# Invalidate Caches: File → Invalidate Caches
```

**Dependency conflicts**:
```bash
./gradlew dependencies
# Check dependency tree
```

### Firebase Errors

**google-services.json missing**:
- Verify file at `app/google-services.json`
- Check Firebase project ID matches

**Auth errors**:
- Enable Phone Auth in Firebase Console
- Add test phone numbers

### App Crashes

**Check Logcat**:
- Filter by "Error" or "Exception"
- Look for stack traces

**Common Issues**:
- Missing Firebase config
- Incorrect API keys
- Database migration errors

**Full Guide**: [docs/troubleshooting.md](troubleshooting.md)

---

## Learning Resources

### Internal Documentation

**Start Here**:
1. [README.md](../README.md) - Project overview
2. [docs/README-docs.md](README-docs.md) - Documentation index
3. [docs/architecture.md](architecture.md) - System architecture
4. [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines
5. [CODE_STYLE.md](../CODE_STYLE.md) - Code conventions

**Feature-Specific**:
- [docs/firebase-setup.md](firebase-setup.md) - Firebase configuration
- [docs/testing-strategy.md](testing-strategy.md) - Testing approach
- [docs/security-encryption.md](security-encryption.md) - Security practices
- [docs/database-migrations.md](database-migrations.md) - Database evolution

### External Resources

**Android/Kotlin**:
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)

**Libraries**:
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Firebase Documentation](https://firebase.google.com/docs)

### Recommended Reading Order

**Day 1**:
1. This onboarding guide
2. [README.md](../README.md)
3. [docs/architecture.md](architecture.md)

**Week 1**:
1. [CONTRIBUTING.md](../CONTRIBUTING.md)
2. [CODE_STYLE.md](../CODE_STYLE.md)
3. Feature-specific docs as needed

---

## Onboarding Checklist

### Day 1: Environment Setup

- [ ] Clone repository
- [ ] Install Android Studio & JDK 17
- [ ] Configure SDK components
- [ ] Gradle sync successful
- [ ] Add `google-services.json`
- [ ] Configure `local.properties`
- [ ] First successful build
- [ ] Run app on emulator/device
- [ ] Explore app features
- [ ] Read README.md

### Week 1: Understanding the Codebase

- [ ] Read architecture.md
- [ ] Understand navigation structure
- [ ] Explore key feature packages
- [ ] Review ViewModel examples
- [ ] Review Repository examples
- [ ] Run existing tests
- [ ] Set up debugging tools
- [ ] Read CONTRIBUTING.md
- [ ] Read CODE_STYLE.md
- [ ] Join team communication channels

### First Contribution

- [ ] Find a "good-first-issue"
- [ ] Create feature branch
- [ ] Make changes following style guide
- [ ] Write tests for changes
- [ ] Run all tests locally
- [ ] Update documentation if needed
- [ ] Create pull request
- [ ] Address code review feedback
- [ ] Celebrate first merged PR! 🎉

---

## Next Steps

### Good First Issues

Look for GitHub issues tagged:
- `good-first-issue`
- `documentation`
- `help-wanted`

### Areas to Explore

- **UI**: Jetpack Compose screens in `ui/` package
- **Data**: Repositories in `data/` package
- **Workers**: Background jobs in `worker/` package
- **Navigation**: Nav graphs in `navigation/` package
- **Tests**: Unit tests in `test/` and `androidTest/`

### Communication Channels

- **GitHub Issues**: Bug reports, feature requests
- **GitHub Discussions**: Questions, ideas
- **Pull Requests**: Code contributions
- **Team Chat**: Ask your mentor for invite

### Getting Help

- **Documentation**: Check [docs/README-docs.md](README-docs.md)
- **Troubleshooting**: See [docs/troubleshooting.md](troubleshooting.md)
- **Mentor**: Reach out to your assigned mentor
- **Team**: Ask in team chat
- **GitHub**: Create discussion or issue

---

**Welcome to the ROSTRY team! We're excited to have you on board.** 🚀

For questions or feedback on this guide, open an issue or PR.
