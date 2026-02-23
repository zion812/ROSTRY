# Implementation Plan: Asset Multimedia Gallery

## Overview

This implementation plan breaks down the Asset Multimedia Gallery feature into discrete coding tasks. The feature provides two primary interfaces: an asset-specific media view for browsing multimedia associated with a particular bird, and a centralized chronological gallery for discovering all farm media sorted by date. The system implements a robust tagging mechanism, offline caching with LRU eviction, and performance optimizations for large collections.

The implementation follows a bottom-up approach: data layer first (entities, DAOs), then domain layer (repository, cache manager), then presentation layer (ViewModels, UI screens), and finally integration with existing upload infrastructure.

## Tasks

- [ ] 1. Set up database schema and entities
  - [ ] 1.1 Create MediaItemEntity with Room annotations
    - Define entity with all fields (mediaId, assetId, url, localPath, mediaType, tags, dateAdded, fileSize, dimensions, thumbnailUrl, uploadStatus, timestamps)
    - Add indices for assetId, dateAdded, uploadStatus, mediaType
    - _Requirements: 2.1, 3.1, 4.4, 10.1-10.4_

  - [ ] 1.2 Create MediaTagEntity with foreign key relationship
    - Define composite primary key (mediaId, tagId)
    - Add foreign key to MediaItemEntity with CASCADE delete
    - Add indices for mediaId and (tagType, value) combination
    - _Requirements: 4.1-4.4, 10.2_

  - [ ] 1.3 Create MediaCacheMetadataEntity for LRU tracking
    - Define entity with mediaId, localPath, fileSize, downloadedAt, lastAccessedAt, accessCount
    - Add indices for lastAccessedAt and fileSize
    - _Requirements: 7.1-7.3, 7.7_

  - [ ] 1.4 Create GalleryFilterStateEntity for filter persistence
    - Define entity with JSON fields for ageGroups and sourceTypes
    - _Requirements: 5.7_


- [ ] 2. Implement DAO layer with optimized queries
  - [ ] 2.1 Create MediaItemDao with pagination queries
    - Implement queryMedia with assetId filter, limit, offset
    - Implement queryMediaByTags with tag filtering and grouping
    - Implement getMediaCount for filter result counts
    - Add CRUD operations (insert, update, delete, batchDelete)
    - Add cache status and lastAccessed update methods
    - _Requirements: 2.1, 3.1-3.3, 5.3-5.6, 9.6_

  - [ ]* 2.2 Write property test for MediaItemDao
    - **Property 4: Chronological Ordering**
    - **Validates: Requirements 2.2, 3.1**

  - [ ] 2.3 Create MediaTagDao with tag management operations
    - Implement insertTag, insertTags for batch operations
    - Implement getTagsForMedia to retrieve all tags for a media item
    - Implement deleteTagsForMedia for cascade cleanup
    - Implement getDistinctTagValues for filter options
    - _Requirements: 4.1-4.4, 5.1-5.2_

  - [ ]* 2.4 Write property test for MediaTagDao
    - **Property 8: Tag Storage Round-Trip**
    - **Validates: Requirements 4.1-4.4**

  - [ ] 2.5 Create MediaCacheMetadataDao for LRU cache tracking
    - Implement insertCacheMetadata, getCacheMetadata
    - Implement updateAccess to track lastAccessedAt and accessCount
    - Implement getCacheCount and getTotalCacheSize for statistics
    - Implement getLeastRecentlyUsed for eviction policy
    - _Requirements: 7.1-7.3_

  - [ ]* 2.6 Write property test for cache LRU eviction
    - **Property 21: LRU Eviction Policy**
    - **Validates: Requirements 7.3**

  - [ ] 2.7 Create GalleryFilterStateDao for filter persistence
    - Implement getFilterState and saveFilterState
    - _Requirements: 5.7_

  - [ ]* 2.8 Write property test for filter persistence
    - **Property 14: Filter State Persistence Round-Trip**
    - **Validates: Requirements 5.7**


