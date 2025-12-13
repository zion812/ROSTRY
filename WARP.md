# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

ROSTRY is an Android AgriTech marketplace application for the poultry industry, combining social networking, e-commerce, and farm management tools. Built with Kotlin, Jetpack Compose, and modern Android development practices.

**Version**: 1.0.0 (80% production-ready)  
**Min SDK**: 24 | **Target SDK**: 36 | **Compile SDK**: 36

## Essential Commands

### Build & Run
```bash
# Clean build
./gradlew clean build

# Debug APK
./gradlew assembleDebug

# Release APK/AAB (auto-increments versionCode)
./gradlew assembleRelease

# Check APK size (fails if >50MB)
./gradlew checkApkSize
```

### Testing
```bash
# Unit tests (app/src/test/)
./gradlew test

# Instrumented tests (app/src/androidTest/)
./gradlew connectedAndroidTest

# Single test file (example)
./gradlew test --tests "com.rio.rostry.ui.auth.AuthViewModelTest"

# Test coverage report (outputs to app/build/reports/jacoco/)
./gradlew jacocoTestReport

# Lint with ktlint and detekt
./gradlew ktlintCheck detekt
```

### Documentation
```bash
# Generate API docs (outputs to docs/api/)
./gradlew dokkaHtmlMultiModule

# Module-level docs (outputs to app/build/dokka/html/)
./gradlew :app:dokkaHtml
```

### Version Management
```bash
# Manually bump version code
./gradlew bumpVersionCode
```

## Architecture

### Layer Structure
```
┌──────────────────┐
│  Compose UI      │  Feature-based packages: ui/<feature>/
├──────────────────┤
│  ViewModels      │  @HiltViewModel, StateFlow, SavedStateHandle
├──────────────────┤
│  Repositories    │  Interface + Impl pattern, offline-first
├──────────────────┤
│  Data Sources    │  Room (SQLCipher), Firebase, Retrofit
└──────────────────┘
```

**Pattern**: Clean Architecture + MVVM  
**DI**: Hilt with modules in `di/` package  
**State**: StateFlow/SharedFlow for reactive updates  
**Offline**: Room as source of truth, bidirectional sync with Firebase

### Key Packages
- `ui/`: Compose screens and ViewModels (feature-organized)
  - `ui/auth/`, `ui/marketplace/`, `ui/monitoring/`, `ui/social/`, etc.
- `data/`: Repositories, database entities, DAOs, sync logic
  - `data/repository/`: Interface + Implementation for each domain
  - `data/database/entity/`: 60+ Room entities
  - `data/database/dao/`: Data Access Objects
- `di/`: Hilt modules for DI configuration
  - `AppModule`, `DatabaseModule`, `RepositoryModule`, `NetworkModule`, etc.
- `workers/`: 20+ WorkManager background jobs
  - `SyncWorker`, `FarmMonitoringWorker`, `VaccinationReminderWorker`, etc.
- `utils/`: Validation, encryption, analytics, notifications
- `domain/`: Use cases and business logic

### Database
- **ORM**: Room with SQLCipher encryption (AES-256)
- **Entities**: 60+ tables covering users, products, social, farm monitoring, analytics
- **Migrations**: Schema versions in `app/schemas/` for testing
- **Passphrase**: Derived from Android Keystore

### Background Jobs
Scheduled in `RostryApp.onCreate()` with 3-second startup delay:
- **SyncWorker**: Bidirectional Room/Firebase sync every 6 hours
- **LifecycleWorker**: Farm milestone reminders
- **TransferTimeoutWorker**: SLA enforcement for ownership transfers
- **ModerationWorker**: Content scanning
- **VaccinationReminderWorker**: Vaccination schedule notifications
- **FarmMonitoringWorker**: Daily health checks and alerts
- **CommunityEngagementWorker**: Personalized recommendations every 12 hours
- **AnalyticsAggregationWorker** & **ReportingWorker**: Dashboard metrics
- Workers use Hilt injection via `HiltWorkerFactory`

## Code Patterns

### New Screen Template
```kotlin
@Composable
fun MyNewScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    
    // Compose UI here
}
```

