---
Version: 2.0
Last Updated: 2025-12-29
Audience: Android developers
Status: Active
---

# Dependency Injection (Hilt)

Version: 2.1
Last Updated: 2026-01-13
Audience: Android developers

---

## Why Hilt
- Standard DI for Android with lifecycle-aware components
- Boilerplate reduction vs manual DI
- Great testing support via `@TestInstallIn`
- Compile-time validation of dependency graphs
- Integration with Android Architecture Components

## Core Concepts
- Components: `SingletonComponent`, `ActivityRetainedComponent`, `ViewModelComponent`
- Modules: `@Module` + `@InstallIn`
- Scopes: `@Singleton`, `@ActivityRetainedScoped`, `@ViewModelScoped`
- Qualifiers: Distinguish multiple bindings of same type
- Entry Points: Access dependencies from non-Hilt classes

## Module Organization
ROSTRY uses 21+ Hilt modules to organize dependencies:

### Core Modules
- `di/AppModule.kt` — App-level singletons (Context, SessionManager, Notifiers, Gson, FirebaseDatabase)
- `di/NetworkModule.kt` — Network client configuration
- `di/HttpModule.kt` — HTTP client and interceptor setup
- `di/DatabaseModule.kt` — Room database, migrations, DAOs
- `di/RepositoryModule.kt` — Interface-to-Implementation bindings for all repositories
- `di/WorkerBaseHelper.kt` — Worker base helper dependencies

### Feature Modules
- `di/AnalyticsModule.kt` — Analytics emitters and trackers
- `di/CoilModule.kt` — Image loading configuration
- `di/LocationModule.kt` — FusedLocationProvider, geocoder
- `di/PlacesModule.kt` — Google Places SDK lazy initialization
- `di/UpgradeModule.kt` — Role upgrade and migration logic
- `di/VerificationModule.kt` — Farm/Identity verification components
- `di/LoveabilityModule.kt` — Gamification and engagement features
- `di/AuthModuleNew.kt` — Multi-provider authentication
- `di/SessionModule.kt` — Session management and token storage
- `di/ViewModelModule.kt` — Shared ViewModel dependencies
- `di/NotifModule.kt` — Notification dependencies
- `di/RemoteModule.kt` — Remote data source dependencies
- `di/UtilsModule.kt` — Utility dependencies
- `di/AppEntryPoints.kt` — Hilt entry points for application components
- `di/MediaUploadInitializer.kt` — Media upload initialization

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

### Repository Binding (RepositoryModule)
RepositoryModule binds all 57+ repository interfaces to their implementations.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository

    // ... and so on for other repositories

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTrackingRepository(impl: TrackingRepositoryImpl): TrackingRepository

    // Many more repository bindings...
}
```

### Entry Points (AppEntryPoints)
Access dependencies from non-Hilt classes.

```kotlin
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoints {
    fun imageLoader(): ImageLoader
    fun mediaUploadInitializer(): MediaUploadInitializer
}
```

### Database Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): RostryDatabase {
        val passphrase = SQLiteDatabase.getBytes(
            // Load from secure storage
            context.getString(R.string.db_key).toCharArray()
        )

        return Room.databaseBuilder(
            context,
            RostryDatabase::class.java,
            "rostry.db"
        )
            .openHelperFactory(SupportFactory(passphrase))
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }

    @Provides
    fun provideProductDao(database: RostryDatabase): ProductDao {
        return database.productDao()
    }
}
```

### Qualifiers
```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Named(val value: String)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    @Named(RolePreferenceStorage.ROLE_PREFS_NAME)
    fun provideRoleSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        // Implementation
    }
}
```

### ViewModel Injection
```kotlin
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val analytics: AnalyticsRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel()  // Extends BaseViewModel for common error handling
```

### Payment Module
Separate module for payment gateway binding:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {
    @Provides
    @Singleton
    fun providePaymentGateway(): PaymentGateway = DefaultPaymentGateway()
}
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

## Current DI Practices in ROSTRY
- **21+ DI Modules** organizing dependencies across the application
- **RepositoryModule** binds all 57+ repository interfaces to implementations
- **BaseViewModel** pattern for common error handling across 114+ ViewModels
- **Entry Points** for accessing dependencies from non-Hilt classes
- **Qualifiers** for distinguishing multiple implementations of the same type
- **PaymentModule** for payment gateway binding
- **MediaUploadInitializer** for media upload initialization
- **AppEntryPoints** for accessing application-level dependencies

## Troubleshooting
- Missing binding: Ensure module is in correct `@InstallIn`
- Duplicate bindings: Remove extra provides/binds or use qualifiers
- KSP/Room: Ensure TypeConverters are imported in `@Database`
- Circular dependencies: Restructure to avoid circular references
- Scope mismatches: Ensure dependencies have compatible lifetimes

See also: `architecture.md`, `testing-strategy.md`, `state-management.md`.