- [ ] 3. Implement domain models and validation
  - [ ] 3.1 Create domain data classes (MediaItem, MediaTag, MediaFilter, PaginationState)
    - Define MediaItem with all metadata fields
    - Define MediaTag with TagType enum (ASSET_ID, AGE_GROUP, SOURCE_TYPE)
    - Define AgeGroup enum (CHICK, JUVENILE, ADULT, SENIOR)
    - Define SourceType enum (FARM_LOG, HEALTH_RECORD, GENERAL_ASSET_PHOTO)
    - Define MediaFilter with optional filtering criteria
    - Define PaginationState for infinite scroll
    - _Requirements: 2.3, 3.2, 4.2-4.3, 5.1-5.4_

  - [ ] 3.2 Implement tag validation logic
    - Create TagValidator to validate asset ID references against FarmAssetEntity
    - Validate AgeGroup enum values
    - Validate SourceType enum values
    - Return descriptive ValidationError for invalid tags
    - _Requirements: 4.5-4.8_

  - [ ]* 3.3 Write property tests for tag validation
    - **Property 9: Asset Reference Validation**
    - **Property 10: Tag Enum Validation**
    - **Property 11: Invalid Tag Error Handling**
    - **Validates: Requirements 4.5-4.8**

  - [ ] 3.3 Create error sealed classes
    - Define ValidationError (InvalidAssetId, InvalidAgeGroup, InvalidSourceType, UnsupportedMediaFormat)
    - Define NetworkError (UploadFailed, DownloadFailed, QuotaExceeded, Timeout)
    - Define StorageError (InsufficientSpace, PermissionDenied, CacheCorruption)
    - Define IntegrityError (OrphanedMedia, MissingThumbnail, CacheInconsistency)
    - _Requirements: 4.8, 6.6, 8.6, 12.7_


- [ ] 4. Implement MediaCacheManager for offline access
  - [ ] 4.1 Create MediaCacheManager interface and implementation
    - Implement isCached to check local availability
    - Implement cacheMedia to download and store files locally
    - Implement getCachedPath to retrieve local file paths
    - Implement evictMedia to remove files from cache
    - Implement getCacheStats for monitoring
    - Implement clearCache and pruneCache for maintenance
    - _Requirements: 7.1, 7.4-7.5_

  - [ ] 4.2 Implement LRU eviction policy
    - Track lastAccessedAt timestamp on every cache access
    - When cache reaches 100 items, evict least recently used
    - Update MediaCacheMetadataDao during eviction
    - _Requirements: 7.2-7.3_

  - [ ]* 4.3 Write property tests for cache operations
    - **Property 20: Cache Size Limit**
    - **Property 21: LRU Eviction Policy**
    - **Property 22: Offline Cache Access**
    - **Validates: Requirements 7.2-7.5**

  - [ ] 4.4 Implement cache corruption recovery
    - Detect corrupted cache entries
    - Remove corrupted files and update database
    - Re-download from cloud if online
    - _Requirements: 7.5_


- [ ] 5. Implement MediaGalleryRepository
  - [ ] 5.1 Create MediaGalleryRepository interface and implementation
    - Implement queryMedia with MediaFilter and pagination
    - Implement getMediaCount for filter result counts
    - Implement searchAssets with partial matching
    - Implement getAssetMedia for asset-specific queries
    - Coordinate between MediaItemDao, MediaTagDao, and MediaCacheManager
    - _Requirements: 1.1-1.4, 2.1, 3.1-3.3, 5.3-5.6_

  - [ ]* 5.2 Write property tests for repository queries
    - **Property 1: Partial Search Matching**
    - **Property 2: Asset Media Completeness**
    - **Property 3: Search Field Coverage**
    - **Property 6: Pagination Boundaries**
    - **Property 12: Filter AND Logic**
    - **Property 13: Filter Count Accuracy**
    - **Validates: Requirements 1.1-1.4, 2.1, 3.2, 5.3-5.6**

  - [ ] 5.3 Implement media deletion operations
    - Implement deleteMedia to remove from cloud, cache, and database
    - Implement batchDeleteMedia with partial failure handling
    - Return BatchDeleteResult with success/failure counts
    - _Requirements: 8.1-8.7_

  - [ ]* 5.4 Write property tests for deletion
    - **Property 25: Complete Deletion**
    - **Property 26: Failed Deletion Preservation**
    - **Property 27: Batch Deletion Completeness**
    - **Validates: Requirements 8.2-8.7**

  - [ ] 5.5 Implement export operations
    - Implement exportMedia to create ZIP archive with media files
    - Include JSON manifest with metadata (tags, dateAdded, fileSize)
    - Return ExportResult with archive path and statistics
    - _Requirements: 12.4-12.7_

  - [ ]* 5.6 Write property tests for export
    - **Property 35: Batch Export Completeness**
    - **Property 36: Export Archive Format**
    - **Property 37: Export Metadata Manifest**
    - **Validates: Requirements 12.4-12.6**

  - [ ] 5.7 Implement cache synchronization
    - Implement syncCache to reconcile local and cloud storage
    - Handle conflicts and version mismatches
    - Return SyncResult with sync statistics
    - _Requirements: 7.6_

  - [ ] 5.8 Implement observeMedia for real-time updates
    - Return Flow<List<MediaItem>> for reactive UI updates
    - _Requirements: 3.3_


