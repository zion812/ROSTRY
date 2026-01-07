package com.rio.rostry.ui.enthusiast.cards

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.RoosterCardUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoosterCardViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product = _product.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    
    // Template selection (Comment 3)
    private val _selectedTemplate = MutableStateFlow<CardTemplate>(CardTemplate.Showstopper)
    val selectedTemplate: StateFlow<CardTemplate> = _selectedTemplate.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = productRepository.findById(productId)
                _product.value = result
            } finally {
                _loading.value = false
            }
        }
    }
    
    /**
     * Select a card template.
     */
    fun selectTemplate(template: CardTemplate) {
        _selectedTemplate.value = template
    }
    
    /**
     * Randomize template selection.
     */
    fun randomTemplate() {
        val templates = CardTemplate.all()
        val current = _selectedTemplate.value
        val filtered = templates.filter { it != current }
        _selectedTemplate.value = filtered.randomOrNull() ?: current
    }

    fun captureAndShare(view: View, context: Context) {
        // Capture the bitmap from the view
        try {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            
            val uri = RoosterCardUtils.saveBitmapToGallery(context, bitmap, "RoosterCard")
            if (uri != null) {
                RoosterCardUtils.shareImage(context, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

