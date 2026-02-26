---
Version: 1.0
Last Updated: 2026-02-26
Audience: Developers
Status: Active
Related_Docs: [firebase-setup.md, data-sources-integration.md, api-documentation.md]
Tags: [api, integration, retrofit, external-services]
---

# API Integration Guide

**Document Type**: Integration Guide  
**Version**: 1.0  
**Last Updated**: 2026-02-26  
**Feature Owner**: Data Layer Infrastructure  
**Status**: ✅ Fully Implemented

## Overview

This guide covers API integration patterns, Retrofit setup, interceptors, error handling, and best practices for integrating external APIs in ROSTRY.

## Retrofit Configuration

### HTTP Module Setup

**File**: `di/HttpModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: LoggingInterceptor,
        networkInterceptor: NetworkInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(networkInterceptor)
            .addInterceptor(OfflineInterceptor(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }
}
```

### Interceptors

#### Authentication Interceptor

```kotlin
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for public endpoints
        if (originalRequest.url.encodedPath in PUBLIC_ENDPOINTS) {
            return chain.proceed(originalRequest)
        }

        // Get auth token
        val token = sessionManager.getAuthToken()
            ?: throw UnauthenticatedException("No auth token")

        // Add auth header
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }

    companion object {
        private val PUBLIC_ENDPOINTS = setOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/products/public",
            "/api/health"
        )
    }
}
```

#### Logging Interceptor

```kotlin
class LoggingInterceptor @Inject constructor() : Interceptor {

    private val logger = Timber.tag("HTTP")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Log request
        logger.d("${request.method} ${request.url}")
        request.headers.forEach { name, value ->
            logger.d("$name: $value")
        }

        val startTime = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime

        // Log response
        logger.d("${response.code} ${response.message} (${duration}ms)")
        response.headers.forEach { name, value ->
            logger.d("$name: $value")
        }

        return response
    }
}
```

#### Network Interceptor

```kotlin
class NetworkInterceptor @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivityManager.isOnline()) {
            throw IOException("No network connection")
        }

        val response = chain.proceed(chain.request())

        // Cache response
        val cacheControl = response.header("Cache-Control")
        if (cacheControl == null || cacheControl.contains("no-store")) {
            return response.newBuilder()
                .header("Cache-Control", "max-age=300") // 5 minutes
                .build()
        }

        return response
    }
}
```

#### Offline Interceptor

```kotlin
class OfflineInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cacheManager: CacheManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // If offline, try to serve from cache
        if (!context.isOnline()) {
            val cacheKey = request.url.encodedPath
            val cachedResponse = cacheManager.get<String>(cacheKey)

            if (cachedResponse != null) {
                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK (cached)")
                    .body(cachedResponse.toResponseBody())
                    .build()
            }
        }

        val response = chain.proceed(request)

        // Cache successful GET requests
        if (request.method == "GET" && response.isSuccessful) {
            val cacheKey = request.url.encodedPath
            val bodyString = response.body?.string()
            bodyString?.let {
                cacheManager.put(cacheKey, it)
            }
            // Re-create response body since we consumed it
            return response.newBuilder()
                .body(bodyString?.toResponseBody())
                .build()
        }

        return response
    }

    private fun Context.isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }
}
```

## API Service Definitions

### Weather API Service

```kotlin
interface WeatherApi {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") count: Int,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherForecastResponse

    @GET("air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirPollutionResponse
}

data class WeatherResponse(
    @SerializedName("main") val main: MainWeather,
    @SerializedName("weather") val weather: List<WeatherCondition>,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("name") val cityName: String
)

data class MainWeather(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int
)

data class WeatherCondition(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val direction: Int
)
```

### Product API Service

```kotlin
interface ProductApi {

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("category") category: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("sortDir") sortDir: String = "desc"
    ): PagedResponse<ProductResponse>

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") productId: String
    ): ProductResponse

    @POST("products")
    suspend fun createProduct(
        @Body request: CreateProductRequest
    ): ProductResponse

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: String,
        @Body request: UpdateProductRequest
    ): ProductResponse

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") productId: String
    ): Unit

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PagedResponse<ProductResponse>
}

data class ProductResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("category") val category: String,
    @SerializedName("breed") val breed: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("weight") val weight: Double?,
    @SerializedName("images") val images: List<String>,
    @SerializedName("seller") val seller: SellerResponse,
    @SerializedName("location") val location: LocationResponse,
    @SerializedName("createdAt") val createdAt: Instant,
    @SerializedName("updatedAt") val updatedAt: Instant
)

data class PagedResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("size") val size: Int
)
```