- [ ] 6. Checkpoint - Ensure data and domain layers are complete
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 7. Integrate with MediaUploadManager for automatic tagging
  - [ ] 7.1 Create MediaUploadRequest data class
    - Define fields: localPath, assetId, ageGroup, sourceType, additionalTags
    - _Requirements: 6.1-6.4_

  - [ ] 7.2 Extend MediaUploadManager with gallery integration
    - Implement automatic tagging based on upload context (farm log, health record, asset profile)
    - Validate tags before upload using TagValidator
    - Implement retry logic with exponential backoff (max 3 attempts)
    - Update Local_Cache immediately on successful upload
    - _Requirements: 6.1-6.7_

  - [ ]* 7.3 Write property tests for upload integration
    - **Property 15: Context-Based Auto-Tagging**
    - **Property 16: Pre-Upload Validation**
    - **Property 17: Upload Retry Limit**
    - **Property 18: Upload Cache Update**
    - **Validates: Requirements 6.1-6.7**

  - [ ] 7.4 Implement thumbnail generation
    - Generate thumbnails with max 400px width for list display
    - Store thumbnail URLs in MediaItemEntity
    - _Requirements: 9.2_

  - [ ]* 7.5 Write property test for thumbnail generation
    - **Property 28: Thumbnail Size Constraint**
    - **Validates: Requirements 9.2**


- [ ] 8. Implement MediaGalleryViewModel
  - [ ] 8.1 Create GalleryUiState data class
    - Define fields: mediaItems, filter, paginationState, selectedItems, isOffline, error
    - _Requirements: 3.1-3.7, 5.1-5.7_

  - [ ] 8.2 Implement MediaGalleryViewModel with state management
    - Manage GalleryUiState with StateFlow
    - Implement loadMedia with pagination support
    - Implement loadNextPage for infinite scroll
    - Implement applyFilter with filter persistence
    - Implement deleteMedia and batchDeleteMedia
    - Implement exportMedia and shareMedia
    - Handle offline/online state transitions
    - _Requirements: 2.1-2.7, 3.1-3.7, 5.1-5.7, 8.1-8.7, 12.1-12.7_

  - [ ] 8.3 Implement asset search functionality
    - Implement searchAssets with debouncing (300ms)
    - Return AssetSearchResult with media count
    - _Requirements: 1.1-1.5_

  - [ ] 8.4 Implement filter state persistence
    - Load filter state on ViewModel initialization
    - Save filter state on filter changes
    - _Requirements: 5.7_

  - [ ]* 8.5 Write unit tests for ViewModel
    - Test state management and user interactions
    - Test pagination state updates
    - Test filter persistence
    - Test error handling
    - _Requirements: 2.1-2.7, 3.1-3.7, 5.1-5.7_


