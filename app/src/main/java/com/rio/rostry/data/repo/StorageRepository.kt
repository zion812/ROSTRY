package com.rio.rostry.data.repo

import android.net.Uri

import android.graphics.Bitmap

interface StorageRepository {
    suspend fun uploadImage(uri: Uri): String?
    suspend fun uploadBitmap(bitmap: Bitmap): String?
}
