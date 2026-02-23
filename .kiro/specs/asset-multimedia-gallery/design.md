# Asset Multimedia Gallery - Design Document

## Overview

The Asset Multimedia Gallery is a comprehensive multimedia management system for farm assets (birds) that provides two primary user interfaces: an asset-specific media view for browsing all multimedia associated with a particular bird, and a centralized chronological gallery for discovering all farm media sorted by date. The system implements a robust tagging mechanism that categorizes media by asset ID, age group, and source type, enabling powerful filtering and organization capabilities.

The design integrates with existing infrastructure including MediaUploadManager for uploads, Firebase Storage for cloud persistence, and Room Database for local caching and offline access. The architecture prioritizes performance optimization for large collections (thousands of items), offline-first functionality, and seamless synchronization between local and cloud storage.

### Key Features

- Asset-specific media browsing with search functionality
- Chronological gallery with infinite scroll pagination
- Multi-dimensional tagging system (asset ID, age group, source type)
- Advanced filtering by tags with persistent filter state
- Automatic tagging during upload from different contexts
- Offline media access with LRU cache eviction
- Media deletion with cloud and local cleanup
- Performance optimization for large collections
- Rich metadata display with full-screen viewer
- Video playback support with standard controls
- Media export and sharing capabilities

### Design Goals

1. **Performance**: Sub-second load times for initial gallery view, smooth scrolling for thousands of items
2. **Offline-First**: Full functionality for recently viewed media without network connectivity
3. **Scalability**: Efficient handling of large media collections through pagination and lazy loading
4. **Data Integrity**: Consistent state between local cache and cloud storage with automatic synchronization
5. **User Experience**: Intuitive navigation, fast search, and seamless media viewing

## Architecture

### System Components

The system follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ┌──────────────────┐         ┌──────────────────────────┐ │
│  │ GalleryScreen    │         │ AssetMediaScreen         │ │
│  │ (Chronological)  │         │ (Asset-Specific)         │ │
│  └──────────────────┘         └──────────────────────────┘ │
│           │                              │                   │
│           └──────────────┬───────────────┘                   │
│                          │                                   │
│                ┌─────────▼──────────┐                       │
│                │  MediaGalleryVM    │                       │
│                └─────────┬──────────┘                       │
└──────────────────────────┼────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────┐
│                    Domain Layer                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │              MediaGalleryRepository                   │ │
│  │  - Query & Filter Logic                              │ │
│  │  - Pagination Management                             │ │
│  │  - Cache Coordination                                │ │
│  └──────────────────────────────────────────────────────┘ │
└──────────────────────────┬────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────┐
│                     Data Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐ │
│  │ MediaItemDao │  │ StorageRepo  │  │ MediaCacheManager│ │
│  │ (Room DB)    │  │ (Firebase)   │  │ (Local Files)   │ │
│  └──────────────┘  └──────────────┘  └─────────────────┘ │
│         │                 │                    │           │
│         └─────────────────┼────────────────────┘           │
└───────────────────────────┼────────────────────────────────┘
                            │
                   ┌────────▼────────┐
                   │  MediaUpload    │
                   │    Manager      │
                   └─────────────────┘
```

### Component Responsibilities

**GalleryScreen**
- Displays all media in reverse chronological order
- Implements infinite scroll with pagination
- Provides filter UI for age group and source type
- Handles media item selection and full-screen viewing
- Supports batch selection for deletion and export

**AssetMediaScreen**
- Displays media filtered by specific asset ID
- Provides asset search functionality
- Shows empty state when no media exists
- Links to full-screen viewer with asset context

**MediaGalleryViewModel**
- Manages UI state and user interactions
- Coordinates between repository and UI
- Handles filter state persistence
- Manages pagination state
- Processes user actions (delete, share, export)

**MediaGalleryRepository**
- Central data coordination layer
- Implements query logic with filtering
- Manages pagination cursors
- Coordinates cache and cloud storage
- Handles synchronization logic
- Implements search indexing

**MediaItemDao**
- Room database access for media metadata
- Provides indexed queries for performance
- Supports complex filtering with multiple criteria
- Implements pagination queries
- Manages local metadata persistence

**StorageRepository**
- Firebase Storage integration
- Handles file upload/download
- Manages cloud file deletion
- Provides download URLs
- Implements retry logic

**MediaCacheManager**
- Local file system management
- LRU cache implementation (100 items)
- Cache eviction policy
- Offline availability tracking
- Cache synchronization with cloud

**MediaUploadManager** (Existing)
- Handles media upload with retry
- Compression and size optimization
- Progress tracking
- Automatic tagging based on context
- Integration with upload queue

### Data Flow

**Upload Flow**
```
User Action → MediaUploadManager → StorageRepository → Firebase Storage
                    ↓
              MediaItemDao → Room Database
                    ↓
              MediaCacheManager → Local Cache