- [ ] 9. Implement GalleryScreen UI
  - [ ] 9.1 Create GalleryScreen composable with infinite scroll
    - Display media items in LazyVerticalGrid with 3 columns
    - Implement infinite scroll with pagination trigger at 80% scroll
    - Display date grouping headers (today, yesterday, last week, older)
    - Show loading indicators during pagination
    - Display empty state when no media exists
    - _Requirements: 3.1-3.7, 9.1, 9.4-9.5_

  - [ ] 9.2 Implement filter UI controls
    - Create FilterBar with age group and source type chips
    - Display media count matching current filters
    - Persist filter selections across navigation
    - _Requirements: 5.1-5.7_

  - [ ] 9.3 Implement media item card with thumbnail
    - Display thumbnail images (max 400px width)
    - Show video play icon overlay for videos
    - Show video duration on video thumbnails
    - Display offline indicator for cached items
    - _Requirements: 2.5, 7.4, 9.2, 11.1, 11.4_

  - [ ] 9.4 Implement long-press selection for batch operations
    - Enable multi-select mode on long-press
    - Show selection checkboxes in multi-select mode
    - Display action bar with delete and export options
    - _Requirements: 8.1, 8.7, 12.4_

  - [ ] 9.5 Implement full-screen media viewer
    - Open on media item tap
    - Support swipe navigation between items
    - Display metadata overlay (date, tags, file size, resolution)
    - Toggle metadata visibility on tap
    - Show share button in toolbar
    - _Requirements: 2.6, 3.6, 10.1-10.7, 12.1-12.3_

  - [ ]* 9.6 Write UI tests for GalleryScreen
    - Test infinite scroll behavior
    - Test filter interactions
    - Test batch selection and deletion
    - Test full-screen viewer navigation
    - _Requirements: 3.1-3.7, 5.1-5.7, 8.1-8.7_


- [ ] 10. Implement AssetMediaScreen UI
  - [ ] 10.1 Create AssetMediaScreen composable with asset search
    - Implement search bar with asset identifier/name search
    - Display search results with asset name, identifier, and media count
    - Show loading indicator during search
    - Display "No assets found" message for empty results
    - _Requirements: 1.1-1.5_

  - [ ] 10.2 Implement asset-specific media grid
    - Display all media items for selected asset
    - Use same media item card component as GalleryScreen
    - Display empty state when asset has no media
    - Support pagination for large media collections
    - _Requirements: 2.1-2.7_

  - [ ] 10.3 Integrate with full-screen viewer
    - Open full-screen viewer on media item tap
    - Pass asset context to viewer
    - Display link to asset profile in metadata overlay
    - _Requirements: 2.6, 10.5_

  - [ ]* 10.4 Write UI tests for AssetMediaScreen
    - Test asset search functionality
    - Test media grid display
    - Test empty states
    - Test navigation to full-screen viewer
    - _Requirements: 1.1-1.5, 2.1-2.7_


- [ ] 11. Implement video playback support
  - [ ] 11.1 Create VideoPlayerScreen composable
    - Integrate ExoPlayer for video playback
    - Implement standard controls (play, pause, seek, volume)
    - Display video duration and current position
    - Prevent device sleep during playback
    - _Requirements: 11.2-11.5_

  - [ ] 11.2 Implement video format validation
    - Validate supported formats (MP4, MOV, AVI)
    - Return descriptive error for unsupported formats
    - _Requirements: 11.6-11.7_

  - [ ]* 11.3 Write property tests for video support
    - **Property 32: Video Format Support**
    - **Property 33: Video Playback Error Handling**
    - **Validates: Requirements 11.6-11.7**

  - [ ]* 11.4 Write UI tests for video playback
    - Test video player controls
    - Test error handling for unsupported formats
    - Test device sleep prevention
    - _Requirements: 11.2-11.7_


- [ ] 12. Implement media export and sharing
  - [ ] 12.1 Implement single media share
    - Create share intent with media file
    - Open system share sheet
    - _Requirements: 12.1-12.3_

  - [ ]* 12.2 Write property test for single item share
    - **Property 34: Single Item Share**
    - **Validates: Requirements 12.3**

  - [ ] 12.3 Implement batch export to ZIP archive
    - Create ZIP archive with selected media files
    - Generate JSON manifest with metadata (tags, dateAdded, fileSize)
    - Include manifest in ZIP archive
    - Return ExportResult with archive path and statistics
    - _Requirements: 12.4-12.6_

  - [ ]* 12.4 Write property tests for batch export
    - **Property 35: Batch Export Completeness**
    - **Property 36: Export Archive Format**
    - **Property 37: Export Metadata Manifest**
    - **Property 38: Export Error Handling**
    - **Validates: Requirements 12.4-12.7**

  - [ ] 12.5 Implement export error handling
    - Handle insufficient storage errors
    - Handle permission errors
    - Return descriptive error messages
    - _Requirements: 12.7_


