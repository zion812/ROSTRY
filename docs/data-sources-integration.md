---
Version: 1.0
Last Updated: 2026-02-05
Audience: Developers
Status: Active
Related_Docs: [data-layer-architecture.md, cache-management.md, fetcher-system.md, architecture.md]
Tags: [data-sources, integration, remote, local, sync]
---

# Data Sources Integration Guide

## Overview

The ROSTRY application integrates multiple data sources including Firebase services, external APIs, and local storage. This document outlines the integration patterns, configuration, and best practices for connecting to various data sources.

## Data Source Categories

### 1. Firebase Integration

#### Firebase Services Used
- **Firebase Auth**: User authentication with phone OTP
- **Firebase Firestore**: Real-time database for user profiles, products, transfers, social data
- **Firebase Storage**: File uploads for images, videos, documents
- **Firebase Functions**: Server-side logic for transfers, payments, moderation
- **Firebase Cloud Messaging (FCM)**: Push notification delivery
- **Firebase App Check**: App attestation and protection

#### Configuration
```kotlin
// FirebaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
    
    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return Firebase.functions
    }
}
```

#### Security Rules
```javascript
// Firestore security rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Products can be read by anyone, but only created by authenticated users
    match /products/{productId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && 
        resource.data.sellerId == request.auth.uid;
    }
  }
}
```

#### Cloud Functions
```typescript
// functions/src/index.ts
import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

// Verify transfer function
export const verifyTransfer = functions.https.onCall(async (data, context) => {
  // Verify user is authenticated
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
  }
  
  // Process transfer verification
  // Implementation details...
});

// Process payment function
export const processPayment = functions.https.onCall(async (data, context) => {
  // Payment processing logic
});
```

### 2. External API Integration

#### Weather API Integration
```kotlin
// WeatherRepositoryImpl.kt
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val cacheManager: CacheManager
) : WeatherRepository {
    
    override suspend fun getWeather(location: String): Resource<WeatherData> {
        return safeApiCall {
            // Check cache first
            val cached = cacheManager.get<WeatherData>("weather_$location")
            if (cached != null) {
                return@safeApiCall cached
            }
            
            // Fetch from API
            val response = weatherApi.getCurrentWeather(location)
            
            // Cache for 15 minutes
            cacheManager.put("weather_$location", response, CachePolicy(ttl = 15.minutes))
            
            response
        }
    }
}

// WeatherApi.kt
interface WeatherApi {
    @GET("/current")
    suspend fun getCurrentWeather(
        @Query("location") location: String,
        @Query("apiKey") apiKey: String
    ): WeatherData
}
```

#### Google Maps Platform Integration
```kotlin
// LocationModule.kt
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
    
    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.getDefault())
    }
}

// PlacesModule.kt
@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {
    
    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.GOOGLE_MAPS_API_KEY)
        }
        return Places.createClient(context)
    }
}
```

#### Payment Gateway Integration
```kotlin
// PaymentModule.kt
@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {
    
    @Provides
    @Singleton
    fun providePaymentGateway(): PaymentGateway {
        return DefaultPaymentGateway(BuildConfig.PAYMENT_API_KEY)
    }
}

// PaymentRepositoryImpl.kt
@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentGateway: PaymentGateway,
    private val firebaseFunctions: FirebaseFunctions
) : PaymentRepository {
    
    override suspend fun processPayment(paymentRequest: PaymentRequest): Resource<PaymentResult> {
        return safeApiCall {
            // Process payment through gateway
            val gatewayResult = paymentGateway.processPayment(paymentRequest)
            
            // Verify payment through Firebase Function
            val verificationResult = firebaseFunctions
                .getHttpsCallable("verifyPayment")
                .call(gatewayResult.paymentId)
                .await()
            
            PaymentResult(
                paymentId = gatewayResult.paymentId,
                status = gatewayResult.status,
                verification = verificationResult.data as Map<String, Any>
            )
        }
    }
}
```

### 3. Service Layer Integration

#### Voice Logging Service
```kotlin
// VoiceLogService.kt
interface VoiceLogService {
    suspend fun startRecording(): Resource<Unit>
    suspend fun stopRecording(): Resource<VoiceLog>
    suspend fun uploadVoiceLog(voiceLog: VoiceLog): Resource<String>
}

// VoiceLogRepositoryImpl.kt
@Singleton
class VoiceLogRepositoryImpl @Inject constructor(
    private val voiceLogService: VoiceLogService,
    private val storage: FirebaseStorage
) : VoiceLogRepository {
    
    override suspend fun createVoiceLog(audioFile: File): Resource<String> {
        return safeCall {
            // Upload to Firebase Storage
            val uploadTask = storage
                .getReference("voice_logs/${UUID.randomUUID()}.mp3")
                .putFile(audioFile.toUri())
            
            val downloadUrl = uploadTask.await().storage.downloadUrl.await()
            downloadUrl.toString()
        }
    }
}
```

