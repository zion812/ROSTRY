# ROSTRY Android

ROSTRY is a multi-module, offline-first AgriTech marketplace and farm management app for premium poultry. This codebase is Kotlin-first, Compose UI, and follows Clean Architecture (Domain/Data/Presentation) with MVVM and Hilt DI.

## Tech Stack
- Kotlin, Jetpack Compose, ViewBinding/DataBinding (for interop)
- Clean Architecture + MVVM
- Hilt for DI
- Room (local), Firebase (Auth, Firestore, Storage, Functions) for cloud
- WorkManager for background sync
- Retrofit/OkHttp/Moshi for networking
- Timber for logging
- Crashlytics for error reporting

## Modules
- `app/` – Application, flavors (dev/staging/prod), DI wiring, WorkManager schedule
- `core/` – Base classes, dispatchers, network monitor, error/resource types
- `domain/` – Entities, repository interfaces (pure Kotlin; uses coroutines `Flow`)
- `data/` – Room database/DAO, repository implementations, network/data sources
- `presentation/` – Compose UI, base viewmodel/fragment, navigation

## Requirements
- Min SDK 24, Target SDK 36
- JDK 17
- Android Gradle Plugin 8.13, Gradle 8.13

## Firebase Setup
1. Create a Firebase project and register 3 Android apps:
   - `com.rio.rostry` (prod)
   - `com.rio.rostry.dev` (dev)
   - `com.rio.rostry.stg` (staging)
2. Download `google-services.json` and place:
   - `app/google-services.json` (prod)
   - `app/src/dev/google-services.json` (dev)
   - `app/src/staging/google-services.json` (staging)
3. Ensure the Google Services plugin is applied (already configured in `app/build.gradle.kts`).

## Build/Run
```powershell
# From project root
.\gradlew.bat clean assembleDevDebug
```
Open in Android Studio and select a flavor: dev/staging/prod.

## Offline-First & Sync
- Room is the source of truth. `data/local/db/AppDatabase.kt` includes tables:
  - Users, Products, Orders, Transfers, CoinTransactions, Notifications, Outbox
- Outbox pattern queues mutations for retry. `OutboxSyncWorker` drains periodically with network constraints.
- `NetworkMonitor` observes connectivity for rural conditions.

## DI Modules
- `app/src/main/java/.../di/FirebaseModule.kt` provides Auth, Firestore, Storage, Functions, Analytics
- `app/src/main/java/.../di/NetworkModule.kt` provides Retrofit/OkHttp/Moshi
- `data/src/main/java/.../di/DataModule.kt` provides Room and repositories

## Base Architecture Classes
- `core/Resource.kt` and `core/Result.kt` – unified state types
- `core/BaseRepository.kt` – safeCall with error logging to Crashlytics via `ErrorLogger`
- `presentation/base/BaseViewModel.kt` – base VM with error state
- `presentation/base/BaseFragment.kt` – ViewBinding helper base

## Testing
- Test libs included: MockK, kotlinx-coroutines-test, Turbine
- Add unit tests under each module’s `src/test/`

## ProGuard/R8
- `app/proguard-rules.pro` includes keep rules for Hilt, Retrofit/Moshi, OkHttp, Firebase, WorkManager, Timber, Room.

## Environments
- Product flavors: `dev`, `staging`, `prod` with suffixes (`.dev`, `.stg`)
- Configure base URL per env in `NetworkModule` or via BuildConfig fields.

## Next Steps
- Implement concrete repositories for domain interfaces
- Add sync strategies (delta tokens, compression) for low bandwidth
- Add comprehensive input validation and analytics
- Add unit tests for DAOs and repositories
- Localize strings for EN/TE/TA/KN/HI