- [ ] 13. Checkpoint - Ensure presentation layer is complete
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 14. Implement performance optimizations
  - [ ] 14.1 Add database indices for query optimization
    - Verify indices on assetId, dateAdded, uploadStatus, mediaType in MediaItemEntity
    - Verify indices on (tagType, value) in MediaTagEntity
    - Verify indices on lastAccessedAt in MediaCacheMetadataEntity
    - _Requirements: 9.7_

  - [ ] 14.2 Implement virtual scrolling for large collections
    - Use LazyVerticalGrid for efficient rendering
    - Implement item recycling for collections > 100 items
    - _Requirements: 9.4_

  - [ ] 14.3 Implement prefetching for pagination
    - Trigger next page load at 80% scroll position
    - Prefetch thumbnails for next page
    - _Requirements: 9.5_

  - [ ]* 14.4 Write property tests for performance
    - **Property 29: Lazy Loading Full Resolution**
    - **Property 30: Prefetch Threshold**
    - **Validates: Requirements 9.3, 9.5**

  - [ ]* 14.5 Write performance tests
    - Test initial gallery load time (< 1 second)
    - Test search query response time (< 500ms)
    - Test page load time (< 300ms)
    - Test cache lookup time (< 50ms)
    - _Requirements: 1.1, 9.1_


- [ ] 15. Implement background maintenance and cleanup
  - [ ] 15.1 Implement orphaned media cleanup job
    - Query media items with assetId that no longer exists in FarmAssetEntity
    - Delete orphaned media from cloud, cache, and database
    - Schedule periodic cleanup (daily)
    - _Requirements: 8.2-8.4_

  - [ ] 15.2 Implement cache pruning job
    - Check cache size against limit (100 items)
    - Evict least recently used items if over limit
    - Schedule periodic pruning (hourly)
    - _Requirements: 7.2-7.3_

  - [ ] 15.3 Implement cache corruption detection and recovery
    - Verify cache file integrity on access
    - Remove corrupted files and update database
    - Re-download from cloud if online
    - _Requirements: 7.5_

  - [ ]* 15.4 Write integration tests for maintenance jobs
    - Test orphaned media cleanup
    - Test cache pruning
    - Test corruption recovery
    - _Requirements: 7.2-7.5, 8.2-8.4_


- [ ] 16. Implement offline/online synchronization
  - [ ] 16.1 Implement connectivity monitoring
    - Monitor network connectivity state changes
    - Update GalleryUiState.isOffline on connectivity changes
    - _Requirements: 7.4-7.6_

  - [ ] 16.2 Implement cache synchronization on connectivity restore
    - Trigger syncCache when transitioning from offline to online
    - Reconcile local cache with cloud storage
    - Handle conflicts and version mismatches
    - _Requirements: 7.6_

  - [ ]* 16.3 Write property test for online sync trigger
    - **Property 23: Online Sync Trigger**
    - **Validates: Requirements 7.6**

  - [ ]* 16.4 Write integration tests for offline/online scenarios
    - Test offline media access
    - Test sync on connectivity restore
    - Test conflict resolution
    - _Requirements: 7.4-7.6_


- [ ] 17. Implement comprehensive property-based tests
  - [ ]* 17.1 Write property tests for search and filtering
    - **Property 1: Partial Search Matching**
    - **Property 3: Search Field Coverage**
    - **Property 12: Filter AND Logic**
    - **Property 13: Filter Count Accuracy**
    - **Property 40: Search Result Ordering**
    - **Validates: Requirements 1.1-1.4, 5.3-5.6**

  - [ ]* 17.2 Write property tests for pagination and ordering
    - **Property 4: Chronological Ordering**
    - **Property 6: Pagination Boundaries**
    - **Property 7: Date Grouping Correctness**
    - **Property 39: Pagination Consistency**
    - **Validates: Requirements 2.2, 3.1-3.2, 3.5, 9.6**

  - [ ]* 17.3 Write property tests for media type support
    - **Property 5: Media Type Support**
    - **Validates: Requirements 2.3**

  - [ ]* 17.4 Write property tests for cache operations
    - **Property 19: View-Triggered Caching**
    - **Property 24: Cache Metadata Completeness**
    - **Validates: Requirements 7.1, 7.7**

  - [ ]* 17.5 Write property tests for metadata display
    - **Property 31: Metadata Tag Completeness**
    - **Validates: Requirements 10.2**