```

**Gallery View Flow**
```
User Request → ViewModel → Repository → MediaItemDao → Room Database
                                ↓
                         MediaCacheManager (check availability)
                                ↓
                         Return Media List with Cache Status
```

**Offline Access Flow**
```
User Views Media → MediaCacheManager (download if not cached)
                         ↓
                   Local File System
                         ↓
                   Update LRU tracking
```

## Components and Interfaces

### Data Transfer Objects

```kotlin
/**
 * Domain model for media items with full metadata
 */
data class MediaItem(
    val mediaId: String,
    val assetId: String?,
    val url: String,
    val localPath: String?,
    val mediaType: MediaType,
    val tags: List<MediaTag>,
    val dateAdded: Long,
    val fileSize: Long,
    val width: Int?,
    val height: Int?,
    val duration: Int?, // For videos, in seconds
    val thumbnailUrl: String?,
    val isCached: Boolean,
    val uploadStatus: UploadStatus,
    val createdAt: Long,
    val updatedAt: Long
)

enum class MediaType {
    IMAGE, VIDEO
}

enum class UploadStatus {
    PENDING, UPLOADING, COMPLETED, FAILED
}

/**
 * Tag for categorizing media
 */
data class MediaTag(
    val tagId: String,
    val tagType: TagType,
    val value: String
)

enum class TagType {
    ASSET_ID,
    AGE_GROUP,
    SOURCE_TYPE
}

/**
 * Predefined age group categories
 */
enum class AgeGroup(val displayName: String) {
    CHICK("Chick"),
    JUVENILE("Juvenile"),
    ADULT("Adult"),
    SENIOR("Senior")
}

/**
 * Predefined source type categories
 */
enum class SourceType(val displayName: String) {
    FARM_LOG("Farm Log"),
    HEALTH_RECORD("Health Record"),
    GENERAL_ASSET_PHOTO("General Photo")
}

/**
 * Filter criteria for gallery queries
 */
data class MediaFilter(
    val assetId: String? = null,
    val ageGroups: Set<AgeGroup> = emptySet(),
    val sourceTypes: Set<SourceType> = emptySet(),
    val mediaTypes: Set<MediaType> = emptySet(),
    val dateRange: DateRange? = null
)

data class DateRange(
    val startDate: Long,
    val endDate: Long
)

/**
 * Pagination state for infinite scroll
 */
data class PaginationState(
    val pageSize: Int = 20,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false
)

/**
 * Gallery UI state
 */
data class GalleryUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val filter: MediaFilter = MediaFilter(),
    val paginationState: PaginationState = PaginationState(),
    val selectedItems: Set<String> = emptySet(),
    val isOffline: Boolean = false,
    val error: String? = null
)
```

### Repository Interface

```kotlin
interface MediaGalleryRepository {
    /**
     * Query media items with filtering and pagination
     */
    suspend fun queryMedia(
        filter: MediaFilter,
        page: Int,
        pageSize: Int
    ): Result<List<MediaItem>>
    
    /**
     * Get media count matching filter
     */
    suspend fun getMediaCount(filter: MediaFilter): Result<Int>
    
    /**
     * Search assets by identifier or name
     */
    suspend fun searchAssets(query: String): Result<List<AssetSearchResult>>
    
    /**
     * Get all media for specific asset
     */
    suspend fun getAssetMedia(assetId: String, page: Int, pageSize: Int): Result<List<MediaItem>>
    
    /**
     * Delete media item (cloud and local)
     */
    suspend fun deleteMedia(mediaId: String): Result<Unit>
    
    /**
     * Batch delete media items
     */
    suspend fun batchDeleteMedia(mediaIds: List<String>): Result<BatchDeleteResult>
    
    /**
     * Export media items as archive
     */
    suspend fun exportMedia(mediaIds: List<String>): Result<ExportResult>
    
    /**
     * Sync local cache with cloud storage
     */
    suspend fun syncCache(): Result<SyncResult>
    
    /**
     * Observe media changes for real-time updates
     */
    fun observeMedia(filter: MediaFilter): Flow<List<MediaItem>>
}

data class AssetSearchResult(
    val assetId: String,
    val name: String,
    val identifier: String,
    val mediaCount: Int
)

data class BatchDeleteResult(
    val successCount: Int,
    val failedIds: List<String>,
    val errors: Map<String, String>
)

data class ExportResult(
    val archivePath: String,
    val itemCount: Int,
    val totalSize: Long
)

data class SyncResult(
    val syncedCount: Int,
    val failedCount: Int,
    val errors: List<String>
)
```

### Cache Manager Interface

```kotlin
interface MediaCacheManager {
    /**
     * Check if media is cached locally
     */
    suspend fun isCached(mediaId: String): Boolean
    
    /**
     * Download and cache media file
     */
    suspend fun cacheMedia(mediaId: String, url: String): Result<String>
    
