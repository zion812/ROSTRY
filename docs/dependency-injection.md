# Dependency Injection (Hilt)

Version: 1.0
Last Updated: 2025-01-15
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
- `di/AppModule.kt` — app-level singletons
- `di/NetworkModule.kt` — Retrofit/Firebase/OkHttp
- `di/DatabaseModule.kt` — Room & DAOs
- `di/RepositoryModule.kt` — repository bindings
- `di/WorkerModule.kt` — WorkManager

## Examples
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
