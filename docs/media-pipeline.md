# Media Upload & Processing Pipeline

End-to-end media handling across capture, compression, upload, caching, and display.

## Architecture

- `MediaManager.kt` coordinates selection, validation, and lifecycle.
- `MediaUploader.kt` streams uploads with resumable logic and progress.
- `ImageCompressor.kt` (and `CompressionUtils.kt`) reduces size while preserving quality.
- Firebase Storage is the canonical store; URLs cached locally.
- `CoilModule.kt` configures image loading, caching, and transformations.

## Workflow

1. Validate file type, size, and dimensions.
2. Compress image/video where applicable.
3. Upload with progress reporting and retry.
4. Persist references in database; update UI via Flow.

## Validation & Moderation

- Content checks, EXIF scrubbing, and optional moderation hooks prior to publish.

## Paths & Caching

- `file_paths.xml` centralizes directories for media cache and exports.
- Offline caching policies and eviction tuned for low-memory devices.

## Video

- ExoPlayer integration for playback and adaptive streaming.

## Performance

- Backpressure-aware pipelines, memory pools, and thumbnail strategies.

## Errors

- Structured retry with exponential backoff and user feedback.