    /**
     * Get local path for cached media
     */
    suspend fun getCachedPath(mediaId: String): String?
    
    /**
     * Remove media from cache
     */
    suspend fun evictMedia(mediaId: String): Result<Unit>
    
    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats
    
    /**
     * Clear entire cache
     */
    suspend fun clearCache(): Result<Unit>
    
    /**
     * Prune cache to size limit using LRU
     */
    suspend fun pruneCache(): Result<Int>
}

data class CacheStats(
    val itemCount: Int,
    val totalSize: Long,
    val maxSize: Long,
    val hitRate: Float
)
```

### Upload Integration

```kotlin
/**
 * Extension to MediaUploadManager for gallery integration
 */
data class MediaUploadRequest(
    val localPath: String,
    val assetId: String?,
    val ageGroup: AgeGroup?,
    val sourceType: SourceType,
    val additionalTags: List<MediaTag> = emptyList()
)

/**
 * Upload result with media metadata
 */
data class MediaUploadResult(
    val mediaId: String,
    val downloadUrl: String,
    val thumbnailUrl: String?,
    val tags: List<MediaTag>
)
```

## Data Models

### Database Schema

```kotlin
@Entity(
    tableName = "media_items",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["dateAdded"]),
        Index(value = ["uploadStatus"]),
        Index(value = ["mediaType"])
    ]
)
data class MediaItemEntity(
    @PrimaryKey val mediaId: String,
    val assetId: String?,
    val url: String,
    val localPath: String?,
    val mediaType: String, // IMAGE, VIDEO
    val dateAdded: Long,
    val fileSize: Long,
    val width: Int?,
    val height: Int?,
    val duration: Int?,
    val thumbnailUrl: String?,
    val uploadStatus: String, // PENDING, UPLOADING, COMPLETED, FAILED
    val createdAt: Long,
    val updatedAt: Long,
    val isCached: Boolean = false,
    val lastAccessedAt: Long? = null,
    val dirty: Boolean = false
)

@Entity(
    tableName = "media_tags",
    primaryKeys = ["mediaId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mediaId"]),
        Index(value = ["tagType", "value"])
    ]
)
data class MediaTagEntity(
    val mediaId: String,
    val tagId: String,
    val tagType: String, // ASSET_ID, AGE_GROUP, SOURCE_TYPE
    val value: String,
    val createdAt: Long
)

@Entity(
    tableName = "media_cache_metadata",
    indices = [
        Index(value = ["lastAccessedAt"]),
        Index(value = ["fileSize"])
    ]
)
data class MediaCacheMetadataEntity(
    @PrimaryKey val mediaId: String,
    val localPath: String,
    val fileSize: Long,
    val downloadedAt: Long,
    val lastAccessedAt: Long,
    val accessCount: Int = 0
)

@Entity(tableName = "gallery_filter_state")
data class GalleryFilterStateEntity(
    @PrimaryKey val id: String = "default",
    val ageGroupsJson: String, // JSON array of selected age groups
    val sourceTypesJson: String, // JSON array of selected source types
    val updatedAt: Long
)
```

### DAO Interfaces

```kotlin
@Dao
interface MediaItemDao {
    @Query("""
        SELECT * FROM media_items 
        WHERE (:assetId IS NULL OR assetId = :assetId)
        AND uploadStatus = 'COMPLETED'
        ORDER BY dateAdded DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryMedia(
        assetId: String?,
        limit: Int,
        offset: Int
    ): List<MediaItemEntity>
    
    @Query("""
        SELECT mi.* FROM media_items mi
        INNER JOIN media_tags mt ON mi.mediaId = mt.mediaId
        WHERE mt.tagType = :tagType AND mt.value IN (:values)
        AND uploadStatus = 'COMPLETED'
        GROUP BY mi.mediaId
        ORDER BY mi.dateAdded DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryMediaByTags(
        tagType: String,
        values: List<String>,
        limit: Int,
        offset: Int
    ): List<MediaItemEntity>
    
    @Query("""
        SELECT COUNT(*) FROM media_items 
        WHERE (:assetId IS NULL OR assetId = :assetId)
        AND uploadStatus = 'COMPLETED'
    """)
    suspend fun getMediaCount(assetId: String?): Int
    
    @Query("SELECT * FROM media_items WHERE mediaId = :mediaId")
    suspend fun getMediaById(mediaId: String): MediaItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaItemEntity)
    
    @Update
    suspend fun updateMedia(media: MediaItemEntity)
    
    @Delete
    suspend fun deleteMedia(media: MediaItemEntity)
    
    @Query("DELETE FROM media_items WHERE mediaId IN (:mediaIds)")
    suspend fun batchDeleteMedia(mediaIds: List<String>)
    
