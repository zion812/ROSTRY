# Media Upload Service

## Overview

The Media Upload Service provides production-ready media upload functionality with thumbnail generation, image compression, retry logic, and validation. This implementation completes Requirement 5 (Media Upload Service Completion) from the Production Readiness Gap Filling spec.

## Features

### 1. Core Upload Functionality
- **File Validation**: Validates file type, size, and dimensions before upload
- **Thumbnail Generation**: Creates 300x300px thumbnails maintaining aspect ratio
- **Image Compression**: Compresses images to 85% quality JPEG with max 2048px dimension
- **EXIF Preservation**: Maintains important EXIF data during compression
- **Separate Storage**: Stores thumbnails in separate Firebase Storage path

### 2. Thumbnail Generation

#### Image Thumbnails
- Efficient decoding with sample size calculation
- Maintains aspect ratio (300x300px max)
- Handles large images gracefully
- Falls back to placeholder on failure

#### Video Thumbnails
- Extracts first frame using MediaMetadataRetriever
- Maintains aspect ratio
- Falls back to placeholder on failure

### 3. Retry Logic
- **Max Retries**: 3 attempts with exponential backoff
- **Delays**: 1s, 2s, 4s between retries
- **Fallback**: Uses placeholder thumbnail after exhausting retries
- **Logging**: All failures logged via ErrorHandler

### 4. Integration Points

#### Circuit Breaker
- All Firebase Storage calls wrapped with circuit breaker
- Automatic fallback on service degradation
- Reports to DegradationManager

#### Validation Framework
- File type validation (jpg, jpeg, png, webp, mp4, 3gp)
- File size validation (10MB images, 100MB videos)
- Image dimension validation (100x100 to 8192x8192)
- Descriptive error messages

#### Error Handler
- All exceptions logged with context
- User-friendly error messages
- Automatic recovery strategies

## Architecture

```
MediaUploadService (IMediaUploadService)
├── upload(MediaUploadRequest): UploadResult
│   ├── Validates file with ValidationFramework
│   ├── Generates thumbnail with retry logic
│   ├── Compresses image (if applicable)
│   ├── Uploads to Firebase Storage via Circuit Breaker
│   └── Returns UploadResult.Success or Failure
│
├── generateThumbnail(File, MediaType): File
│   ├── Retries up to 3 times with exponential backoff
│   ├── Calls generateImageThumbnail or generateVideoThumbnail
│   └── Falls back to placeholder on failure
│
└── compressImage(File, quality): File
    ├── Limits max dimension to 2048px
    ├── Compresses to specified quality (default 85%)
    └── Preserves EXIF data
```

## Data Models

### MediaUploadRequest
```kotlin
data class MediaUploadRequest(
    val file: File,
    val mediaType: MediaType,
    val ownerId: String,
    val entityType: String,
    val entityId: String
)
```

### MediaType
```kotlin
enum class MediaType {
    IMAGE,
    VIDEO
}
```

### UploadResult
```kotlin
sealed class UploadResult {
    data class Success(
        val mediaUrl: String,
        val thumbnailUrl: String,
        val metadata: MediaMetadata
    ) : UploadResult()
    
    data class Failure(val error: Throwable) : UploadResult()
}
```

### MediaMetadata
```kotlin
data class MediaMetadata(
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val format: String,
    val duration: Int? = null
)
```

## Database Schema

### MediaMetadataEntity
```sql
CREATE TABLE media_metadata (
    mediaId TEXT PRIMARY KEY,
    originalUrl TEXT NOT NULL,
    thumbnailUrl TEXT NOT NULL,
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    sizeBytes INTEGER NOT NULL,
    format TEXT NOT NULL,
    duration INTEGER,
    compressionQuality INTEGER,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (mediaId) REFERENCES media_items(id) ON DELETE CASCADE
);

CREATE INDEX idx_media_metadata_mediaId ON media_metadata(mediaId);
```

## Usage Example

```kotlin
@Inject
lateinit var mediaUploadService: MediaUploadService

suspend fun uploadProductImage(imageFile: File, productId: String, userId: String) {
    val request = MediaUploadRequest(
        file = imageFile,
        mediaType = MediaType.IMAGE,
        ownerId = userId,
        entityType = "product",
        entityId = productId
    )
    
    when (val result = mediaUploadService.upload(request)) {
        is UploadResult.Success -> {
            // Use result.mediaUrl and result.thumbnailUrl
            println("Uploaded: ${result.mediaUrl}")
            println("Thumbnail: ${result.thumbnailUrl}")
            println("Dimensions: ${result.metadata.width}x${result.metadata.height}")
        }
        is UploadResult.Failure -> {
            // Handle error
            println("Upload failed: ${result.error.message}")
        }
    }
}
```

## Requirements Satisfied

- ✅ 5.1: Generate 300x300px thumbnails for images
- ✅ 5.2: Extract thumbnails from video first frame
- ✅ 5.3: Compress images to 85% quality
- ✅ 5.4: Retry thumbnail generation up to 3 times with exponential backoff
- ✅ 5.5: Use placeholder thumbnail after retry exhaustion
- ✅ 5.6: Validate image dimensions and file formats
- ✅ 5.7: Store thumbnails separately from original media
- ✅ 5.8: Verify thumbnail exists or default is assigned

## Testing

Unit tests are provided in `MediaUploadServiceTest.kt` covering:
- File validation
- Upload failure scenarios
- Service construction and interface compliance

## Future Enhancements

1. **Video Duration Extraction**: Extract actual video duration using MediaMetadataRetriever
2. **Progress Callbacks**: Add upload progress callbacks for UI updates
3. **Batch Upload**: Support uploading multiple files in a single operation
4. **Smart Compression**: Adjust compression quality based on image content
5. **Thumbnail Caching**: Cache generated thumbnails to avoid regeneration
