package com.rio.rostry.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.PostsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class ContentDiscoveryViewModel @Inject constructor(
    private val postsDao: PostsDao
) : ViewModel() {

    data class TrendingState(
        val topHashtags: List<Pair<String, Int>> = emptyList()
    )

    private val _trending = MutableStateFlow(TrendingState())
    val trending: StateFlow<TrendingState> = _trending.asStateFlow()

    init {
        observeTrending()
    }

    private fun observeTrending() {
        viewModelScope.launch {
            val sevenDaysMs = 7L * 24 * 60 * 60 * 1000
            val since = System.currentTimeMillis() - sevenDaysMs
            postsDao.streamTextsSince(since).collectLatest { list ->
                val counts = mutableMapOf<String, Int>()
                list.forEach { text ->
                    if (!text.isNullOrBlank()) {
                        HASH_REGEX.findAll(text).forEach { match ->
                            val tag = match.value.lowercase()
                            counts[tag] = (counts[tag] ?: 0) + 1
                        }
                    }
                }
                val top = counts.entries
                    .sortedByDescending { it.value }
                    .take(10)
                    .map { it.key to it.value }
                _trending.value = TrendingState(topHashtags = top)
            }
        }
    }

    companion object {
        private val HASH_REGEX = "#[A-Za-z0-9_]{2,}".toRegex()
    }
}