## Rate Limiting

### Rate Limiter Implementation

```kotlin
class RateLimiter @Inject constructor() {
    private val tokenBucket = ConcurrentHashMap<String, TokenBucket>()

    data class TokenBucket(
        var tokens: Double,
        val maxTokens: Double,
        val refillRate: Double, // tokens per second
        var lastRefill: Long = System.currentTimeMillis()
    )

    /**
     * Check if request is allowed
     */
    suspend fun isAllowed(clientId: String, tokensRequired: Int = 1): Boolean {
        return synchronized(this) {
            val bucket = tokenBucket.getOrPut(clientId) {
                TokenBucket(
                    tokens = 100.0,
                    maxTokens = 100.0,
                    refillRate = 10.0
                )
            }

            // Refill tokens based on time elapsed
            val now = System.currentTimeMillis()
            val elapsed = (now - bucket.lastRefill) / 1000.0
            bucket.tokens = minOf(
                bucket.maxTokens,
                bucket.tokens + elapsed * bucket.refillRate
            )
            bucket.lastRefill = now

            // Check if enough tokens
            if (bucket.tokens >= tokensRequired) {
                bucket.tokens -= tokensRequired
                true
            } else {
                false
            }
        }
    }

    /**
     * Get remaining tokens
     */
    fun getRemainingTokens(clientId: String): Double {
        return tokenBucket[clientId]?.tokens ?: 0.0
    }
}
```

### Rate Limit Interceptor

```kotlin
class RateLimitInterceptor @Inject constructor(
    private val rateLimiter: RateLimiter,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val clientId = sessionManager.getUserId() ?: "anonymous"

        // Check rate limit
        if (!rateLimiter.isAllowed(clientId)) {
            throw ApiError.RateLimitExceeded(
                "Rate limit exceeded. Try again in ${calculateRetryAfter(clientId)} seconds"
            )
        }

        val response = chain.proceed(chain.request())

        // Update rate limit from headers
        response.headers.get("X-RateLimit-Remaining")?.toIntOrNull()?.let {
            // Update local rate limiter
        }

        return response
    }

    private fun calculateRetryAfter(clientId: String): Long {
        val remaining = rateLimiter.getRemainingTokens(clientId)
        return ((1 - remaining) / 10.0).toLong() + 1
    }
}
```

## Error Handling

### API Error Response

```kotlin
data class ErrorResponse(
    @SerializedName("error") val error: ErrorDetail,
    @SerializedName("timestamp") val timestamp: Instant,
    @SerializedName("path") val path: String
)

data class ErrorDetail(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: List<ErrorDetail>? = null
)
```

### Error Handler

```kotlin
class ApiErrorHandler @Inject constructor() {

    fun handleErrorResponse(response: Response<*>): ApiError {
        val errorBody = response.errorBody()?.string()
        val errorResponse = try {
            Gson().fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

        return when (response.code()) {
            400 -> ApiError.BadRequest(
                errorResponse?.error?.message ?: "Bad request"
            )
            401 -> ApiError.Unauthorized(
                errorResponse?.error?.message ?: "Unauthorized"
            )
            403 -> ApiError.Forbidden(
                errorResponse?.error?.message ?: "Forbidden"
            )
            404 -> ApiError.NotFound(
                errorResponse?.error?.message ?: "Not found"
            )
            408 -> ApiError.TimeoutError(
                errorResponse?.error?.message ?: "Request timeout"
            )
            429 -> ApiError.RateLimitExceeded(
                errorResponse?.error?.message ?: "Rate limit exceeded"
            )
            in 500..599 -> ApiError.ServerError(
                errorResponse?.error?.message ?: "Server error",
                response.code()
            )
            else -> ApiError.UnknownError(
                errorResponse?.error?.message ?: "Unknown error",
                null
            )
        }
    }
}
```

## Testing API Integration

### Mock API Service

