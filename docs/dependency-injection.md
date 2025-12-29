---
Version: 2.0
Last Updated: 2025-12-29
Audience: Android developers
Status: Active
---

# Dependency Injection (Hilt)

Version: 2.0
Last Updated: 2025-12-29
Audience: Android developers

---

## Why Hilt
- Standard DI for Android with lifecycle-aware components
- Boilerplate reduction vs manual DI
- Great testing support via `@TestInstallIn`

## Core Concepts
- Components: `SingletonComponent`, `ActivityRetainedComponent`, `ViewModelComponent`
- Modules: `@Module` + `@InstallIn`
- Scopes: `@Singleton`, `@ActivityRetainedScoped`, `@ViewModelScoped`
- Qualifiers: Distinguish multiple bindings of same type

## Module Organization
ROSTRY uses 20+ Hilt modules to organize dependencies:

### Core Modules
- `di/AppModule.kt` — App-level singletons (DataStore, Result handlers)
- `di/NetworkModule.kt` — Retrofit, OkHttp, Firebase
- `di/HttpModule.kt` — Service definitions, interceptors
- `di/DatabaseModule.kt` — Room database, migrations, DAOs
- `di/RepositoryModule.kt` — Interface-to-Implementation bindings
- `di/WorkerModule.kt` — WorkManager factory components

### Feature Modules
- `di/AnalyticsModule.kt` — Analytics emitters and trackers
- `di/SocialModule.kt` — Social platform and messaging
- `di/CoilModule.kt` — Image loading configuration
- `di/LocationModule.kt` — FusedLocationProvider, geocoder
- `di/PlacesModule.kt` — Google Places SDK lazy initialization
- `di/UpgradeModule.kt` — Role upgrade and migration logic
- `di/VerificationModule.kt` — Farm/Identity verification components
- `di/LoveabilityModule.kt` — Gamification and UX features
- `di/AuthModuleNew.kt` — Multi-provider authentication
- `di/SessionModule.kt` — Session management and token storage
- `di/ViewModelModule.kt` — Shared ViewModel dependencies

## Examples

### Lazy Initialization (PlacesModule)
Used to improve startup performance by deferring SDK initialization until first use.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {
    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context, initializer: PlacesInitializer): PlacesClient {
        initializer.initialize() // Lazy init
        return Places.createClient(context)
    }
}
```

### Analytics Tracking (AnalyticsModule)
Provides multiple trackers for different destinations.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideFirebaseTracker(): AnalyticsTracker = FirebaseAnalyticsTracker()

    @Provides
    @Singleton
    fun provideCustomTracker(db: AppDatabase): AnalyticsTracker = LocalDbTracker(db.analyticsDao())
}
```
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RostryDatabase =
        Room.databaseBuilder(context, RostryDatabase::class.java, "rostry.db").build()

    @Provides fun productDao(db: RostryDatabase): ProductDao = db.productDao()
}
```

### Qualifiers
```kotlin
@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource

@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSource
```

### ViewModel Injection
```kotlin
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val analytics: AnalyticsRepository,
) : ViewModel()
```

## Testing with Hilt
```kotlin
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
abstract class TestRepositoryModule {
    @Binds @Singleton
    abstract fun bindProductRepository(fake: FakeProductRepository): ProductRepository
}
```

## Troubleshooting
- Missing binding: Ensure module is in correct `@InstallIn`
- Duplicate bindings: Remove extra provides/binds or use qualifiers
- KSP/Room: Ensure TypeConverters are imported in `@Database`

See also: `architecture.md`, `testing-strategy.md`.
