package com.rio.rostry.domain.error

sealed class MediaError : Exception() {

    sealed class ValidationError : MediaError() {
        object InvalidAssetId : ValidationError()
        object InvalidAgeGroup : ValidationError()
        object InvalidSourceType : ValidationError()
        object UnsupportedMediaFormat : ValidationError()
        object EmptyMediaFile : ValidationError()
    }

    sealed class NetworkError : MediaError() {
        object UploadFailed : NetworkError()
        object DownloadFailed : NetworkError()
        object QuotaExceeded : NetworkError()
        object Timeout : NetworkError()
        object NoConnection : NetworkError()
    }

    sealed class StorageError : MediaError() {
        object InsufficientSpace : StorageError()
        object PermissionDenied : StorageError()
        object CacheCorruption : StorageError()
    }

    sealed class IntegrityError : MediaError() {
        object OrphanedMedia : IntegrityError()
        object MissingThumbnail : IntegrityError()
        object CacheInconsistency : IntegrityError()
    }
}
