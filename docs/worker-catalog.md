Title: Worker Catalog
Version: 1.1
Last Updated: 2026-02-05
Audience: Developers, DevOps

## Table of Contents
- [Core Sync Workers](#core-sync-workers)
- [Farm Monitoring Workers](#farm-monitoring-workers)
- [Personalization & Analytics Workers](#personalization--analytics-workers)
- [Notification & Reminder Workers](#notification--reminder-workers)
- [Media & Upload Workers](#media--upload-workers)
- [Moderation & Maintenance Workers](#moderation--maintenance-workers)
- [Transfer & Order Workers](#transfer--order-workers)
- [Evidence-Based Order Workers](#evidence-based-order-workers)
- [Data & Migration Workers](#data--migration-workers)
- [Worker Configuration](#worker-configuration)
- [Monitoring & Debugging](#monitoring--debugging)
- [Implementation Guide](#implementation-guide)

```kotlin
// Example usage
val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
    .setConstraints(Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build())
    .build()
WorkManager.getInstance(context).enqueue(workRequest)
```

## Core Sync Workers

### SyncWorker
**Purpose**: Primary data synchronization worker for Room/Firebase harmonization
- Synchronizes local Room database with Firebase Firestore
- Handles bidirectional sync of user data, products, transfers, and social content
- Implements conflict resolution with server timestamp preference
- Processes pending changes from outbox queue
- Reduced from 6h to 8h intervals for quota optimization

**Scheduling**: Periodic every 8 hours
**Constraints**: Network connected, device idle preferred
**Integration**: Works with SyncManager for incremental sync
**Retry Logic**: Exponential backoff with max 3 attempts

### OutboxSyncWorker
**Purpose**: Processes outbox pattern for offline changes
- Handles pending operations when device comes online
- Processes ORDER and POST entities from outbox
- Implements retry logic with exponential backoff
- Cleans up completed entries after 7 days

**Scheduling**: Periodic every 15 minutes
**Constraints**: Network connected, battery not low
**Retry Logic**: Exponential backoff, max 3 attempts

```kotlin
// Scheduling implementation
fun schedule(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<OutboxSyncWorker>(
        15, TimeUnit.MINUTES
    )
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            WorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(WORK_NAME, EXISTING_KEEP, workRequest)
}
```

### PullSyncWorker
**Purpose**: Dedicated worker for pulling remote updates
- Focuses on download-only operations to reduce conflicts
- Handles large data sets with pagination
- Updates local cache without pushing changes
- Used for read-heavy synchronization scenarios

**Scheduling**: Periodic hourly or on-demand
**Constraints**: Network connected (unmetered preferred)
**Integration**: Works with SyncManager for incremental pulls

## Farm Monitoring Workers

Farm monitoring workers automate poultry health tracking, vaccination schedules, and performance reporting to help farmers maintain optimal flock conditions.

### FarmMonitoringWorker
**Purpose**: Daily farm health checks and operational summaries
- Aggregates vaccination due dates
- Monitors growth metrics and alerts
- Generates daily health summaries
- Updates dashboard snapshots

**Scheduling**: Daily at 6 AM
**Constraints**: Network connected, device idle
**Integration**: Updates FarmerDashboardSnapshotEntity

### VaccinationReminderWorker
**Purpose**: Vaccination schedule alerts and reminders
- Scans vaccination records for upcoming due dates
- Sends notifications 7 days and 1 day before due
- Tracks vaccination compliance rates
- Integrates with farm alert system

**Scheduling**: Daily at 8 AM
**Constraints**: Network connected
**Notification**: FCM push notifications with deep links

### FarmPerformanceWorker
**Purpose**: Weekly performance reports and analytics
- Calculates growth rates and mortality statistics
- Generates breeding performance metrics
- Creates weekly summary reports
- Updates performance dashboards

**Scheduling**: Weekly on Sunday at 9 AM
**Constraints**: Network connected, storage not low
**Output**: FarmerDashboardSnapshotEntity updates

## Personalization & Analytics Workers

These workers handle AI-driven personalization and data aggregation for insights and recommendations.

### PersonalizationWorker
**Purpose**: 6-hour recommendation updates for personalized content
- Processes user interaction data
- Updates CommunityRecommendationEntity
- Refreshes UserInterestEntity weights
- Generates personalized content suggestions

**Scheduling**: Every 6 hours (as implemented in PersonalizationWorker.kt)
**Note**: The actual implementation uses 6-hour intervals. The CHANGELOG reference to 12 hours may reflect an earlier design decision that was changed during implementation.
**Constraints**: Network connected
**Integration**: Uses PersonalizationService for scoring

```kotlin
// Scheduling as implemented (6 hours)
val request = PeriodicWorkRequestBuilder<PersonalizationWorker>(6, TimeUnit.HOURS)
    .setConstraints(Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build())
    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
    .build()
```

### AnalyticsAggregationWorker
**Purpose**: Data aggregation for dashboard metrics
- Processes raw farm data into aggregated metrics
- Calculates performance KPIs and trends
- Updates analytics cache for fast dashboard loading
- Handles data retention and cleanup

**Scheduling**: Daily at 2 AM
**Constraints**: Device idle, storage not low
**Output**: Pre-computed analytics data

### ReportingWorker
**Purpose**: Scheduled report generation and export
- Generates PDF/CSV reports for farm performance
- Handles automated report delivery
- Processes large datasets for compliance reports
- Integrates with export utilities

**Scheduling**: Weekly on Monday at 6 AM
**Constraints**: Network connected, sufficient storage
**Integration**: Uses PdfExporter and CsvExporter

## Notification & Reminder Workers

Notification workers handle user-facing alerts and reminders with preference-based delivery.

### TransferReminderWorker
**Purpose**: Transfer deadline reminders and status updates
- Monitors pending transfer expiration
- Sends reminder notifications
- Tracks transfer completion rates
- Handles dispute deadline alerts

**Scheduling**: Daily at 10 AM
**Constraints**: Network connected
**Deep Links**: Direct navigation to transfer details

### CommunityNotificationWorker
**Purpose**: Community engagement notifications
- New message alerts in threads
- Group activity notifications
- Event reminders and updates
- Expert consultation notifications

**Scheduling**: Real-time triggered (not periodic)
**Constraints**: Network connected
**Preferences**: Respects user notification settings

### GeneralReminderWorker
**Purpose**: General app reminders and engagement
- Marketplace listing expiration alerts
- Profile completion reminders
- Feature onboarding notifications
- Seasonal farming reminders

**Scheduling**: Weekly patterns
**Constraints**: Network connected
**Personalization**: Based on user role and activity

## Media & Upload Workers

Media workers handle background upload and processing of images, videos, and documents.

### MediaUploadWorker
**Purpose**: Background media uploads with compression
- Compresses images/videos before upload
- Handles Firebase Storage uploads
- Tracks upload progress and retries
- Updates media URLs in database entities

**Scheduling**: On-demand (triggered by user actions)
**Constraints**: Network connected, battery not low
**Retry Logic**: Exponential backoff, max 5 attempts

### MediaOptimizationWorker
**Purpose**: Media optimization and cleanup
- Resizes images for different screen densities
- Generates thumbnails for videos
- Cleans up temporary media files
- Optimizes storage usage

**Scheduling**: Daily at 3 AM
**Constraints**: Device idle, storage not low
**Integration**: Works with media upload pipeline

### MediaSyncWorker
**Purpose**: Synchronizes media metadata
- Updates media URLs after successful uploads
- Syncs media status across devices
- Handles failed upload recovery
- Maintains media reference integrity

**Scheduling**: Periodic hourly
**Constraints**: Network connected
**Dependencies**: Runs after MediaUploadWorker

## Moderation & Maintenance Workers

These workers handle content moderation, system cleanup, and performance optimization.

### ModerationWorker
**Purpose**: Content moderation and community safety
- Scans posts and messages for inappropriate content
- Flags suspicious transfer requests
- Monitors community engagement metrics
- Implements automated moderation rules

**Scheduling**: Every 4 hours
**Constraints**: Network connected
**Integration**: Updates moderation flags in entities

## Transfer & Order Workers

### TransferTimeoutWorker
**Purpose**: Enforces SLA deadlines for transfer completion
- Monitors pending transfers for timeout conditions
- Automatically cancels transfers that exceed time limits
- Sends notifications to involved parties about timeout status
- Updates transfer status to TIMEOUT_EXPIRED

**Scheduling**: Periodic every 2 hours
**Constraints**: Network connected
**Integration**: Works with TransferWorkflowRepository for status updates

### OutgoingMessageWorker
**Purpose**: Flushes queued outgoing messages to delivery system
- Processes messages stored in OutgoingMessageEntity queue
- Delivers messages to recipients via Firebase or other channels
- Updates delivery status and timestamps
- Replaced by OutboxSyncWorker for broader sync operations

**Scheduling**: On-demand when messages are queued
**Constraints**: Network connected
**Integration**: Works with MessagingRepository for delivery

## Evidence-Based Order Workers

### EvidenceOrderWorker
**Purpose**: Manages evidence-based order lifecycle automation
- Handles quote expiry for unaccepted quotes
- Sends payment reminder notifications
- Processes delivery confirmation workflows
- Manages dispute resolution timelines
- Updates order status through 10-state workflow

**Scheduling**: Periodic every 1 hour
**Constraints**: Network connected
**Integration**: Works with EvidenceOrderRepository for state management

## Data & Migration Workers

### LegacyProductMigrationWorker
**Purpose**: One-time migration of legacy product data
- Converts old product format to new schema
- Preserves historical data during architecture updates
- Runs only once during app upgrade
- Validates migrated data integrity

**Scheduling**: One-time execution on app upgrade
**Constraints**: None (runs during app startup)
**Integration**: Works with ProductRepository for data conversion

### DataCleanupWorker
**Purpose**: Regular cleanup of obsolete data
- Removes expired temporary data
- Cleans up orphaned records
- Archives old logs and temporary files
- Maintains database performance

**Scheduling**: Daily at 1 AM
**Constraints**: Device idle, storage not low
**Integration**: Works with various repositories for cleanup

### DataExportWorker
**Purpose**: Handles bulk data export operations
- Processes large-scale data export requests
- Generates export files in background
- Manages export file storage and cleanup
- Notifies users when exports are ready

**Scheduling**: On-demand for export requests
**Constraints**: Sufficient storage, network connected
**Integration**: Works with ExportManager for file generation

### BatchGraduationWorker
**Purpose**: Manages batch lifecycle transitions
- Automatically advances batches through lifecycle stages
- Updates batch statuses based on time thresholds
- Creates follow-up tasks for graduated batches
- Integrates with farm monitoring systems

**Scheduling**: Daily at 3 AM
**Constraints**: Device idle
**Integration**: Works with FarmMonitoringRepository

### DailyAnalyticsWorker
**Purpose**: Processes daily analytics aggregation
- Aggregates daily usage metrics
- Calculates performance KPIs
- Updates analytics dashboards
- Generates daily reports

**Scheduling**: Daily at 2 AM
**Constraints**: Device idle
**Integration**: Works with AnalyticsRepository

### EventReminderWorker
**Purpose**: Sends event reminder notifications
- Monitors upcoming events
- Sends timely reminders to attendees
- Handles RSVP status updates
- Integrates with calendar systems

**Scheduling**: Daily at 9 AM
**Constraints**: Network connected
**Integration**: Works with EventRepository

### FarmAlertWorker
**Purpose**: Manages farm-specific alert notifications
- Monitors farm health metrics
- Sends critical alerts to farmers
- Tracks alert acknowledgment
- Integrates with farm monitoring systems

**Scheduling**: Periodic every 30 minutes
**Constraints**: Network connected
**Integration**: Works with FarmMonitoringRepository

### NotificationFlushWorker
**Purpose**: Batch processes notification delivery
- Groups multiple notifications for efficient delivery
- Reduces battery drain from frequent notifications
- Handles different notification priorities
- Manages notification queuing and delivery

**Scheduling**: Periodic every 15 minutes
**Constraints**: Network connected
**Integration**: Works with NotificationRepository

### OrderStatusWorker
**Purpose**: Monitors and updates order status changes
- Tracks order progression through states
- Sends status update notifications
- Handles order timeout conditions
- Integrates with EvidenceOrderRepository

**Scheduling**: Periodic every 30 minutes
**Constraints**: Network connected
**Integration**: Works with OrderRepository

### VerificationUploadWorker
**Purpose**: Processes verification document uploads
- Handles transfer verification document processing
- Manages document validation and storage
- Updates verification status
- Integrates with TransferWorkflowRepository

**Scheduling**: On-demand for verification uploads
**Constraints**: Network connected, sufficient storage
**Integration**: Works with VerificationRepository

### CacheCleanupWorker
**Purpose**: Cache management and cleanup
- Removes expired marketplace listings
- Cleans up old notification data
- Manages image cache size
- Optimizes database indices

**Scheduling**: Daily at 4 AM
**Constraints**: Device idle
**Performance**: Low-priority background task

### DatabaseOptimizationWorker
**Purpose**: Database maintenance and optimization
- Runs VACUUM on Room database
- Rebuilds indices for better query performance
- Analyzes query patterns for optimization
- Handles database migration cleanup

**Scheduling**: Weekly on Saturday at 2 AM
**Constraints**: Device idle, sufficient storage
**Integration**: Works with SQLCipher encrypted database

### LogCleanupWorker
**Purpose**: Log file management
- Rotates and compresses Timber logs
- Removes old debug logs
- Manages log file size limits
- Prepares logs for upload on crashes

**Scheduling**: Daily at 5 AM
**Constraints**: Storage not low
**Privacy**: Ensures no PII in retained logs

## Worker Configuration

### Registration and Initialization
Workers are registered in the AppModule using Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    // Worker factories for assisted injection
    @Provides
    fun provideSyncWorkerFactory(
        syncManager: SyncManager,
        connectivityManager: ConnectivityManager
    ): WorkerFactory {
        return SyncWorker.Factory(syncManager, connectivityManager)
    }
}
```

### Constraint Configuration
Common constraint patterns:

```kotlin
val networkConstraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .build()

val idleConstraints = Constraints.Builder()
    .setRequiresDeviceIdle(true)
    .setRequiredNetworkType(NetworkType.UNMETERED)
    .build()

val storageConstraints = Constraints.Builder()
    .setRequiresStorageNotLow(true)
    .build()
```

### Scheduling Patterns

#### Periodic Workers
```kotlin
val dailyWork = PeriodicWorkRequestBuilder<DailyWorker>(
    1, TimeUnit.DAYS
)
    .setInitialDelay(6, TimeUnit.HOURS) // Start at 6 AM
    .setConstraints(networkConstraints)
    .build()
```

#### One-Time Workers
```kotlin
val immediateWork = OneTimeWorkRequestBuilder<ImmediateWorker>()
    .setConstraints(networkConstraints)
    .setBackoffCriteria(
        BackoffPolicy.EXPONENTIAL,
        30, TimeUnit.SECONDS
    )
    .build()
```

#### Expedited Workers
```kotlin
val urgentWork = OneTimeWorkRequestBuilder<UrgentWorker>()
    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
    .build()
```

### Backoff Policies and Retry Strategies
```kotlin
.setBackoffCriteria(
    BackoffPolicy.EXPONENTIAL,
    WorkRequest.MIN_BACKOFF_MILLIS, // 30 seconds
    TimeUnit.MILLISECONDS
)
// Max attempts: 3 (configurable in worker)
```

### Worker Chaining and Dependencies
```kotlin
val syncWork = OneTimeWorkRequestBuilder<SyncWorker>().build()
val cleanupWork = OneTimeWorkRequestBuilder<CleanupWorker>().build()

WorkManager.getInstance(context)
    .beginWith(syncWork)
    .then(cleanupWork)
    .enqueue()
```

## Monitoring & Debugging

### Viewing Worker Status
```kotlin
// In ViewModel or Activity
val workInfo = WorkManager.getInstance(context)
    .getWorkInfoByIdLiveData(workRequest.id)
    .observe(viewLifecycleOwner) { info ->
        when (info?.state) {
            WorkInfo.State.ENQUEUED -> showQueued()
            WorkInfo.State.RUNNING -> showRunning()
            WorkInfo.State.SUCCEEDED -> showSuccess()
            WorkInfo.State.FAILED -> showFailure()
            WorkInfo.State.CANCELLED -> showCancelled()
        }
    }
```

### Logging and Diagnostics
All workers use Timber for logging:
```kotlin
Timber.d("Worker started")
Timber.i("Processing ${items.size} items")
Timber.e(exception, "Worker failed")
```

### Testing Workers Locally
```kotlin
@Test
fun testSyncWorker() = runBlocking {
    val worker = TestListenableWorkerBuilder<SyncWorker>(context)
        .setWorkerFactory(factory)
        .build()
    
    val result = worker.doWork()
    assertThat(result, `is`(ListenableWorker.Result.success()))
}
```

### Common Issues and Troubleshooting

#### Worker Not Running
- Check constraints (network, battery, storage)
- Verify WorkManager initialization
- Check for unique work name conflicts
- Ensure device is not in Doze mode

#### High Battery Usage
- Review scheduling frequency
- Add battery constraints
- Use device idle constraints for non-urgent work
- Implement exponential backoff properly

#### Network Timeouts
- Increase retry attempts
- Add network type constraints
- Implement proper backoff policies
- Handle offline scenarios gracefully

#### Storage Issues
- Add storage constraints
- Implement cleanup in workers
- Monitor database size
- Handle large data sets with pagination

### Performance Monitoring
```kotlin
// Track worker performance
val startTime = System.currentTimeMillis()
// ... worker logic ...
val duration = System.currentTimeMillis() - startTime
Timber.d("Worker completed in ${duration}ms")
```

## Implementation Guide

### Creating a New Worker

1. **Extend CoroutineWorker**
```kotlin
@HiltWorker
class MyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MyRepository
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Worker logic here
            repository.performTask()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "MyWorker failed")
            Result.retry()
        }
    }
    
    companion object {
        private const val WORK_NAME = "my_worker"
        
        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<MyWorker>(
                1, TimeUnit.HOURS
            ).build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
        }
    }
}
```

2. **Add to WorkerModule**
```kotlin
@Provides
fun provideMyWorkerFactory(
    repository: MyRepository
): WorkerFactory {
    return MyWorker.Factory(repository)
}
```

3. **Schedule in Application.onCreate()**
```kotlin
MyWorker.schedule(applicationContext)
```

### Worker Best Practices

#### Error Handling
```kotlin
override suspend fun doWork(): Result {
    return try {
        validateInputs()
        performWork()
        Result.success()
    } catch (e: NetworkException) {
        Timber.w(e, "Network error, will retry")
        Result.retry()
    } catch (e: ValidationException) {
        Timber.e(e, "Validation failed, not retryable")
        Result.failure()
    } catch (e: Exception) {
        Timber.e(e, "Unexpected error")
        Result.failure()
    }
}
```

#### Resource Management
```kotlin
override suspend fun doWork(): Result {
    return withContext(Dispatchers.IO) {
        val connection = database.openHelper.writableDatabase
        try {
            // Use connection
            performDatabaseWork(connection)
            Result.success()
        } finally {
            connection.close()
        }
    }
}
```

#### Progress Reporting
```kotlin
override suspend fun doWork(): Result {
    setProgress(workDataOf("progress" to 0))
    
    for (i in 0..100 step 10) {
        performStep(i)
        setProgress(workDataOf("progress" to i))
        delay(100) // Allow cancellation
    }
    
    return Result.success()
}
```

### Testing Strategies

#### Unit Testing
```kotlin
@Test
fun `worker succeeds with valid data`() = runTest {
    val repository = mockk<MyRepository>()
    coEvery { repository.performTask() } returns Unit
    
    val worker = TestListenableWorkerBuilder<MyWorker>(context)
        .setWorkerFactory(MyWorkerFactory(repository))
        .build()
    
    val result = worker.doWork()
    assertEquals(ListenableWorker.Result.success(), result)
}
```

#### Integration Testing
```kotlin
@Test
fun `worker integrates with real dependencies`() {
    val config = Configuration.Builder()
        .setWorkerFactory(HiltWorkerFactory)
        .build()
    
    WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    
    // Schedule and verify
}
```

### Code Examples from Actual Workers

#### SyncWorker Pattern
```kotlin
override suspend fun doWork(): Result {
    Timber.d("SyncWorker started")
    return try {
        when (val res = syncManager.syncAll()) {
            is Resource.Success -> {
                val stats = res.data
                Timber.d("Sync completed: pushed=${stats?.pushed}, pulled=${stats?.pulled}")
                Result.success()
            }
            is Resource.Error -> {
                Timber.e("SyncManager error: ${res.message}")
                Result.retry()
            }
            is Resource.Loading -> Result.success()
        }
    } catch (e: Exception) {
        Timber.e(e, "SyncWorker failed")
        Result.failure()
    }
}
```

#### OutboxSyncWorker with Constraints
```kotlin
override suspend fun doWork(): Result {
    if (!connectivityManager.isOnline()) {
        return Result.retry()
    }
    
    return try {
        syncManager.syncAll()
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}
```

#### PersonalizationWorker Scheduling
```kotlin
companion object {
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<PersonalizationWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL, 
                10, TimeUnit.MINUTES
            )
            .build()
        
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}
```

### CHANGELOG
* [See CHANGELOG for changes](#changelog)