```kotlin
class MockProductApi : ProductApi {
    private val products = mutableListOf<ProductResponse>()

    override suspend fun getProducts(
        page: Int,
        size: Int,
        category: String?,
        minPrice: Double?,
        maxPrice: Double?,
        sortBy: String,
        sortDir: String
    ): PagedResponse<ProductResponse> {
        // Simulate network delay
        delay(100)

        val filtered = products.filter { product ->
            category == null || product.category == category
        }.let {
            if (minPrice != null) it.filter { it.price >= minPrice } else it
        }.let {
            if (maxPrice != null) it.filter { it.price <= maxPrice } else it
        }

        return PagedResponse(
            content = filtered,
            totalElements = filtered.size.toLong(),
            totalPages = 1,
            number = page,
            size = size
        )
    }

    override suspend fun getProduct(productId: String): ProductResponse {
        delay(100)
        return products.find { it.id == productId }
            ?: throw ApiError.NotFound("Product not found")
    }

    override suspend fun createProduct(request: CreateProductRequest): ProductResponse {
        delay(100)
        val product = ProductResponse(
            id = UUID.randomUUID().toString(),
            name = request.name,
            description = request.description,
            price = request.price,
            currency = "INR",
            category = request.category,
            breed = request.breed,
            age = request.age,
            weight = request.weight,
            images = request.images,
            seller = SellerResponse(id = "seller1", name = "Test Seller"),
            location = LocationResponse(lat = 0.0, lon = 0.0),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        products.add(product)
        return product
    }

    // ... other implementations
}
```

### API Integration Test

```kotlin
@HiltAndroidTest
class ProductApiIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var productApi: ProductApi

    @Test
    fun `getProducts returns product list`() = runTest {
        // When
        val response = productApi.getProducts(
            page = 0,
            size = 10,
            category = null,
            minPrice = null,
            maxPrice = null,
            sortBy = "createdAt",
            sortDir = "desc"
        )

        // Then
        assertThat(response.content).isNotEmpty()
        assertThat(response.totalPages).isGreaterThan(0)
    }

    @Test
    fun `getProduct returns product when exists`() = runTest {
        // Given
        val productId = "test-product-id"

        // When
        val product = productApi.getProduct(productId)

        // Then
        assertThat(product.id).isEqualTo(productId)
    }

    @Test
    fun `getProduct throws NotFound when product doesn't exist`() = runTest {
        // Given
        val productId = "non-existent-id"

        // When/Then
        assertFailsWith<ApiError.NotFound> {
            productApi.getProduct(productId)
        }
    }
}
```

## Best Practices

### API Design Principles

1. **RESTful endpoints**: Use proper HTTP methods and status codes
2. **Versioning**: Include API version in URL or headers
3. **Pagination**: Always paginate list responses
4. **Filtering**: Support filtering and sorting
5. **Error handling**: Return consistent error formats
6. **Rate limiting**: Protect API from abuse
7. **Caching**: Use appropriate cache headers
8. **Documentation**: Document all endpoints with OpenAPI/Swagger

### Security Best Practices

1. **HTTPS only**: Never use HTTP in production
2. **Token authentication**: Use Bearer tokens
3. **Token refresh**: Implement token refresh mechanism
4. **Certificate pinning**: Pin certificates in production
5. **Input validation**: Validate all input on server
6. **Rate limiting**: Implement rate limiting
7. **CORS**: Configure CORS properly
8. **API keys**: Store securely, rotate regularly

### Performance Best Practices

1. **Connection pooling**: Reuse connections
2. **Compression**: Enable gzip compression
3. **Caching**: Cache responses appropriately
4. **Pagination**: Limit response sizes
5. **Field selection**: Allow clients to request specific fields
6. **Batching**: Support batch operations
7. **Async operations**: Use coroutines for async calls
8. **Timeout configuration**: Set appropriate timeouts

## Troubleshooting

### Common Issues

**Problem**: 401 Unauthorized errors
- **Cause**: Expired or missing auth token
- **Solution**: Implement token refresh, check token storage

**Problem**: 429 Rate Limit Exceeded
- **Cause**: Too many requests
- **Solution**: Implement backoff, reduce request frequency

**Problem**: Timeout errors
- **Cause**: Slow network or server
- **Solution**: Increase timeout, implement retry

**Problem**: SSL handshake failures
- **Cause**: Certificate issues
- **Solution**: Check certificate pinning, update trust store

## Related Documentation

- `firebase-setup.md` - Firebase integration
- `data-sources-integration.md` - Data source patterns
- `api-documentation.md` - API documentation standards
- `security-encryption.md` - Security practices

## Appendix: File Locations

```
data/api/
├── WeatherApi.kt
├── ProductApi.kt
├── OrderApi.kt
└── ...

data/interceptor/
├── AuthInterceptor.kt
├── LoggingInterceptor.kt
├── NetworkInterceptor.kt
├── OfflineInterceptor.kt
└── RateLimitInterceptor.kt

di/
├── HttpModule.kt
├── NetworkModule.kt
└── ...

data/model/api/
├── WeatherResponse.kt
├── ProductResponse.kt
├── ErrorResponse.kt
└── ...
```
