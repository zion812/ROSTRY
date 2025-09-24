# ROSTRY Android Application

ROSTRY is an AgriTech marketplace application built with modern Android development practices.

## Project Setup

1.  **Clone the repository.**
2.  **Firebase Setup**: Download your project's `google-services.json` file from the Firebase console and place it in the `ROSTRY/app/` directory.
3.  **Build the project**: Open the project in Android Studio. It should sync Gradle automatically. If not, trigger a manual Gradle sync.

## Documentation

Refer to the [Documentation Index](docs/README-docs.md) for detailed guides on architecture, feature domains, data contracts, and operational practices.

## Architecture

This project follows the **MVVM (Model-View-ViewModel)** architecture pattern, leveraging Android Jetpack components and Hilt for dependency injection.

### Core Components:

*   **UI Layer (Jetpack Compose)**: Views are built using Jetpack Compose. Composable functions observe StateFlows from ViewModels to react to data changes.
*   **ViewModels (`androidx.lifecycle.ViewModel`)**: Responsible for preparing and managing data for the UI. They expose data via `StateFlow` and handle user interactions by calling methods on repositories or use cases.
    *   `BaseViewModel`: Common base class for ViewModels, providing a `viewModelScope` for coroutines and basic error handling capabilities.
*   **Repositories**: Manage data operations. They abstract data sources (network, local database) and provide a clean API for ViewModels to access data.
    *   `BaseRepository`: Common base class for repositories, offering helper functions like `safeApiCall` and `safeCall` for consistent error handling and `Resource` wrapping.
    *   Implementations like `UserRepositoryImpl` and `ProductRepositoryImpl` demonstrate fetching data from Firebase Firestore and caching/serving it via Room.
*   **Data Layer**:
    *   **Room Database (`androidx.room`)**: Local persistence library, acting as the single source of truth for offline-first capabilities. Entities include `UserEntity`, `ProductEntity`, `OrderEntity`, etc.
    *   **DAOs (Data Access Objects)**: Interfaces defining database operations for each entity.
    *   **Firebase**: Used for backend services:
        *   Authentication
        *   Firestore (database)
        *   Storage (file uploads)
        *   Functions (server-side logic - to be implemented)
*   **Dependency Injection (Hilt)**: Manages dependencies throughout the application.
    *   `@HiltAndroidApp` in `RostryApp`.
    *   Modules: `AppModule`, `DatabaseModule`, `NetworkModule`, `RepositoryModule`, `ViewModelModule`.
*   **Coroutines & Flow**: Used for asynchronous operations and reactive data streams.
*   **WorkManager**: For background tasks like data synchronization (`SyncWorker`).
*   **`Resource` Sealed Class**: Wraps data with states (Success, Error, Loading) for robust UI updates and error handling.
*   **Timber**: For logging.

### Directory Structure:

*   `app/src/main/java/com/rio/rostry/`
    *   `RostryApp.kt`: Application class.
    *   `di/`: Hilt modules.
    *   `data/`:
        *   `base/`: `BaseRepository.kt`
        *   `database/`:
            *   `dao/`: Room DAO interfaces.
            *   `entity/`: Room entity classes.
            *   `AppDatabase.kt`: Room database definition.
        *   `repository/`: Repository interfaces and implementations.
    *   `ui/`:
        *   `base/`: `BaseViewModel.kt`
        *   `feature_name/`: Composables, ViewModels for specific features (e.g., `ui/main/MainViewModel.kt`).
    *   `domain/`: (Placeholder) Intended for use cases/interactors if business logic becomes more complex.
    *   `utils/`: Utility classes like `Resource.kt`.
    *   `workers/`: `WorkManager` worker classes (e.g., `SyncWorker.kt`).
*   `app/src/main/res/`: Android resources.
*   `app/build.gradle.kts`: App-level Gradle build script with dependencies.
*   `build.gradle.kts`: Project-level Gradle build script.
*   `app/proguard-rules.pro`: ProGuard rules for release builds.

## Building

*   **Debug builds**: Standard build process from Android Studio.
*   **Release builds**: Minification is enabled. ProGuard rules are in `app/proguard-rules.pro`.

## Testing

*   Unit tests will be placed in `app/src/test/java/`.
*   Android instrumented tests will be placed in `app/src/androidTest/java/`.
*   MockK is included for mocking dependencies in tests.

## Offline-First Approach

The application aims for an offline-first experience:
1.  **Room as Single Source of Truth**: The UI primarily observes data from the Room database.
2.  **Network for Synchronization**: Data is fetched from Firebase (Firestore) and synced to the Room database.
3.  **WorkManager for Background Sync**: `SyncWorker` will be responsible for periodic background data synchronization.
4.  **Repositories**: Handle the logic of fetching from network, updating Room, and providing data to ViewModels.

## Error Handling

*   The `Resource` sealed class (`Success`, `Error`, `Loading`) is used to communicate data states from repositories to ViewModels and then to the UI.
*   `BaseRepository` includes `safeApiCall` and `safeCall` to wrap calls in `Resource` and catch exceptions.
*   `BaseViewModel` provides a basic `handleError` mechanism.

## Further Development

*   Implement remaining repository interfaces and implementations.
*   Develop UI screens using Jetpack Compose.
*   Implement detailed ViewModel logic for each screen.
*   Flesh out `SyncWorker` for robust background data synchronization and conflict resolution.
*   Add comprehensive unit and instrumentation tests.
*   Implement input validation and network connectivity checks.
*   Set up Firebase Storage for file uploads and Firebase Functions for backend logic as needed.