### ViewModel Pattern
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(MyUiState())
    val state: StateFlow<MyUiState> = _state.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            repository.getData()
                .collect { result ->
                    _state.update { it.copy(data = result) }
                }
        }
    }
}
```

### Repository Pattern
```kotlin
interface MyRepository {
    fun getData(): Flow<List<MyData>>
}

class MyRepositoryImpl @Inject constructor(
    private val dao: MyDao,
    private val firestore: FirebaseFirestore
) : MyRepository {
    override fun getData(): Flow<List<MyData>> = flow {
        // Offline-first: emit cached data, then sync
        emit(dao.getAll())
        syncWithFirebase()
        emit(dao.getAll())
    }
}
```

### Dependency Injection
- Define interface + implementation for repositories
- Bind in `RepositoryModule` using `@Binds`
- Provide singletons in respective modules (e.g., `DatabaseModule`, `FirebaseModule`)
- Use qualifiers for multiple implementations (e.g., `@LocalDataSource`, `@RemoteDataSource`)

### Navigation
- Routes defined in `ui/navigation/Routes.kt`
- Single Activity with Compose Navigation in `AppNavHost.kt`
- Role-based routing after auth (Farmer, Enthusiast, General user)
- Deep link support enabled

### Testing
- **Unit tests**: ViewModels, Repositories, Utils in `app/src/test/`
- **Integration tests**: Database migrations, Auth flows in `app/src/androidTest/`
- **Tools**: JUnit 5, Mockito, MockK, kotlinx-coroutines-test, Turbine (Flow testing)
- **Hilt testing**: Custom `HiltTestRunner` at `app/src/androidTest/java/com/rio/rostry/HiltTestRunner.kt`
- **Target**: 80% unit, 15% integration, 5% UI tests

## Configuration & Setup

### Required Files
1. **google-services.json**: Place in `app/` directory
2. **local.properties**: Copy from `local.properties.template` and add:
   ```properties
   MAPS_API_KEY=your_google_maps_android_key
   MAPS_JS_API_KEY=your_google_maps_js_key  # Optional, falls back to MAPS_API_KEY
   ```

### Firebase Requirements
- Enable Auth providers: Phone, Email/Password, Google Sign-In
- Add SHA-1 fingerprint to Firebase project for Google Sign-In
- Configure Firestore security rules (require `phone_number` claim)
- Enable Storage, Functions, Cloud Messaging, Crashlytics

### Phone Authentication
- **Policy**: All users must verify phone number before accessing app
- Firebase Auth phone OTP with FirebaseUI drop-in UI
- `setAppVerificationDisabledForTesting(true)` in debug builds

### App Check
- **Debug**: `DebugAppCheckProviderFactory` installed synchronously
- **Release**: `PlayIntegrityAppCheckProviderFactory` installed on IO thread
- Debug token logged in Logcat (search "App Check Debug Token")

## Common Tasks

### Adding a New Feature
1. Create feature package in `ui/<feature>/`
2. Define ViewModel extending `ViewModel` or `BaseViewModel`
3. Create Repository interface in `data/repository/`
4. Implement repository with Room DAO + Firebase integration
5. Bind repository in `di/RepositoryModule.kt`
6. Add navigation route in `ui/navigation/Routes.kt`
7. Write unit tests for ViewModel and Repository

### Adding a Database Entity
1. Create entity in `data/database/entity/`
2. Create DAO in `data/database/dao/`
3. Register DAO in `AppDatabase.kt`
4. Create migration in `AppDatabase` (increment version)
5. Export schema: build and verify in `app/schemas/`
6. Write migration test in `app/src/androidTest/`

### Adding a Background Worker
1. Create worker in `workers/` extending `CoroutineWorker`
2. Inject dependencies via `@HiltWorker` and `@AssistedInject`
3. Schedule in `RostryApp.setupSessionBasedWorkers()`
4. Define constraints (network, battery, storage)
5. Use `PeriodicWorkRequestBuilder` for recurring tasks

### Debugging Sync Issues
- Check `SyncStatusViewModel` for sync state
- View sync logs in Logcat (filter "SyncWorker")
- Use `ui/sync/SyncIssuesScreen.kt` for conflict resolution
- Verify Firebase security rules if sync fails

## Critical Files

- **Application Entry**: `RostryApp.kt` - Worker scheduling, App Check, Coil, Places SDK
- **Main Activity**: `MainActivity.kt` - Single Activity, Navigation, Theme
- **Database**: `data/database/AppDatabase.kt` - 60+ entities, migrations, converters
- **Navigation**: `ui/navigation/AppNavHost.kt`, `ui/navigation/Routes.kt`
- **Session**: `session/SessionManager.kt` - Auth state, role management
- **Build Config**: `app/build.gradle.kts` - Dependencies, ProGuard, JaCoCo, Dokka
- **Documentation Index**: `docs/README-docs.md` - Links to all guides

## Documentation

**Comprehensive Docs** available in `docs/`:
- **System Blueprint**: `SYSTEM_BLUEPRINT.md` - Complete SINF document
- **Quick Start**: `QUICK_START.md` - 5-minute local setup
- **Cheat Sheet**: `CHEAT_SHEET.md` - Copy-paste commands
- **Architecture**: `docs/architecture.md` - Detailed layer breakdown
- **Testing**: `docs/testing-strategy.md` - Testing patterns and tools
- **Security**: `docs/security-encryption.md` - SQLCipher, root detection
- **Feature Docs**: `docs/ai-personalization.md`, `docs/gamification.md`, `docs/traceability.md`, etc.

## Development Workflow

### Starting Development
1. Clone repo and open in Android Studio
2. Sync Gradle (may take 2-5 min on first run)
3. Add `google-services.json` and configure `local.properties`
4. Run on emulator or device
5. Use demo account: `demo_farmer1` / `password123`

### Before Committing
1. Run `./gradlew ktlintCheck detekt` (fix any issues)
2. Run `./gradlew test` (ensure tests pass)
3. If editing database, export schema and write migration test
4. Update `CHANGELOG.md` if adding features

### Release Build Checklist
- Verify `MAPS_API_KEY` is not placeholder
- Ensure ProGuard rules cover all reflection-based libraries
- Run `./gradlew assembleRelease` (auto-increments `versionCode`)
- Test release APK on physical device
- Verify Crashlytics mapping file upload

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Build fails | `./gradlew clean` then rebuild |
| Gradle sync fails | File → Invalidate Caches / Restart |
| App crashes on launch | Check `google-services.json` is present |
| Firebase errors | Verify SHA-1 fingerprint in Firebase Console |
| Maps not loading | Verify `MAPS_API_KEY` in `local.properties` |
| Phone auth fails in debug | Check App Check debug token in Firebase Console |
| Database migration crash | Check `AppDatabase` version and migration logic |
| Worker not running | Check WorkManager constraints and battery optimization settings |

## Code Style

- Follow Kotlin coding conventions
- Use `ktlint` and `detekt` for linting
- Prefer `Flow` over LiveData for reactive streams
- Use `StateFlow` for UI state, `SharedFlow` for events
- All repositories return `Flow<T>` or `Flow<Result<T>>`
- Avoid blocking calls on main thread
- Use `viewModelScope` for ViewModel coroutines
- Use `@HiltViewModel` for ViewModels with Hilt injection

## Firebase Emulator (Optional)

For local testing without cloud Firebase:
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Start emulators (Auth, Firestore, Storage, Functions)
firebase emulators:start

# Connect app to emulators in debug build:
# Add to RostryApp.onCreate() before Firebase usage:
# FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
# FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
```

## Project Status & Roadmap

- See `CHANGELOG.md` for recent changes
- See `ROADMAP.md` for planned features
- Current focus: Marketplace refinements, Analytics dashboards, UI/UX polish

## Support

- Internal documentation: `docs/` directory
- GitHub Issues for bugs/feature requests
- Code of Conduct: `CONTRIBUTING.md`
- Security Policy: `SECURITY.md` (report vulnerabilities privately)