#### Backup Service
```kotlin
// BackupService.kt
interface BackupService {
    suspend fun createBackup(): Resource<BackupResult>
    suspend fun restoreBackup(backupId: String): Resource<Unit>
    suspend fun listBackups(): Resource<List<BackupInfo>>
}

// BackupRepositoryImpl.kt
@Singleton
class BackupRepositoryImpl @Inject constructor(
    private val backupService: BackupService,
    private val storage: FirebaseStorage
) : BackupRepository {
    
    override suspend fun createUserDataBackup(): Resource<String> {
        return safeCall {
            // Create backup of user data
            val backupResult = backupService.createBackup()
            
            if (backupResult is Resource.Success) {
                // Upload backup to Firebase Storage
                val uploadTask = storage
                    .getReference("backups/${backupResult.data.backupId}.json")
                    .putBytes(backupResult.data.data.toByteArray())
                
                val downloadUrl = uploadTask.await().storage.downloadUrl.await()
                downloadUrl.toString()
            } else {
                throw Exception("Backup creation failed")
            }
        }
    }
}
```

## Data Source Selection Strategies

### 1. Context-Aware Data Source Selection
```kotlin
class DataSourceSelector @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val connectivityManager: ConnectivityManager
) {
    
    fun selectDataSource(context: DataSourceContext): DataSource {
        return when {
            !isConnected() -> localDataSource
            context.requiresRealtime -> remoteDataSource
            context.offlineMode -> localDataSource
            context.cacheAvailable -> localDataSource
            else -> remoteDataSource
        }
    }
    
    private fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
```

### 2. Priority-Based Selection
```kotlin
enum class DataSourcePriority {
    LOCAL_FIRST,
    REMOTE_FIRST,
    BALANCED
}

class PriorityBasedDataSourceSelector @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    
    suspend fun <T> executeWithPriority(
        priority: DataSourcePriority,
        localOperation: suspend () -> T,
        remoteOperation: suspend () -> T
    ): T {
        return when (priority) {
            DataSourcePriority.LOCAL_FIRST -> {
                try {
                    localOperation()
                } catch (e: Exception) {
                    remoteOperation()
                }
            }
            DataSourcePriority.REMOTE_FIRST -> {
                try {
                    remoteOperation()
                } catch (e: Exception) {
                    localOperation()
                }
            }
            DataSourcePriority.BALANCED -> {
                // Execute both concurrently and return the first successful result
                coroutineScope {
                    val localDeferred = async { localOperation() }
                    val remoteDeferred = async { remoteOperation() }
                    
                    // Return the first successful result
                    listOf(localDeferred, remoteDeferred)
                        .firstOrNull { it.isCompleted && !it.getCompletionExceptionOrNull() != null }
                        ?.await()
                        ?: localDeferred.await() // fallback to local
                }
            }
        }
    }
}
```

## Error Handling Across Data Sources

### 1. Unified Error Handling
```kotlin
sealed class DataSourceError {
    object NetworkError : DataSourceError()
    object AuthenticationError : DataSourceError()
    object NotFoundError : DataSourceError()
    object ValidationError : DataSourceError()
    data class UnknownError(val message: String) : DataSourceError()
}

suspend fun <T> handleDataSourceErrors(block: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(block())
    } catch (e: IOException) {
        Resource.Error(DataSourceError.NetworkError, e)
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> Resource.Error(DataSourceError.AuthenticationError, e)
            404 -> Resource.Error(DataSourceError.NotFoundError, e)
            400 -> Resource.Error(DataSourceError.ValidationError, e)
            else -> Resource.Error(DataSourceError.UnknownError(e.message()), e)
        }
    } catch (e: Exception) {
        Resource.Error(DataSourceError.UnknownError(e.message ?: "Unknown error"), e)
    }
}
```

### 2. Fallback Strategies
```kotlin
class FallbackDataSourceHandler @Inject constructor(
    private val primaryDataSource: DataSource,
    private val secondaryDataSource: DataSource,
    private val cacheDataSource: DataSource
) {
    
    suspend fun <T> executeWithFallback(
        primaryOp: suspend () -> T,
        secondaryOp: suspend () -> T,
        cacheOp: suspend () -> T
    ): Resource<T> {
        // Try primary source
        runCatching { return Resource.Success(primaryOp()) }
        
        // Try secondary source
        runCatching { return Resource.Success(secondaryOp()) }
        
        // Try cache
        runCatching { return Resource.Success(cacheOp()) }
        
        // All failed
        return Resource.Error("All data sources failed")
    }
}
```

## Performance Optimization

### 1. Connection Pooling
```kotlin
// HttpModule.kt
@Module
@InstallIn(SingletonComponent::class)
object HttpModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()
    }
}
```