- [ ] 18. Final integration and wiring
  - [ ] 18.1 Wire MediaGalleryRepository into dependency injection
    - Register MediaGalleryRepository in DI container
    - Inject into MediaGalleryViewModel
    - _Requirements: All_

  - [ ] 18.2 Wire MediaCacheManager into dependency injection
    - Register MediaCacheManager in DI container
    - Inject into MediaGalleryRepository
    - _Requirements: 7.1-7.7_

  - [ ] 18.3 Add navigation routes for GalleryScreen and AssetMediaScreen
    - Define navigation routes in app navigation graph
    - Add navigation from main menu to GalleryScreen
    - Add navigation from asset profile to AssetMediaScreen
    - _Requirements: 1.1-1.5, 2.1-2.7, 3.1-3.7_

  - [ ] 18.4 Integrate with existing MediaUploadManager
    - Update MediaUploadManager to use MediaGalleryRepository
    - Ensure automatic tagging works from all upload contexts
    - _Requirements: 6.1-6.7_

  - [ ]* 18.5 Write end-to-end integration tests
    - Test upload → auto-tagging → gallery display flow
    - Test search → select asset → view media → delete flow
    - Test filter → export → verify archive flow
    - Test offline → view cached media → online → sync flow
    - _Requirements: All_

- [ ] 19. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.


## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP delivery
- Each task references specific requirements for traceability
- Property-based tests validate universal correctness properties from the design document
- Unit tests validate specific examples and edge cases
- Checkpoints ensure incremental validation at key milestones
- All code examples use Kotlin as specified in the design document
- The implementation follows a bottom-up approach: data layer → domain layer → presentation layer → integration
- Database indices are critical for performance with large collections (thousands of items)
- LRU cache eviction ensures offline access stays within storage limits
- Automatic tagging during upload reduces manual categorization effort
- Filter persistence improves user experience across navigation sessions

## Test Configuration

Property-based tests use Kotest Property Testing with:
- Minimum 100 iterations per property test
- Each property test references its design document property number
- Tag format: `Feature: asset-multimedia-gallery, Property {number}: {property_text}`

## Performance Targets

- Initial gallery load: < 1 second
- Search query response: < 500ms
- Page load (20 items): < 300ms
- Cache lookup: < 50ms
- Thumbnail generation: < 200ms per image

## Key Files to Create/Modify

**Data Layer:**
- `data/local/entity/MediaItemEntity.kt`
- `data/local/entity/MediaTagEntity.kt`
- `data/local/entity/MediaCacheMetadataEntity.kt`
- `data/local/entity/GalleryFilterStateEntity.kt`
- `data/local/dao/MediaItemDao.kt`
- `data/local/dao/MediaTagDao.kt`
- `data/local/dao/MediaCacheMetadataDao.kt`
- `data/local/dao/GalleryFilterStateDao.kt`

**Domain Layer:**
- `domain/model/MediaItem.kt`
- `domain/model/MediaTag.kt`
- `domain/model/MediaFilter.kt`
- `domain/repository/MediaGalleryRepository.kt`
- `domain/cache/MediaCacheManager.kt`
- `domain/validation/TagValidator.kt`
- `domain/error/MediaErrors.kt`

**Presentation Layer:**
- `presentation/gallery/MediaGalleryViewModel.kt`
- `presentation/gallery/GalleryScreen.kt`
- `presentation/gallery/GalleryUiState.kt`
- `presentation/asset/AssetMediaScreen.kt`
- `presentation/viewer/FullScreenMediaViewer.kt`
- `presentation/video/VideoPlayerScreen.kt`

**Integration:**
- `data/upload/MediaUploadRequest.kt`
- `data/upload/MediaUploadManagerExtensions.kt`

**Test Files:**
- `test/property/MediaGalleryPropertyTests.kt`
- `test/repository/MediaGalleryRepositoryTest.kt`
- `test/cache/MediaCacheManagerTest.kt`
- `test/viewmodel/MediaGalleryViewModelTest.kt`
- `test/integration/MediaGalleryIntegrationTest.kt`
