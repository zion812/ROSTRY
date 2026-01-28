package com.rio.rostry.ui.enthusiast.showcase

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.domain.showcase.ShowcaseCard
import com.rio.rostry.domain.showcase.ShowcaseCardGenerator
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * ViewModel for ShowcaseCardPreviewScreen.
 * Generates and manages sharing of showcase cards.
 */
@HiltViewModel
class ShowcaseCardViewModel @Inject constructor(
    private val showcaseCardGenerator: ShowcaseCardGenerator,
    private val productDao: ProductDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val showRecordRepository: com.rio.rostry.data.repository.ShowRecordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val productId: String = savedStateHandle["productId"] ?: ""
    
    private val _uiState = MutableStateFlow<ShowcaseCardUiState>(ShowcaseCardUiState.Loading)
    val uiState: StateFlow<ShowcaseCardUiState> = _uiState.asStateFlow()
    
    private val _config = MutableStateFlow(com.rio.rostry.domain.showcase.ShowcaseConfig())
    val config: StateFlow<com.rio.rostry.domain.showcase.ShowcaseConfig> = _config.asStateFlow()
    
    private var currentCard: ShowcaseCard? = null
    
    init {
        if (productId.isNotBlank()) {
            generateCard()
        } else {
            _uiState.value = ShowcaseCardUiState.Error("No bird selected")
        }
    }
    
    fun updateConfig(newConfig: com.rio.rostry.domain.showcase.ShowcaseConfig) {
        _config.value = newConfig
        generateCard()
    }
    
    private fun generateCard() {
        viewModelScope.launch {
            _uiState.value = ShowcaseCardUiState.Loading
            
            try {
                val bird = productDao.findById(productId)
                if (bird == null) {
                    _uiState.value = ShowcaseCardUiState.Error("Bird not found")
                    return@launch
                }
                
                // Get vaccination count
                val vaccinationCount = vaccinationRecordDao.getRecordsByProduct(productId).size
                
                // Get show stats
                val statsResource = showRecordRepository.getStats(productId)
                val stats = if (statsResource is Resource.Success) statsResource.data ?: emptyList() else emptyList()
                
                // Generate card
                when (val result = showcaseCardGenerator.generateCard(
                    bird = bird,
                    config = _config.value,
                    stats = stats,
                    vaccinationCount = vaccinationCount
                )) {
                    is Resource.Success -> {
                        currentCard = result.data
                        _uiState.value = ShowcaseCardUiState.Success(
                            cardBitmap = result.data!!.bitmap,
                            birdName = bird.name,
                            file = result.data.file
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = ShowcaseCardUiState.Error(result.message ?: "Failed to generate card")
                    }
                    is Resource.Loading -> {
                        // Keep loading
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate showcase card")
                _uiState.value = ShowcaseCardUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
    
    fun regenerate() {
        generateCard()
    }
    
    fun share(context: Context) {
        val card = currentCard ?: return
        showcaseCardGenerator.shareCard(context, card)
    }
    
    fun shareToWhatsApp(context: Context) {
        val state = _uiState.value as? ShowcaseCardUiState.Success ?: return
        
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                state.file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(intent)
        } catch (e: Exception) {
            // WhatsApp not installed, use generic share
            Timber.w(e, "WhatsApp not available, using generic share")
            share(context)
        }
    }
    
    fun shareToInstagram(context: Context) {
        val state = _uiState.value as? ShowcaseCardUiState.Success ?: return
        
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                state.file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                setPackage("com.instagram.android")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(intent)
        } catch (e: Exception) {
            // Instagram not installed, use generic share
            Timber.w(e, "Instagram not available, using generic share")
            share(context)
        }
    }
    
    fun saveToGallery(context: Context) {
        val state = _uiState.value as? ShowcaseCardUiState.Success ?: return
        
        viewModelScope.launch {
            try {
                val filename = "ROSTRY_Showcase_${System.currentTimeMillis()}.png"
                
                val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ROSTRY")
                    }
                    
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    
                    uri?.let { context.contentResolver.openOutputStream(it) }
                } else {
                    val directory = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "ROSTRY"
                    )
                    if (!directory.exists()) directory.mkdirs()
                    
                    val file = File(directory, filename)
                    FileOutputStream(file)
                }
                
                outputStream?.use {
                    state.cardBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                
                Toast.makeText(context, "Saved to gallery!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Timber.e(e, "Failed to save to gallery")
                Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

sealed class ShowcaseCardUiState {
    data object Loading : ShowcaseCardUiState()
    data class Success(
        val cardBitmap: Bitmap,
        val birdName: String,
        val file: File
    ) : ShowcaseCardUiState()
    data class Error(val message: String) : ShowcaseCardUiState()
}