### 2. Request Batching
```kotlin
class BatchRequestProcessor @Inject constructor(
    private val httpClient: OkHttpClient,
    private val gson: Gson
) {
    
    suspend fun <T> executeBatch(requests: List<ApiRequest>): List<Resource<T>> {
        return requests.chunked(BATCH_SIZE).flatMap { chunk ->
            executeBatchChunk(chunk)
        }
    }
    
    private suspend fun <T> executeBatchChunk(chunk: List<ApiRequest>): List<Resource<T>> {
        return withContext(Dispatchers.IO) {
            chunk.map { request ->
                async {
                    safeApiCall { executeRequest(request) }
                }
            }.awaitAll()
        }
    }
    
    companion object {
        private const val BATCH_SIZE = 10
    }
}
```

## Security Considerations

### 1. API Key Management
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    
    @Provides
    @Singleton
    fun provideApiKey(@ApplicationContext context: Context): String {
        return try {
            // Retrieve from secure storage or build config
            BuildConfig.API_KEY
        } catch (e: Exception) {
            // Fallback to encrypted storage
            val encryptedPrefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
            val encryptedKey = encryptedPrefs.getString("api_key", "") ?: ""
            decrypt(encryptedKey)
        }
    }
}
```

### 2. Certificate Pinning
```kotlin
@Provides
@Singleton
fun provideSecureHttpClient(): OkHttpClient {
    val certificatePinner = CertificatePinner.Builder()
        .add("api.rostry.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        .add("api.rostry.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=")
        .build()
    
    return OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .build()
}
```

## Testing Data Source Integration

### 1. Mock Data Sources
```kotlin
// Test modules for data source integration
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseModule::class]
)
object TestFirebaseModule {
    
    @Provides
    @Singleton
    fun provideMockFirebaseAuth(): FirebaseAuth {
        return mockk<FirebaseAuth>()
    }
    
    @Provides
    @Singleton
    fun provideMockFirestore(): FirebaseFirestore {
        return mockk<FirebaseFirestore>()
    }
}
```

### 2. Integration Testing
```kotlin
@HiltAndroidTest
class DataSourceIntegrationTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var userRepository: UserRepository
    
    @Test
    fun `user data is synced between local and remote sources`() = runTest {
        // Given
        val testUser = User("test-id", "Test User", "test@example.com")
        
        // When
        userRepository.createUser(testUser)
        
        // Then
        // Verify user exists in both local and remote sources
        val localUser = userRepository.getLocalUser("test-id")
        val remoteUser = userRepository.getRemoteUser("test-id")
        
        assertEquals(testUser, localUser)
        assertEquals(testUser, remoteUser)
    }
}
```

## Monitoring and Observability

### 1. Data Source Metrics
```kotlin
data class DataSourceMetrics(
    val successRate: Double,
    val avgResponseTime: Duration,
    val errorRate: Double,
    val throughput: Int
)

class DataSourceMonitor @Inject constructor(
    private val metricsCollector: MetricsCollector
) {
    
    suspend fun collectMetrics(dataSource: String): DataSourceMetrics {
        val metrics = metricsCollector.getMetricsForDataSource(dataSource)
        
        return DataSourceMetrics(
            successRate = metrics.successfulRequests.toDouble() / metrics.totalRequests.toDouble(),
            avgResponseTime = metrics.totalResponseTime / metrics.successfulRequests,
            errorRate = metrics.failedRequests.toDouble() / metrics.totalRequests.toDouble(),
            throughput = metrics.totalRequests / metrics.timeWindow.inWholeSeconds.toInt()
        )
    }
}
```

### 2. Health Checks
```kotlin
interface DataSourceHealthCheck {
    suspend fun checkHealth(): HealthStatus
}

class FirebaseHealthCheck @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : DataSourceHealthCheck {
    
    override suspend fun checkHealth(): HealthStatus {
        return try {
            // Test a simple operation
            val testDoc = firestore.collection("health_check").document("test")
            testDoc.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()
            
            HealthStatus.Healthy
        } catch (e: Exception) {
            HealthStatus.Unhealthy(e.message ?: "Unknown error")
        }
    }
}
```

## Troubleshooting

### Common Issues

#### 1. Network Connectivity Issues
- **Symptoms**: Requests timing out or failing with network errors
- **Solutions**: 
  - Verify network permissions in manifest
  - Check connectivity before making requests
  - Implement proper retry mechanisms

#### 2. Authentication Problems
- **Symptoms**: 401 errors, unauthorized access
- **Solutions**:
  - Verify Firebase Auth configuration
  - Check token refresh mechanisms
  - Ensure proper session management

#### 3. Rate Limiting
- **Symptoms**: 429 errors from external APIs
- **Solutions**:
  - Implement exponential backoff
  - Add rate limiting to your requests
  - Use caching to reduce API calls

#### 4. Data Consistency
- **Symptoms**: Inconsistent data between local and remote sources
- **Solutions**:
  - Implement proper conflict resolution
  - Use timestamp-based synchronization
  - Add data validation checks

### Debugging Tips
- Enable detailed logging for data source operations
- Monitor API quotas and usage
- Use Firebase console to monitor database operations
- Implement circuit breakers for external API calls
- Add health check endpoints for each data source