    @Query("UPDATE media_items SET isCached = :isCached WHERE mediaId = :mediaId")
    suspend fun updateCacheStatus(mediaId: String, isCached: Boolean)
    
    @Query("UPDATE media_items SET lastAccessedAt = :timestamp WHERE mediaId = :mediaId")
    suspend fun updateLastAccessed(mediaId: String, timestamp: Long)
    
    @Query("""
        SELECT * FROM media_items 
        WHERE uploadStatus = 'COMPLETED'
        ORDER BY dateAdded DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun observeMedia(limit: Int, offset: Int): Flow<List<MediaItemEntity>>
}

@Dao
interface MediaTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: MediaTagEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<MediaTagEntity>)
    
    @Query("SELECT * FROM media_tags WHERE mediaId = :mediaId")
    suspend fun getTagsForMedia(mediaId: String): List<MediaTagEntity>
    
    @Query("DELETE FROM media_tags WHERE mediaId = :mediaId")
    suspend fun deleteTagsForMedia(mediaId: String)
    
    @Query("""
        SELECT DISTINCT value FROM media_tags 
        WHERE tagType = :tagType
    """)
    suspend fun getDistinctTagValues(tagType: String): List<String>
}

@Dao
interface MediaCacheMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheMetadata(metadata: MediaCacheMetadataEntity)
    
    @Query("SELECT * FROM media_cache_metadata WHERE mediaId = :mediaId")
    suspend fun getCacheMetadata(mediaId: String): MediaCacheMetadataEntity?
    
    @Query("UPDATE media_cache_metadata SET lastAccessedAt = :timestamp, accessCount = accessCount + 1 WHERE mediaId = :mediaId")
    suspend fun updateAccess(mediaId: String, timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM media_cache_metadata")
    suspend fun getCacheCount(): Int
    
    @Query("SELECT SUM(fileSize) FROM media_cache_metadata")
    suspend fun getTotalCacheSize(): Long?
    
    @Query("""
        SELECT * FROM media_cache_metadata 
        ORDER BY lastAccessedAt ASC 
        LIMIT :count
    """)
    suspend fun getLeastRecentlyUsed(count: Int): List<MediaCacheMetadataEntity>
    
    @Delete
    suspend fun deleteCacheMetadata(metadata: MediaCacheMetadataEntity)
}

@Dao
interface GalleryFilterStateDao {
    @Query("SELECT * FROM gallery_filter_state WHERE id = :id")
    suspend fun getFilterState(id: String = "default"): GalleryFilterStateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFilterState(state: GalleryFilterStateEntity)
}
```

### Asset Search Index

For efficient asset search, we'll leverage the existing FarmAssetEntity with additional indexing:

```kotlin
/**
 * Search query for assets with media
 */
@Query("""
    SELECT fa.assetId, fa.name, fa.birdCode as identifier, COUNT(mi.mediaId) as mediaCount
    FROM farm_assets fa
    LEFT JOIN media_items mi ON fa.assetId = mi.assetId
    WHERE (fa.name LIKE :query OR fa.birdCode LIKE :query)
    AND fa.isDeleted = 0
    GROUP BY fa.assetId
    ORDER BY mediaCount DESC, fa.name ASC
    LIMIT 20
""")
suspend fun searchAssetsWithMedia(query: String): List<AssetSearchResult>
```


### Firebase Storage Structure

Media files are organized in Firebase Storage with the following path structure:

```
/media/
  /{userId}/
    /assets/
      /{assetId}/
        /{mediaId}.{ext}          # Original media file
        /{mediaId}_thumb.{ext}    # Thumbnail (400px max width)
    /general/
      /{mediaId}.{ext}            # Media not associated with specific asset
```

### Tag Validation Rules

**Asset ID Tags**
- Must reference an existing FarmAssetEntity
- Validated against farm_assets table before insertion
- Automatically added when uploading from asset context

**Age Group Tags**
- Must match one of: CHICK, JUVENILE, ADULT, SENIOR
- Derived from asset's ageWeeks if available
- Can be manually specified during upload

**Source Type Tags**
- Must match one of: FARM_LOG, HEALTH_RECORD, GENERAL_ASSET_PHOTO
- Automatically set based on upload context
- Cannot be changed after upload (immutable)


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Partial Search Matching

*For any* asset identifier and any substring of that identifier, searching for the substring should return the asset in the results.

**Validates: Requirements 1.2**

### Property 2: Asset Media Completeness

*For any* asset with associated media items, selecting that asset should return exactly all media items tagged with that asset's identifier, with no duplicates or omissions.

**Validates: Requirements 1.3, 2.1**

### Property 3: Search Field Coverage

*For any* asset, searching for content from its name, identifier, or tag values should return that asset in the search results.

**Validates: Requirements 1.4**

### Property 4: Chronological Ordering

*For any* list of media items, the returned results should be sorted by dateAdded in descending order (newest first), such that for all adjacent pairs (i, i+1), dateAdded[i] >= dateAdded[i+1].

**Validates: Requirements 2.2, 3.1**

### Property 5: Media Type Support

*For any* media item of type IMAGE or VIDEO, the system should accept, store, and retrieve it correctly with its type preserved.

**Validates: Requirements 2.3**

### Property 6: Pagination Boundaries

*For any* page number and page size, requesting that page should return at most pageSize items, and the items should not overlap with other pages.

**Validates: Requirements 3.2, 9.6**

### Property 7: Date Grouping Correctness

*For any* media item with a dateAdded timestamp, it should be placed in the correct date group: "today" if within last 24 hours, "yesterday" if within 24-48 hours, "last week" if within 7 days, "older" otherwise.

**Validates: Requirements 3.5**

### Property 8: Tag Storage Round-Trip

*For any* set of tags attached to a media item, storing and then retrieving the media item should return all tags with their values preserved.

**Validates: Requirements 4.1, 4.2, 4.3, 4.4**

### Property 9: Asset Reference Validation

*For any* asset identifier tag, if the asset ID does not reference an existing FarmAssetEntity, the tag should be rejected with a validation error.

**Validates: Requirements 4.5**

### Property 10: Tag Enum Validation

*For any* age group or source type tag, if the value is not one of the predefined enum values (CHICK, JUVENILE, ADULT, SENIOR for age groups; FARM_LOG, HEALTH_RECORD, GENERAL_ASSET_PHOTO for source types), the tag should be rejected with a validation error.

**Validates: Requirements 4.6, 4.7**

### Property 11: Invalid Tag Error Handling

*For any* invalid tag (non-existent asset ID, invalid enum value, malformed data), attempting to create a media item with that tag should return a descriptive validation error and not create the item.

**Validates: Requirements 4.8**

### Property 12: Filter AND Logic

*For any* combination of filters (age groups, source types, media types), the returned media items should match ALL selected filter criteria, not just some of them.

**Validates: Requirements 5.3, 5.4**

### Property 13: Filter Count Accuracy

*For any* filter configuration, the count of media items matching the filter should equal the actual number of items returned when querying with that filter.

**Validates: Requirements 5.6**

### Property 14: Filter State Persistence Round-Trip

*For any* filter state (selected age groups, source types, date ranges), saving the state and then loading it should restore all selections exactly as they were.

**Validates: Requirements 5.7**

### Property 15: Context-Based Auto-Tagging

*For any* media upload from a specific context (farm log, health record, asset profile), the media should automatically receive the appropriate source type tag (FARM_LOG, HEALTH_RECORD) or asset identifier tag matching the context.

**Validates: Requirements 6.1, 6.2, 6.3**

### Property 16: Pre-Upload Validation

*For any* media upload request with invalid tags, the validation should fail before the upload begins, preventing any file transfer or storage operations.

**Validates: Requirements 6.5**

### Property 17: Upload Retry Limit

*For any* failed upload, the system should retry up to 3 times with exponential backoff, and after 3 failures, should report a final failure without further retries.

**Validates: Requirements 6.6**

### Property 18: Upload Cache Update

*For any* successful media upload, the local cache should be updated immediately with the new media item and its metadata.

**Validates: Requirements 6.7**

### Property 19: View-Triggered Caching

*For any* media item that is viewed, if it is not already cached, it should be downloaded and stored in the local cache.

**Validates: Requirements 7.1**

### Property 20: Cache Size Limit

*For any* sequence of cache operations, the total number of cached media items should never exceed 100.

**Validates: Requirements 7.2**

### Property 21: LRU Eviction Policy

*For any* cache at capacity (100 items), when a new item is added, the item with the oldest lastAccessedAt timestamp should be evicted.

**Validates: Requirements 7.3**

### Property 22: Offline Cache Access

*For any* cached media item, when the system is offline, the item should still be retrievable from the local cache with all its metadata.

**Validates: Requirements 7.5**

### Property 23: Online Sync Trigger

*For any* transition from offline to online state, the system should trigger a synchronization operation between the local cache and cloud storage.

**Validates: Requirements 7.6**

### Property 24: Cache Metadata Completeness

*For any* media item stored in cache, retrieving it should return all metadata (tags, dateAdded, fileSize, dimensions) exactly as it was when cached.

**Validates: Requirements 7.7**

### Property 25: Complete Deletion

*For any* media item, when deletion is confirmed, the item should be removed from cloud storage, local cache, and database, such that subsequent queries for that mediaId return no results.

**Validates: Requirements 8.2, 8.3, 8.4**

### Property 26: Failed Deletion Preservation

*For any* media deletion that fails (network error, permission error), the media item should remain in the database and be accessible in subsequent queries.

**Validates: Requirements 8.6**

### Property 27: Batch Deletion Completeness

*For any* set of media IDs selected for batch deletion, all items should be removed from storage, cache, and database, with the batch result indicating success count and any failures.

**Validates: Requirements 8.7**

### Property 28: Thumbnail Size Constraint

*For any* thumbnail image generated for list display, the width should not exceed 400 pixels.

**Validates: Requirements 9.2**

### Property 29: Lazy Loading Full Resolution

*For any* media item displayed in a list view, the full-resolution image should not be loaded until the item is opened in the full-screen viewer.

**Validates: Requirements 9.3**

### Property 30: Prefetch Threshold

*For any* paginated media list, when the scroll position reaches 80% of the current page, the next page should be prefetched.

**Validates: Requirements 9.5**

### Property 31: Metadata Tag Completeness

*For any* media item with associated tags, retrieving the metadata should include all tags with their type and value.

**Validates: Requirements 10.2**

### Property 32: Video Format Support

*For any* video file with format MP4, MOV, or AVI, the system should accept and store it with the correct mediaType of VIDEO.

**Validates: Requirements 11.6**

### Property 33: Video Playback Error Handling

*For any* video playback failure (unsupported format, corrupted file), the system should return an error message indicating the failure reason and format information.

**Validates: Requirements 11.7**

### Property 34: Single Item Share

*For any* media item, initiating a share operation should provide access to that specific media file for sharing.

**Validates: Requirements 12.3**

### Property 35: Batch Export Completeness

*For any* set of media IDs selected for export, the export operation should include all selected items in the output.

**Validates: Requirements 12.4**

### Property 36: Export Archive Format

*For any* batch export of multiple media items, the output should be a valid ZIP archive containing all media files.

**Validates: Requirements 12.5**

### Property 37: Export Metadata Manifest

*For any* batch export, the ZIP archive should contain a JSON manifest file with metadata (tags, dateAdded, fileSize) for each exported media item.

**Validates: Requirements 12.6**

### Property 38: Export Error Handling

*For any* export operation that fails (insufficient storage, permission error), the system should return an error message with the specific failure reason.

**Validates: Requirements 12.7**

### Property 39: Pagination Consistency

*For any* two consecutive requests for the same page with the same filter, if no data has changed, the results should be identical.

**Validates: Requirements 3.2**

### Property 40: Search Result Ordering

*For any* search query that returns multiple assets, the results should be ordered by media count (descending) then by asset name (ascending).

**Validates: Requirements 1.1**


## Error Handling

### Error Categories

**Validation Errors**
- Invalid tag values (non-existent asset IDs, invalid enums)
- Malformed media files (corrupted, unsupported formats)
- Invalid filter combinations
- Missing required fields

**Network Errors**
- Upload failures (timeout, connection lost, quota exceeded)
- Download failures (file not found, access denied)
- Sync failures (conflict resolution, version mismatch)

**Storage Errors**
- Insufficient local storage for cache
- Cloud storage quota exceeded
- File system permission errors
- Cache corruption

**Data Integrity Errors**
- Orphaned media items (asset deleted but media remains)
- Missing thumbnails
- Inconsistent cache state
- Database constraint violations

### Error Handling Strategies

**Validation Errors**
```kotlin
sealed class ValidationError : Exception() {
    data class InvalidAssetId(val assetId: String) : ValidationError()
    data class InvalidAgeGroup(val value: String) : ValidationError()
    data class InvalidSourceType(val value: String) : ValidationError()
    data class UnsupportedMediaFormat(val format: String) : ValidationError()
}
```

Strategy: Fail fast with descriptive error messages. Prevent invalid data from entering the system.

**Network Errors**
```kotlin
sealed class NetworkError : Exception() {
    data class UploadFailed(val attempt: Int, val reason: String) : NetworkError()
    data class DownloadFailed(val mediaId: String, val reason: String) : NetworkError()
    object QuotaExceeded : NetworkError()
    object Timeout : NetworkError()
}
```

Strategy: Implement exponential backoff retry (up to 3 attempts). Queue operations for later retry when offline. Provide clear user feedback about network issues.

**Storage Errors**
```kotlin
sealed class StorageError : Exception() {
    object InsufficientSpace : StorageError()
    data class PermissionDenied(val path: String) : StorageError()
    data class CacheCorruption(val mediaId: String) : StorageError()
}
```

Strategy: Graceful degradation. When cache is full, evict LRU items. When local storage fails, continue with cloud-only mode. Provide user notifications for critical storage issues.

**Data Integrity Errors**
```kotlin
sealed class IntegrityError : Exception() {
    data class OrphanedMedia(val mediaId: String, val assetId: String) : IntegrityError()
    data class MissingThumbnail(val mediaId: String) : IntegrityError()
    data class CacheInconsistency(val mediaId: String) : IntegrityError()
}
```

Strategy: Implement background cleanup jobs. Log integrity issues for monitoring. Attempt automatic repair when possible (regenerate thumbnails, remove orphans).

### Error Recovery Mechanisms

**Upload Retry Logic**
```kotlin
suspend fun uploadWithRetry(
    request: MediaUploadRequest,
    maxAttempts: Int = 3
): Result<MediaUploadResult> {
    var attempt = 0
    var lastError: Exception? = null
    
    while (attempt < maxAttempts) {
        try {
            return Result.success(performUpload(request))
        } catch (e: Exception) {
            lastError = e
            attempt++
            if (attempt < maxAttempts) {
                val backoffMs = 500L * (1 shl (attempt - 1)) // Exponential backoff
                delay(backoffMs)
            }
        }
    }
    
    return Result.failure(lastError ?: Exception("Upload failed"))
}
```

**Cache Corruption Recovery**
```kotlin
suspend fun recoverFromCacheCorruption(mediaId: String) {
    try {
        // Remove corrupted cache entry
        cacheManager.evictMedia(mediaId)
        
        // Update database to reflect cache status
        mediaItemDao.updateCacheStatus(mediaId, isCached = false)
        
        // Re-download from cloud if online
        if (connectivityManager.isOnline()) {
            val media = mediaItemDao.getMediaById(mediaId)
            media?.let {
                cacheManager.cacheMedia(it.mediaId, it.url)
            }
        }
    } catch (e: Exception) {
        // Log for monitoring
        logger.error("Failed to recover from cache corruption", e)
    }
}
```

**Orphaned Media Cleanup**
```kotlin
suspend fun cleanupOrphanedMedia() {
    val orphanedMedia = mediaItemDao.queryMedia(
        assetId = null,
        limit = 100,
        offset = 0
    ).filter { media ->
        media.assetId != null && !assetExists(media.assetId)
    }
    
    orphanedMedia.forEach { media ->
        try {
            deleteMedia(media.mediaId)
        } catch (e: Exception) {
            logger.warn("Failed to delete orphaned media ${media.mediaId}", e)
        }
    }
}
```

### Edge Cases

**Empty States**
- Asset with no media: Display empty state message with option to add media
- Gallery with no media: Display onboarding message
- Search with no results: Display "No assets found" with search tips
- Filter with no matches: Display "No media matches filters" with option to clear filters

**Boundary Conditions**
- Cache at exactly 100 items: Evict LRU before adding new item
- Page request beyond available data: Return empty list with hasMore = false
- Concurrent deletions: Use database transactions to prevent race conditions
- Simultaneous uploads of same file: Deduplicate by content hash

**Race Conditions**
- Concurrent cache eviction and access: Use locks to ensure consistency
- Simultaneous filter updates: Debounce filter changes to prevent excessive queries
- Parallel uploads with same tags: Allow, as tags are not unique constraints
- Delete during upload: Cancel upload if media is deleted before completion

## Testing Strategy

### Dual Testing Approach

The testing strategy employs both unit tests and property-based tests to ensure comprehensive coverage:

**Unit Tests** focus on:
- Specific examples demonstrating correct behavior
- Edge cases and boundary conditions
- Error handling scenarios
- Integration points between components
- UI interaction flows

**Property-Based Tests** focus on:
- Universal properties that hold for all inputs
- Comprehensive input coverage through randomization
- Invariants that must be maintained
- Round-trip properties (serialization, caching)
- Metamorphic properties (counts, ordering)

Together, these approaches provide comprehensive coverage where unit tests catch concrete bugs and property tests verify general correctness.

### Property-Based Testing Configuration

**Framework**: Use Kotest Property Testing for Kotlin
- Minimum 100 iterations per property test (due to randomization)
- Each property test references its design document property
- Tag format: `Feature: asset-multimedia-gallery, Property {number}: {property_text}`

**Example Property Test Structure**:
```kotlin
class MediaGalleryPropertyTests : StringSpec({
    "Property 4: Chronological Ordering" {
        // Feature: asset-multimedia-gallery, Property 4: Chronological Ordering
        checkAll(100, Arb.list(Arb.mediaItem(), 1..100)) { mediaItems ->
            val sorted = repository.queryMedia(
                filter = MediaFilter(),
                page = 0,
                pageSize = mediaItems.size
            ).getOrThrow()
            
            sorted.zipWithNext().forEach { (current, next) ->
                current.dateAdded shouldBeGreaterThanOrEqualTo next.dateAdded
            }
        }
    }
    
    "Property 8: Tag Storage Round-Trip" {
        // Feature: asset-multimedia-gallery, Property 8: Tag Storage Round-Trip
        checkAll(100, Arb.mediaItem(), Arb.set(Arb.mediaTag(), 1..10)) { media, tags ->
            val mediaWithTags = media.copy(tags = tags.toList())
            
            // Store
            repository.insertMedia(mediaWithTags)
            
            // Retrieve
            val retrieved = repository.getMediaById(media.mediaId).getOrThrow()
            
            // Verify all tags preserved
            retrieved.tags.toSet() shouldBe tags
        }
    }
})
```

### Unit Test Coverage

**Repository Layer Tests**
- Query with various filter combinations
- Pagination edge cases (first page, last page, beyond available data)
- Search with partial matches, special characters
- Delete operations (single, batch, with failures)
- Export operations with various item counts
- Sync operations with conflicts

**Cache Manager Tests**
- LRU eviction at capacity
- Cache hit/miss scenarios
- Corruption recovery
- Offline access
- Cache statistics accuracy

**DAO Layer Tests**
- Complex queries with multiple joins
- Index usage verification
- Transaction rollback on errors
- Concurrent access handling

**Upload Manager Integration Tests**
- Automatic tagging from different contexts
- Retry logic with simulated failures
- Progress tracking
- Validation before upload

**ViewModel Tests**
- State management
- Filter persistence
- Pagination state updates
- Error handling and user feedback

### Test Data Generators

```kotlin
object MediaItemArb {
    fun mediaItem() = arbitrary {
        MediaItem(
            mediaId = Arb.uuid().bind().toString(),
            assetId = Arb.string(10..20).orNull().bind(),
            url = "https://storage.example.com/${Arb.uuid().bind()}",
            localPath = Arb.string(20..50).orNull().bind(),
            mediaType = Arb.enum<MediaType>().bind(),
            tags = Arb.list(mediaTag(), 0..5).bind(),
            dateAdded = Arb.long(1_600_000_000_000L..1_700_000_000_000L).bind(),
            fileSize = Arb.long(100_000L..10_000_000L).bind(),
            width = Arb.int(100..4000).orNull().bind(),
            height = Arb.int(100..4000).orNull().bind(),
            duration = if (bind(Arb.enum<MediaType>()) == MediaType.VIDEO) {
                Arb.int(1..600).bind()
            } else null,
            thumbnailUrl = "https://storage.example.com/thumb_${Arb.uuid().bind()}",
            isCached = Arb.bool().bind(),
            uploadStatus = Arb.enum<UploadStatus>().bind(),
            createdAt = Arb.long(1_600_000_000_000L..1_700_000_000_000L).bind(),
            updatedAt = Arb.long(1_600_000_000_000L..1_700_000_000_000L).bind()
        )
    }
    
    fun mediaTag() = arbitrary {
        val tagType = Arb.enum<TagType>().bind()
        val value = when (tagType) {
            TagType.ASSET_ID -> Arb.uuid().bind().toString()
            TagType.AGE_GROUP -> Arb.enum<AgeGroup>().bind().name
            TagType.SOURCE_TYPE -> Arb.enum<SourceType>().bind().name
        }
        
        MediaTag(
            tagId = Arb.uuid().bind().toString(),
            tagType = tagType,
            value = value
        )
    }
    
    fun mediaFilter() = arbitrary {
        MediaFilter(
            assetId = Arb.string(10..20).orNull().bind(),
            ageGroups = Arb.set(Arb.enum<AgeGroup>(), 0..4).bind(),
            sourceTypes = Arb.set(Arb.enum<SourceType>(), 0..3).bind(),
            mediaTypes = Arb.set(Arb.enum<MediaType>(), 0..2).bind(),
            dateRange = Arb.dateRange().orNull().bind()
        )
    }
}
```

### Performance Testing

**Load Testing Scenarios**
- Gallery with 10,000+ media items
- Search across 1,000+ assets
- Concurrent uploads (10 simultaneous)
- Cache operations under memory pressure
- Pagination through large result sets

**Performance Benchmarks**
- Initial gallery load: < 1 second
- Search query response: < 500ms
- Page load (20 items): < 300ms
- Cache lookup: < 50ms
- Thumbnail generation: < 200ms per image

### Integration Testing

**End-to-End Scenarios**
1. Upload media from farm log → verify auto-tagging → view in gallery
2. Search for asset → select asset → view asset media → delete media
3. Apply filters → verify count → export filtered results
4. Go offline → view cached media → go online → verify sync
5. Upload multiple media → batch delete → verify complete removal

**Cross-Component Integration**
- MediaUploadManager → Repository → DAO → Database
- Repository → CacheManager → File System
- ViewModel → Repository → StorageRepository → Firebase
- Search → FarmAssetEntity → MediaItemEntity join queries

### Continuous Testing

**Automated Test Execution**
- Run unit tests on every commit
- Run property tests on pull requests
- Run integration tests nightly
- Run performance tests weekly

**Test Coverage Goals**
- Unit test coverage: > 80%
- Property test coverage: All 40 properties implemented
- Integration test coverage: All critical user flows
- Edge case coverage: All identified edge cases tested

